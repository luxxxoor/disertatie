package com.dizertatie.Middleware.Controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(
    value = "middleware.type",
    havingValue = "SERVLET"
)
public class TomcatController extends AbstractController
{
    @GetMapping("/test1")
    String test() {
        return super._test().block();
    }
}
