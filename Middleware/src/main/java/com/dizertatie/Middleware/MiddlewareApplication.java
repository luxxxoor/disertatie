package com.dizertatie.Middleware;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MiddlewareApplication {

	public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MiddlewareApplication.class);
        builder.web(WebApplicationType.REACTIVE);

        builder.run(args);
	}

}
