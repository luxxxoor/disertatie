package com.dizertatie.Middleware.UniversalControllers;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.dizertatie.Middleware.Tools.FluidIOMapping.fluidHandle;

@RestController
public class UniversalController
{
    @Autowired
    private Environment env;

    @GetMapping("/test")
    Mono<String> test() {
        Mono<String> mono = Mono.just(env.getProperty("middleware.type"));

        return fluidHandle(mono);
    }
}
