package com.dizertatie.Middleware.Tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class FluidIOMapping {
    @Autowired
    private Environment autowiredEnvironment;

    private static Environment environment;

    @PostConstruct
    private void init() {
        environment = autowiredEnvironment;
    }

    public static <T> Mono<T> fluidMap(Mono<T> obj) {
        if (Objects.equals(environment.getProperty("middleware.type"), "SERVLET")) {
            return Mono.just(obj.block());
        }
        return obj;
    }

//    TODO: Create fluidMapping with 2 different approaches on blocking and non-blocking
}
