package com.dizertatie.Middleware.UniversalControllers;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public
class UniversalController
{
    @Autowired
    private Environment env;

    @GetMapping("/test")
    Mono<String> test() {
        return tool(Mono.just(env.getProperty("middleware.type")));
    }

    private <T> Mono<T> tool(Mono<T> obj) {
        if (Objects.equals(env.getProperty("middleware.type"), "SERVLET")) {
            return Mono.just(obj.block());
        }
        return obj;
    }
}
