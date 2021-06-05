package com.dizertatie.Middleware;

import com.dizertatie.Middleware.Tools.FluidIO;
import lombok.SneakyThrows;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class MiddlewareApplication {

    @SneakyThrows
    public static void main(String[] args) {
        List<Class<?>> classesWithFluidAnnotation = new MiddlewareApplication.MyClass().findMyTypes("com.dizertatie.Middleware");
        classesWithFluidAnnotation.stream().map(Class::getName).forEach(System.out::println);

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

    static class MyClass {
        private List<Class<?>> findMyTypes(String basePackage) throws IOException, ClassNotFoundException {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

            List<Class<?>> candidates = new ArrayList<>();
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + "/" + "**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    if (isCandidate(metadataReader)) {
                        candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
            return candidates;
        }

        private String resolveBasePackage(String basePackage) {
            return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
        }

        private boolean isCandidate(MetadataReader metadataReader) throws ClassNotFoundException {
            try {
                Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
                if (c.getAnnotation(FluidIO.class) != null) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
            return false;
        }
    }
}
