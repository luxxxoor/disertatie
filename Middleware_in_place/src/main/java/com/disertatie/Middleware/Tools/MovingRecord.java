package com.disertatie.Middleware.Tools;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import com.google.common.collect.EvictingQueue;

import org.javatuples.Pair;

public class MovingRecord {
    private Mutable<Long> blockingAverage = new Mutable<>(0l),
                           nonblockingAverage = new Mutable<>(0l);
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
        Long averageForBlocking = blockingAverage.get();
        Long averageForNonBlocking = nonblockingAverage.get();

        if (averageForBlocking.equals(averageForNonBlocking)) {
            return random.nextBoolean() ? RequestType.BLOCKING : RequestType.NONBLOCKING;
        }

        return
                averageForBlocking.compareTo(averageForNonBlocking) < 0 ?
                        RequestType.BLOCKING :
                        RequestType.NONBLOCKING;
    }

    private void addToAverage(Duration duration, RequestType type) {
        EvictingQueue<Duration> queue = blocking;
        Mutable<Long> averageMutable = blockingAverage;
        if (type == RequestType.NONBLOCKING) {
            queue = nonblocking;
            averageMutable = nonblockingAverage;
        }
        Integer n = queue.size();

        Long average = averageMutable.get();
        Long newTime = duration.toNanos();
        if (queue.remainingCapacity() == 0) {
            Long removedTime = queue.peek().toNanos();
            averageMutable.set(average - (removedTime/n) + (newTime/n));
        } else {
            averageMutable.set((average*n + newTime) / (n+1));
        }

        synchronized(this) { queue.add(duration); }
    }

    public Pair<Instant, RequestType> startRecording() {
        var startTime = Instant.now();

        if (numberOfSameRequests.compareTo(sameRequestsLimit) >= 0) {
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


        addToAverage(requestTime, requestType);
        return requestTime;
    }

    static class Mutable<T> {
        protected T value;

        public Mutable(T value) {
            set(value);
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }

    }
}
