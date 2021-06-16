package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIO;
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
	void contextLoads() {
	}

	@Test
	void testForStarvation() {
		AtomicInteger blockingCounter = new AtomicInteger(0);
		AtomicInteger nonblockingCounter = new AtomicInteger(0);
		Supplier<Object> blocking = () -> {
			blockingCounter.addAndGet(1);
			try {
				Thread.sleep(300);
			} catch (InterruptedException ignored) {
			}
			return new Object();
		};
		Supplier<Mono<Object>> nonblocking = () -> {
			nonblockingCounter.addAndGet(1);
			return Mono.just(new Object());
		};

		for (int i = 0; i < 100; ++i) {
			FluidIO.fluidSwitch(blocking, nonblocking).subscribe();
		}

		assert blockingCounter.get() < nonblockingCounter.get();
		assert blockingCounter.get() > 0;
	}

	@Test
	void testForBalance() {
		AtomicInteger blockingCounter = new AtomicInteger(0);
		AtomicInteger nonblockingCounter = new AtomicInteger(0);

		Supplier<Object> blocking = () -> {
			blockingCounter.addAndGet(1);

			return new Object();
		};
		Supplier<Mono<Object>> nonblocking = () -> {
			nonblockingCounter.addAndGet(1);
			try {
				Thread.sleep(300);
			} catch (InterruptedException ignored) {
			}

			return Mono.just(new Object());
		};

		Supplier<Object> blocking2 = () -> {
			blockingCounter.addAndGet(1);
			try {
				Thread.sleep(300);
			} catch (InterruptedException ignored) {
			}

			return new Object();
		};
		Supplier<Mono<Object>> nonblocking2 = () -> {
			nonblockingCounter.addAndGet(1);

			return Mono.just(new Object());
		};

		for (int i = 0; i < 6; ++i) {
			FluidIO.fluidSwitch(blocking, nonblocking).subscribe();
		}

		assert blockingCounter.get() > nonblockingCounter.get();

		for (int i = 0; i < 30; ++i) {
			FluidIO.fluidSwitch(blocking2, nonblocking2).subscribe();
		}

		assert nonblockingCounter.get() > blockingCounter.get();

		for (int i = 0; i < 60; ++i) {
			FluidIO.fluidSwitch(blocking, nonblocking).subscribe();
		}

		assert blockingCounter.get() > nonblockingCounter.get();
	}

    @Test
    void testMovingRecordRandom() {
        int tries = 1000;
        Random random = new Random();

        MovingRecord m = new MovingRecord(10, 16);
        List<Pair<Instant, RequestType>> recordings = new ArrayList<>();
        List<RequestType> accumulatedTypes = new ArrayList<>();

        for (int i=0; i<tries; ++i) {
            boolean newRecording = random.nextBoolean();

            if (recordings.isEmpty() || newRecording) {
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
        
        accumulatedTypes.forEach(System.out::println);
        assert ! accumulatedTypes.isEmpty();
    }
}
