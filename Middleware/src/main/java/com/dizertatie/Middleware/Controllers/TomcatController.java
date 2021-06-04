package com.dizertatie.Middleware.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TomcatController
{
    @RequestMapping("/test")
    String test() {
        return "Tomcat";
    }
}
