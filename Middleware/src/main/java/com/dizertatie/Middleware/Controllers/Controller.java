package com.dizertatie.Middleware.Controllers;

import com.dizertatie.Middleware.Tool.FluidIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FluidIO
@RestController
class Controller
{
    @Autowired
    private Environment env;

    @RequestMapping("/test")
    String test() {
        return env.getProperty("middleware.server");
    }
}
