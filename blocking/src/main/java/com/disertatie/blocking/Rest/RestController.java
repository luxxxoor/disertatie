package com.disertatie.blocking.Rest;

import com.disertatie.blocking.algorithms.Algorithms;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class RestController {
    static Integer counter = 0;

    @GetMapping("/test1")
    public ResponseEntity<Integer> test1() {
        System.out.println("[test1] Started: " + counter);
        int c = counter;
        counter += 1;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[test1] Finished: " + c);
        return new ResponseEntity<>(c, OK);
    }

    @GetMapping("/test2")
    public CompletableFuture<ResponseEntity<BigInteger>> test2(@RequestParam Integer n) {
        System.out.println("[test2] Started: " + counter);
        int c = counter;
        counter += 1;

        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(Algorithms.fibonacci(n), OK))
                .thenApply((result) -> {
                    System.out.println("[test2] Finished: " + c);
                    return result;
                });
    }

    @GetMapping("/test3")
    public CompletableFuture<ResponseEntity<BigInteger>> test3(@RequestParam Integer m, @RequestParam Integer n) {
        System.out.println("[test3] Started: " + counter);
        int c = counter;
        counter += 1;

        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(Algorithms.ack(m, n), OK))
                .thenApply((result) -> {
                    System.out.println("[test3] Finished: " + c);
                    return result;
                });
    }

    @GetMapping("/test4")
    public CompletableFuture<ResponseEntity<BigInteger>> test4(@RequestParam Integer m, @RequestParam Integer n, @RequestParam String ip) {
        System.out.println("[test4] Started: " + counter);
        int c = counter;
        counter += 1;

        return CompletableFuture.supplyAsync(() -> Algorithms.ack(m,n))
                .thenApply((result) -> callSecondServer(result, ip))
                .thenApply((response) -> new ResponseEntity<>(response.join(), OK))
                .thenApply((result) -> {
                    System.out.println("[test4] Finished: " + c);
                    return result;
                });
    }

    @Async
    private CompletableFuture<BigInteger> callSecondServer(BigInteger ackermannResponse, String ip) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + ip + ":8080/test3/?m=" + ackermannResponse.mod(BigInteger.valueOf(3)) +
                "&n=" + ackermannResponse.mod(BigInteger.valueOf(10));
        return CompletableFuture.completedFuture(restTemplate.getForObject(url, BigInteger.class));
    }
}
