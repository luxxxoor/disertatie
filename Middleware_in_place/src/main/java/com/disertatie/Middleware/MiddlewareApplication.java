package com.disertatie.Middleware;

import java.net.URI;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import io.undertow.Undertow;
import io.undertow.Handlers;

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class MiddlewareApplication {
    public static void main(String[] args) {
        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                            .web(WebApplicationType.REACTIVE);

        nettyBuilder.run(args);
    }

    private static SpringApplicationBuilder makeBuilder(Integer port, Class<?>... sources) {
        return new SpringApplicationBuilder(MiddlewareApplication.class)
                .child(sources)
                .properties(String.format("server.port=%d", port));
    }
}
