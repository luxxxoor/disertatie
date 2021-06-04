package com.dizertatie.Middleware.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TomcatController
{
    @Autowired
    private Environment env;

    @RequestMapping("/test")
    String test() {
        return env.getProperty("middleware.server");
    }
}
