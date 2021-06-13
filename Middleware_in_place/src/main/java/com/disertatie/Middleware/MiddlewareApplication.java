package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIOReverseProxy;

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
        var tomcatBuilder = makeBuilder(8080, MiddlewareApplication.class)
                .properties("middleware.type=SERVLET")
                .web(WebApplicationType.SERVLET);

        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                .properties("middleware.type=REACTIVE")
                .web(WebApplicationType.REACTIVE);

        tomcatBuilder.run(args);
        nettyBuilder.run(args);

        FluidIOReverseProxy fluidIOReverseProxy = new FluidIOReverseProxy(
                URI.create("http://127.0.0.1:8080"),
                URI.create("http://127.0.0.1:8081"));

        Undertow reverseProxy = Undertow.builder()
                .addHttpListener(8083, "localhost")
                .setHandler(Handlers.proxyHandler(fluidIOReverseProxy))
                .build();
        reverseProxy.start();
    }

    private static SpringApplicationBuilder makeBuilder(Integer port, Class<?>... sources) {
        return new SpringApplicationBuilder(MiddlewareApplication.class)
                .child(sources)
                .properties(String.format("server.port=%d", port));
    }
}
