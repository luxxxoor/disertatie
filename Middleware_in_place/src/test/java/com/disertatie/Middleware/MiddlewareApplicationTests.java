package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
}
