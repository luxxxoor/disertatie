package com.dizertatie.Middleware.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class NettyController
{
    @RequestMapping("/test_netty")
    String test() {
        return "Netty";
    }
}
