package com.dizertatie.Middleware.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TomcatController
{
    @RequestMapping("/test_tomcat")
    String test() {
        return "Tomcat";
    }
}
