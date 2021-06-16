package com.disertatie.Middleware.Tools;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class FluidIO {
    @Value("${fluid.request-per-average}")
    private Integer requestPerAverageProperty;

    @Value("${fluid.same-requests-limit}")
    private Integer sameRequestsLimitProperty;

    // Default values
    static Integer requestPerAverage = 3;
    static Integer sameRequestsLimit = 5;

    @PostConstruct
    private void init() {
        if (requestPerAverageProperty != null && requestPerAverageProperty > requestPerAverage) {
            requestPerAverage = requestPerAverageProperty;
        }

        if (sameRequestsLimitProperty != null && sameRequestsLimitProperty > sameRequestsLimit) {
            sameRequestsLimit = sameRequestsLimitProperty;
        }
    }

    private static String getRequestKey(long depth) {
        StackWalker walker = StackWalker.getInstance();
        var possibleFrame = walker.walk(s -> s.skip(depth).findFirst());
        if (possibleFrame.isEmpty())
            throw new IndexOutOfBoundsException("Stack is not that deep");
        var frame = possibleFrame.get();
        
        return frame.getClassName() + "::" + frame.getMethodName();
    }
    private static String getRequestKey() {
        return getRequestKey(4);
    }

    private static final Map<String, MovingRecord> recordedTimes = new HashMap<>();
    private static Triplet<Instant, RequestType, String> startRecording() {
        String requestKey = getRequestKey();
        System.out.println(requestKey);
        if (! recordedTimes.containsKey(requestKey))
            recordedTimes.put(requestKey, new MovingRecord(requestPerAverage, sameRequestsLimit));

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
            modifiedMono = Mono.just(obj.toProcessor().block());
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
