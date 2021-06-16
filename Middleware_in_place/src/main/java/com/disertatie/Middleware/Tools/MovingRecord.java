package com.disertatie.Middleware.Tools;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Random;

import com.google.common.collect.EvictingQueue;

import org.javatuples.Pair;

public class MovingRecord {
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
        // System.out.println("Average time (blocking): " + averageForBlocking.toNanos() + "ns");
        // System.out.println("Average time (nonblocking): " + averageForNonBlocking.toNanos() + "ns");

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

        // System.out.println(numberOfSameRequests + " / " + sameRequestsLimit);
        if (numberOfSameRequests.compareTo(sameRequestsLimit) >= 0) {
            return Pair.with(startTime, lastRequestType.oppositeType());
        }

        return Pair.with(startTime, getFasterApproachType());
    }

    synchronized public Duration endRecording(Pair<Instant, RequestType> recording) {
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
