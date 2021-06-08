package com.dizertatie.Middleware;

import com.dizertatie.Middleware.Tools.FluidIO;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import com.dizertatie.Middleware.UniversalControllers.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class MiddlewareApplication {

    @SneakyThrows
    public static void main(String[] args) {
        var tomcatBuilder = makeBuilder(8080, MiddlewareApplication.class)
                .properties("middleware.type=SERVLET")
                .web(WebApplicationType.SERVLET);

        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                .properties("middleware.type=REACTIVE")
                .web(WebApplicationType.REACTIVE);

        tomcatBuilder.run(args);
        nettyBuilder.run(args);
    }

    private static SpringApplicationBuilder makeBuilder(Integer port, Class<?>... sources) {
        return new SpringApplicationBuilder(MiddlewareApplication.class)
                .child(sources)
                .properties(String.format("server.port=%d", port));
    }
}
