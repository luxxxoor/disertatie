package com.dizertatie.Middleware;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MiddlewareApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MiddlewareApplication.class)
                .properties("server.port=8080")
                .properties("middleware.server=SERVLET")
                .web(WebApplicationType.SERVLET)
                .run(args);
        new SpringApplicationBuilder(MiddlewareApplication.class)
                .properties("server.port=8081")
                .properties("middleware.server=REACTIVE")
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
