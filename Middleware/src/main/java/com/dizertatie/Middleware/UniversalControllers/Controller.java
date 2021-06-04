package com.dizertatie.Middleware.UniversalControllers;

import com.dizertatie.Middleware.Tools.FluidIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FluidIO
class Controller
{
    @Autowired
    private Environment env;

    @GetMapping("/test")
    String test() {
        return env.getProperty("middleware.type");
    }
}
