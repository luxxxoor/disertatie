package com.dizertatie.Middleware.Controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@ConditionalOnProperty(
    value = "middleware.type",
    havingValue = "REACTIVE"
)
public class NettyController extends AbstractController
{
    @GetMapping("/test1")
    Mono<String> test() {
        return super._test();
    }
}
