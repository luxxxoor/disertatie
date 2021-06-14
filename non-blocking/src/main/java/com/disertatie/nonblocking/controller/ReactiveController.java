package com.disertatie.nonblocking.controller;

import com.disertatie.nonblocking.Repository.ReactiveCityRepository;
import com.disertatie.nonblocking.Repository.ReactiveCountryRepository;
import com.disertatie.nonblocking.algorithms.Algorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ReactiveController {
    @Autowired
    private ReactiveCountryRepository countryRepository;
    @Autowired
    private ReactiveCityRepository cityRepository;

    static Integer counter = 0;

    @GetMapping("/test1")
    public Mono<ResponseEntity<Integer>> test1() {
        System.out.println("[test1] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.defer(() -> Mono.just(new ResponseEntity<>(c, OK)))
                .delayElement(Duration.ofMillis(500))
                .map(element -> {
                    System.out.println("[test1] Finished: " + c);
                    return element;
                });
    }

    @GetMapping("/test2")
    public Mono<ResponseEntity<BigInteger>> test2(@RequestParam Integer n) {
        System.out.println("[test2] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.defer(() -> Mono.just(new ResponseEntity<>(Algorithms.fibonacci(n), OK)))
                .map(element -> {
                    System.out.println("[test2] Finished: " + c);
                    return element;
                });
    }

    @GetMapping("/test3")
    public Mono<ResponseEntity<BigInteger>> test3(@RequestParam Integer m, @RequestParam Integer n) {
        System.out.println("[test3] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.defer(() -> Mono.just(new ResponseEntity<>(Algorithms.ack(m, n), OK)))
                .map(element -> {
                    System.out.println("[test3] Finished: " + c);
                   return element;
                });
    }

    @GetMapping("/test4")
    public Mono<ResponseEntity<BigInteger>> test4(@RequestParam Integer m, @RequestParam Integer n, @RequestParam String ip) {
        System.out.println("[test4] Started: " + counter);
        int c = counter;
        counter += 1;

        return Mono.defer(() -> Mono.just(Algorithms.ack(m, n)))
                .flatMap(result -> callSecondServer(result, ip))
                .map(response -> new ResponseEntity<>(response, OK))
                .map(element -> {
                    System.out.println("[test4] Finished: " + c);
                    return element;
                });
    }

    @GetMapping("/test5")
    public Mono<ResponseEntity<List<String>>> test5(@RequestParam(name = "country") String countryName) {
        return Mono.defer(() -> countryRepository.findOneByName(countryName))
                .map(country -> cityRepository.findAllByCountryId(country.id))
                .flatMap(Flux::collectList)
                .map(cities -> cities.stream().map(city -> city.name).collect(Collectors.toList()))
                .map(cities -> new ResponseEntity<>(cities, OK));
    }

    private Mono<BigInteger> callSecondServer(BigInteger ackermannResponse, String ip) {
        WebClient webClient = WebClient.create("http://" + ip + ":8081");
        String uri = "/test3/?m=" + ackermannResponse.mod(BigInteger.valueOf(3)) +
                "&n=" + ackermannResponse.mod(BigInteger.valueOf(10));
        return webClient.get().uri(uri).retrieve().bodyToMono(BigInteger.class);
    }
}
