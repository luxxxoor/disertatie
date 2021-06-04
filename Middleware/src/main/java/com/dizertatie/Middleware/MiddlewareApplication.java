package com.dizertatie.Middleware;

import com.dizertatie.Middleware.Tool.FluidIO;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MiddlewareApplication {

    public static void main(String[] args) {
        var servletBuilder = new SpringApplicationBuilder(MiddlewareApplication.class)
                .properties("server.port=8080")
                .properties("middleware.server=SERVLET")
                .web(WebApplicationType.SERVLET);

        var reactiveBuilder = new SpringApplicationBuilder(MiddlewareApplication.class)
                .properties("server.port=8081")
                .properties("middleware.server=REACTIVE")
                .web(WebApplicationType.REACTIVE);

        // var servletBeans = servletContext.getBeansWithAnnotation(FluidIO.class);
        // var reactiveBeans = reactiveContext.getBeansWithAnnotation(FluidIO.class);
        //
        // System.out.println("Tomcat beans with FluidIO:");
        // servletBeans.forEach((name, obj) -> {
        //     System.out.println(name);
        // });
        // System.out.println();
        //
        // System.out.println("Netty beans with FluidIO:");
        // reactiveBeans.forEach((name, obj) -> {
        //     System.out.println(name);
        // });
        // System.out.println();

        var servletContext = servletBuilder.run(args);
        var servletBeans = servletContext.getBeansWithAnnotation(FluidIO.class);
        
        // System.out.println("Tomcat beans with FluidIO:");
        // servletBeans.forEach((name, obj) -> {
        //     System.out.println(name);
        // });
        // System.out.println();
    }
}
