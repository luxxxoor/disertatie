package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.MovingRecord;
import com.disertatie.Middleware.Tools.RequestType;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@SpringBootTest
class MiddlewareApplicationTests {

    @Test
    void testMovingRecordRandom() {
        int tries = 10000;
        double lowerBound = 0.45,
               upperBound = 0.55;
        Random random = new Random();

        MovingRecord m = new MovingRecord(10, 16);
        List<Pair<Instant, RequestType>> recordings = new ArrayList<>();
        List<RequestType> accumulatedTypes = new ArrayList<>();

        for (int i=0; i<tries;) {
            boolean newRecording = random.nextBoolean();

            if (recordings.isEmpty() || newRecording) {
                i++;
                var recording = m.startRecording();
                accumulatedTypes.add(recording.getValue1());
                recordings.add(recording);
            } else {
                var randIdx = random.nextInt(recordings.size());
                var recording = recordings.get(randIdx);
                recordings.remove(recording);

                m.endRecording(recording);
            }
        }
        
        // accumulatedTypes.forEach(System.out::println);
        assert ! accumulatedTypes.isEmpty();
        assert accumulatedTypes.size() == tries;

        long blockingCount = accumulatedTypes
                                .stream()
                                .filter(x -> x.equals(RequestType.BLOCKING))
                                .count();
        long nonblockingCount = accumulatedTypes
                                    .stream()
                                    .filter(x -> x.equals(RequestType.NONBLOCKING))
                                    .count();

        double blockingChance = (double)blockingCount / tries;
        double nonblockingChance = (double)nonblockingCount / tries;

        assert blockingChance > lowerBound && blockingChance < upperBound;
        assert nonblockingChance > lowerBound && nonblockingChance < upperBound;
    }
}
