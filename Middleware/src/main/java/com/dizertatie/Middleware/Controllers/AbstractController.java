package com.dizertatie.Middleware.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;

public abstract class AbstractController {
    @Autowired
    private Environment env;

    protected Mono<String> _test() {
        return Mono.just(env.getProperty("middleware.type"));
    }
}
