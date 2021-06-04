package com.dizertatie.Middleware.Tool;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Wrapper<T, U> {
    T obj;

    Wrapper(T obj){
        this.obj = obj;
    }

    Wrapper<U> get() {
        Mono<Integer> i;
        Flux<Integer> i1;
    }
}
