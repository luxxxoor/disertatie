package com.disertatie.Middleware.Tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Supplier;

@Component
public class RpFluidIO {
    @Autowired
    private Environment environment;

    public <T> Mono<T> fluidHandle(Mono<T> obj) {
        if (Objects.equals(environment.getProperty("middleware.type"), "BLOCKING")) {
            return Mono.just(obj.block());
        }
        return obj;
    }

    public <T> Mono<T> fluidSwitch(Supplier<T> onBlocking, Supplier<Mono<T>> onNonblocking) {
        if (Objects.equals(environment.getProperty("middleware.type"), "BLOCKING")) {
            return Mono.just(onBlocking.get());
        }
        return onNonblocking.get();
    }
}
