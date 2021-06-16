package com.disertatie.Middleware.Tools;

import java.io.*;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.yaml.snakeyaml.Yaml;

public class FluidIOReverseProxy implements ProxyClient {
    private static final ProxyTarget TARGET = new ProxyTarget() {};

    private final UndertowClient client;
    private final URI servletUri, reactiveUri;
    private final Map<String, MovingRecord> recordedTimes = new HashMap<>();

    private Integer requestPerAverage = 3;
    private Integer sameRequestsLimit = 5;

    public FluidIOReverseProxy(URI servletUri, URI reactiveUri) {
        this.client = UndertowClient.getInstance();
        this.servletUri = servletUri;
        this.reactiveUri = reactiveUri;

        try {
            InputStreamReader inputStream = new InputStreamReader(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("application.yaml"));
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            Integer requestPerAverageProperty = (Integer) ((Map)data.get("fluid")).get("request-per-average");
            Integer sameRequestsLimitProperty = (Integer) ((Map)data.get("fluid")).get("same-requests-limit");

            if (requestPerAverageProperty != null && requestPerAverageProperty > requestPerAverage) {
                requestPerAverage = requestPerAverageProperty;
            }

            if (sameRequestsLimitProperty != null && sameRequestsLimitProperty > sameRequestsLimit) {
                sameRequestsLimit = sameRequestsLimitProperty;
            }
        } catch (Exception ignored) {
        }
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
        System.out.println(requestPerAverage);
        System.out.println(sameRequestsLimit);
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

}
