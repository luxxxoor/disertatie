package com.dizertatie.Middleware.Tool;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Wrapper<T> {
    private final T obj;

    Wrapper(T obj){
        this.obj = obj;
    }

    T get() {
        return obj;
    }
}
