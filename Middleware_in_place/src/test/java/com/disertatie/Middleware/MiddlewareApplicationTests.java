package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

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
			FluidIO.fluidSwitch(blocking, nonblocking);
		}

		System.out.println(blockingCounter.get());
		System.out.println(nonblockingCounter.get());
	}
}
