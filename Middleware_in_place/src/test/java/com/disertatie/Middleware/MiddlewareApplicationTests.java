package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

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
		Supplier<Void> blocking = () -> {
			blockingCounter.addAndGet(1);
			return null;
		};
		Supplier<Mono<Void>> nonblocking = () -> {
			nonblockingCounter.addAndGet(1);
			return Mono.just(null);
		};

		for (int i = 0; i < 100; ++i) {
			FluidIO.fluidSwitch(blocking, nonblocking);
		}
	}
}
