package com.dizertatie.Middleware.Controllers;

import com.dizertatie.Middleware.Tool.FluidIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FluidIO
@RestController
class Controller implements ControllerWithValue<String>
{
    @Autowired
    private Environment env;
    
    private String value;

    @RequestMapping("/test")
    String test() {
        return env.getProperty("middleware.server") + value;
    }

	@Override
	public void setValue(String value) {
        this.value = value;
	}
}
