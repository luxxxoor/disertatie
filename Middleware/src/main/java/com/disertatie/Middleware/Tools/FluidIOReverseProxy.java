package com.disertatie.Middleware.Tools;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.EvictingQueue;

import org.javatuples.Pair;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;


/**
 * Start the ReverseProxy with an ImmutableMap of matching endpoints and a default
 *
 * Example:
 * mapping: ImmutableMap("api" -> "http://some-domain.com")
 * default: "http://default-domain.com"
 *
 * Request 1: localhost:8080/foo -> http://default-domain.com/foo
 * Request 2: localhost:8080/api/bar -> http://some-domain.com/bar
 */
public class FluidIOReverseProxy implements ProxyClient {
    private static final ProxyTarget TARGET = new ProxyTarget() {};

    private final UndertowClient client;
    private final URI servletUri, reactiveUri;
    private final Map<String, MovingRecord> recordedTimes = new HashMap<>();

    public FluidIOReverseProxy(URI servletUri, URI reactiveUri) {
        this.client = UndertowClient.getInstance();
        this.servletUri = servletUri;
        this.reactiveUri = reactiveUri;
    }

    @Override
    public ProxyTarget findTarget(HttpServerExchange exchange) {
        return TARGET;
    }

    private String getRequestKey(HttpServerExchange exchange) {
        return exchange.getRequestMethod() + " : " + exchange.getRequestPath();
    }

    @Override
    public void getConnection(
            ProxyTarget target, 
            HttpServerExchange exchange, 
            ProxyCallback<ProxyConnection> callback, 
            long timeout, 
            TimeUnit timeUnit) 
    {

        String requestKey = getRequestKey(exchange);
        if (! recordedTimes.containsKey(requestKey))
            recordedTimes.put(requestKey, new MovingRecord(3, 5));

        MovingRecord record = recordedTimes.get(requestKey);
        var recording = record.startRecording();
        var requestType = recording.getValue1();

        URI targetUri = reactiveUri;
        if (requestType == RequestType.BLOCKING)
            targetUri = servletUri;

        client.connect(
                new ConnectNotifier(callback, exchange, requestKey, recording),
                targetUri,
                exchange.getIoThread(),
                exchange.getConnection().getByteBufferPool(),
                OptionMap.EMPTY);
    }

    private final class ConnectNotifier implements ClientCallback<ClientConnection> {
        private final ProxyCallback<ProxyConnection> callback;
        private final HttpServerExchange exchange;
        private final String requestKey;
        private final Pair<Instant, RequestType> recording;

        private ConnectNotifier(
                ProxyCallback<ProxyConnection> callback, 
                HttpServerExchange exchange, 
                String requestKey, 
                Pair<Instant, RequestType> recording) {
            this.callback = callback;
            this.exchange = exchange;
            this.requestKey = requestKey;
            this.recording = recording;
        }

        @Override
        public void completed(final ClientConnection connection) {
            final ServerConnection serverConnection = exchange.getConnection();
            serverConnection.addCloseListener(serverConnection1 -> IoUtils.safeClose(connection));
            callback.completed(exchange, new ProxyConnection(connection, "/"));

            var duration = recordedTimes.get(requestKey).endRecording(recording);
            System.out.println(this.requestKey + " " + 
                               recording.getValue1().toString() + " " + 
                               duration.toNanos() + "ns");
        }

        @Override
        public void failed(IOException e) {
            callback.failed(exchange);
        }
    }


    enum RequestType {
        NONE,
        BLOCKING,
        NONBLOCKING
    }

    class MovingRecord {
        private EvictingQueue<Duration> blocking, nonblocking;
        private Integer sameRequestsLimit,
                numberOfSameRequests = 0;
        private RequestType lastRequestType = RequestType.NONE;
        private Random random = new Random();

        public MovingRecord(Integer requestsPerAverage, Integer sameRequestsLimit) {
            blocking = EvictingQueue.create(requestsPerAverage);
            nonblocking = EvictingQueue.create(requestsPerAverage);

            this.sameRequestsLimit = sameRequestsLimit;
        }

        public RequestType getSuggestedRequestType() {
            return lastRequestType;
        }

        private RequestType getFasterApproachType() {
            Duration averageForBlocking = averageDuration(blocking);
            Duration averageForNonBlocking = averageDuration(nonblocking);
            System.out.println("Average time (blocking): " + averageForBlocking.toNanos() + "ns");
            System.out.println("Average time (nonblocking): " + averageForNonBlocking.toNanos() + "ns");

            if (averageForBlocking.equals(averageForNonBlocking)) {
                return random.nextBoolean() ? RequestType.BLOCKING : RequestType.NONBLOCKING;
            }

            return
                    averageForBlocking.compareTo(averageForNonBlocking) < 0 ?
                            RequestType.BLOCKING :
                            RequestType.NONBLOCKING;
        }

        private RequestType getOtherRequestType(RequestType type) {
            switch (type) {
                case BLOCKING: return RequestType.NONBLOCKING;
                case NONBLOCKING: return RequestType.BLOCKING;

                default:
                    return RequestType.NONE;
            }
        }

        private Duration averageDuration(
                EvictingQueue<Duration> durations,
                TemporalUnit unit)
        {
            if (durations.isEmpty()) return Duration.ZERO;

            Long totalTime = durations
                    .stream()
                    .map(d -> d.get(unit))
                    .reduce(Long.valueOf(0), Long::sum);

            return Duration.of(totalTime/durations.size(), unit);
        }
        private Duration averageDuration(EvictingQueue<Duration> durations) {
            return averageDuration(durations, ChronoUnit.NANOS);
        }

        public Pair<Instant, RequestType> startRecording() {
            var startTime = Instant.now();

            System.out.println(numberOfSameRequests + " / " + sameRequestsLimit);
            if (numberOfSameRequests.compareTo(sameRequestsLimit) >= 0) {
                return Pair.with(startTime, getOtherRequestType(lastRequestType));
            }

            return Pair.with(startTime, getFasterApproachType());
        }

        public Duration endRecording(Pair<Instant, RequestType> recording) {
            Instant startTime = recording.getValue0();
            RequestType requestType = recording.getValue1();
            Duration requestTime = Duration.between(startTime, Instant.now());

            if (lastRequestType == RequestType.NONE || lastRequestType != requestType) {
                lastRequestType = requestType;
                numberOfSameRequests = 1;
            } else /* lastRequestType == requestType */  {
                numberOfSameRequests++;
            }

            EvictingQueue<Duration> queue = this.blocking;
            if (requestType == RequestType.NONBLOCKING)
                queue = this.nonblocking;

            queue.add(requestTime);
            return requestTime;
        }

    }
}
