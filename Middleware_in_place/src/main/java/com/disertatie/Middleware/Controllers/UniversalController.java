package com.disertatie.Middleware.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.disertatie.Middleware.Tools.FluidIOMapping.fluidHandle;

@RestController
public class UniversalController
{

    @GetMapping("/test")
    Mono<String> fuckfuckfuckfuck() {
        Mono<String> mono = Mono.just("test");

        return fluidHandle(mono);
    }
}
