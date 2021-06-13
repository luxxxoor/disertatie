package com.disertatie.Middleware.Tools;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import com.google.common.collect.EvictingQueue;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

@Component
public class FluidIOMapping {
    @Autowired
    private Environment autowiredEnvironment;

    private static Environment environment;

    @PostConstruct
    private void init() {
        environment = autowiredEnvironment;
    }

    private static String getRequestKey(long depth) {
        // return obj.getClass().getEnclosingMethod().getName();
        StackWalker walker = StackWalker.getInstance();
        var possibleFrame = walker.walk(s -> s.skip(depth).findFirst());
        if (possibleFrame.isEmpty())
            throw new IndexOutOfBoundsException("Stack is not that deep");
        var frame = possibleFrame.get();
        
        return frame.getClassName() + "::" + frame.getMethodName();
        // return exchange.getRequestMethod() + " : " + exchange.getRequestPath();
    }
    private static String getRequestKey() {
        return getRequestKey(4);
    }

    private static final Map<String, MovingRecord> recordedTimes = new HashMap<>();
    private static Triplet<Instant, RequestType, String> startRecording() {
        String requestKey = getRequestKey();
        System.out.println(requestKey);
        if (! recordedTimes.containsKey(requestKey))
            recordedTimes.put(requestKey, new MovingRecord(3, 5));

        MovingRecord record = recordedTimes.get(requestKey);
        var recording = record.startRecording();

        return Triplet.with(
                recording.getValue0(),
                recording.getValue1(),
                requestKey);
    }
    private static Duration endRecording(Triplet<Instant, RequestType, String> identifier) {
        var requestKey = identifier.getValue2();
        var recording = Pair.with(identifier.getValue0(), identifier.getValue1());
        MovingRecord record = recordedTimes.get(requestKey);

        return record.endRecording(recording);
    }

    public static <T> Mono<T> fluidHandle(Mono<T> obj) {
        final var identifier = startRecording();
        var requestType = identifier.getValue1();

        Mono<T> modifiedMono = obj;
        if (requestType == RequestType.BLOCKING) {
            modifiedMono = Mono.just(obj.block());
        }

        modifiedMono = modifiedMono.doOnTerminate(() -> {
            endRecording(identifier);
        });

        return modifiedMono;
    }

    public static <T> Mono<T> fluidSwitch(Supplier<T> onBlocking, Supplier<Mono<T>> onNonblocking) {
        final var identifier = startRecording();
        var requestType = identifier.getValue1();

        Mono<T> modifiedMono;
        if (requestType == RequestType.BLOCKING) {
            modifiedMono = Mono.just(onBlocking.get());
        } else {
            modifiedMono = onNonblocking.get();
        }

        modifiedMono = modifiedMono.doOnTerminate(() -> {
            endRecording(identifier);
        });

        return modifiedMono;
    }
}

enum RequestType {
    NONE,
    BLOCKING,
    NONBLOCKING;

    private RequestType opposite;
    static {
        NONE.opposite = NONE;
        BLOCKING.opposite = NONBLOCKING;
        NONBLOCKING.opposite = BLOCKING;
    }

    public RequestType oppositeType() {
        return opposite;
    }
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
        if (numberOfSameRequests == sameRequestsLimit) {
            return Pair.with(startTime, lastRequestType.oppositeType());
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
