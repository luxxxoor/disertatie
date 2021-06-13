package com.disertatie.Middleware.Tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Objects;
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

    public static <T> Mono<T> fluidHandle(Mono<T> obj) {
        var middlewareType = environment.getProperty("middleware.type");
        System.out.println("From fluidHandle: " + middlewareType);

        if (Objects.equals(middlewareType, "SERVLET")) {
            return Mono.just(obj.block());
        }
        return obj;
    }

    public static <T> Mono<T> fluidSwitch(Supplier<T> onBlocking, Supplier<Mono<T>> onNonblocking) {
        if (Objects.equals(environment.getProperty("middleware.type"), "SERVLET")) {
            return Mono.just(onBlocking.get());
        }
        return onNonblocking.get();
    }
//    TODO: Create fluidMapping with 2 different approaches on blocking and non-blocking
}
