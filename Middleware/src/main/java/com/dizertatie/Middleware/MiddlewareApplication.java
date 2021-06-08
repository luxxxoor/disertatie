package com.dizertatie.Middleware;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class MiddlewareApplication {
    public static void main(String[] args) {
        var reverseProxyBuilder = makeBuilder(8083, MiddlewareApplication.class)
                .properties("middleware.type=REVERSE_PROXY")
                .web(WebApplicationType.SERVLET);
        var tomcatBuilder = makeBuilder(8080, MiddlewareApplication.class)
                .properties("middleware.type=SERVLET")
                .web(WebApplicationType.SERVLET);

        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                .properties("middleware.type=REACTIVE")
                .web(WebApplicationType.REACTIVE);

        tomcatBuilder.run(args);
        nettyBuilder.run(args);
        reverseProxyBuilder.run(args);
    }

    private static SpringApplicationBuilder makeBuilder(Integer port, Class<?>... sources) {
        return new SpringApplicationBuilder(MiddlewareApplication.class)
                .child(sources)
                .properties(String.format("server.port=%d", port));
    }
}
