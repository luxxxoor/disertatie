package com.dizertatie.Middleware.Configuration;

import org.springframework.beans.factory.config.BeanPostProcessor;

class BeansPostProcess implements BeanPostProcessor {

    @Override
    public final Object postProcessAfterInitialization(final Object bean, final String beanName) {
        System.out.println("Processing bean: " + beanName);
        (N
        return bean;
    }
}

