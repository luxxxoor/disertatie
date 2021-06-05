package com.disertatie.nonblocking.controller;

import com.disertatie.nonblocking.algorithms.Algorithms;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class FluxAndMonoController {
    static Integer counter = 0;

    @GetMapping("/test1")
    public Mono<ResponseEntity<Integer>> test1() {
        System.out.println("[test1] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.just(new ResponseEntity<>(c, OK))
                .delayElement(Duration.ofMillis(500))
                .map((x)-> {
                    System.out.println("[test1] Finished: " + c);
                    return x;
                });
    }

    @GetMapping(value = "/test2")
    public Mono<ResponseEntity<BigInteger>> test2(@RequestParam Integer n) {
        System.out.println("[test2] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.just(new ResponseEntity<>(Algorithms.fibonacci(n), OK))
                .map((x)-> {
                    System.out.println("[test2] Finished: " + c);
                    return x;
                });
    }

    @GetMapping(value = "/test3")
    public Mono<ResponseEntity<BigInteger>> test3(@RequestParam Integer m, @RequestParam Integer n) {
        System.out.println("[test3] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.just(new ResponseEntity<>(Algorithms.ack(m, n), OK))
                .map((x)-> {
                    System.out.println("[test3] Finished: " + c);
                   return x;
                });
    }

    @GetMapping(value = "/test4")
    public Mono<ResponseEntity<BigInteger>> test4(@RequestParam Integer m, @RequestParam Integer n, @RequestParam String ip) {
        System.out.println("[test4] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.just(Algorithms.ack(m, n))
                .flatMap((result) -> callSecondServer(result, ip))
                .map((response) -> new ResponseEntity<>(response, OK))
                .map((x)-> {
                    System.out.println("[test4] Finished: " + c);
                    return x;
                });
    }

    private Mono<BigInteger> callSecondServer(BigInteger ackermannResponse, String ip) {
        WebClient webClient = WebClient.create("http://" + ip + ":8081");
        String uri = "/test3/?m=" + ackermannResponse.mod(BigInteger.valueOf(3)) +
                "&n=" + ackermannResponse.mod(BigInteger.valueOf(10));
        return webClient.get().uri(uri).retrieve().bodyToMono(BigInteger.class);
    }
}
