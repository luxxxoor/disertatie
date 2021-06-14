package com.disertatie.Middleware;

import com.disertatie.Middleware.Tools.FluidIOReverseProxy;

import java.net.URI;

import io.undertow.UndertowOptions;
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
                .properties("middleware.type=BLOCKING")
                .web(WebApplicationType.SERVLET);

        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                .properties("middleware.type=NON_BLOCKING")
                .web(WebApplicationType.REACTIVE);

        tomcatBuilder.run(args);
        nettyBuilder.run(args);

        FluidIOReverseProxy fluidIOReverseProxy = new FluidIOReverseProxy(
                URI.create("http://192.168.43.59:8080"),
                URI.create("http://192.168.43.59:8081"));

        Undertow reverseProxy = Undertow.builder()
                .setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, 60000)
                .addHttpListener(8083, "0.0.0.0")
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
