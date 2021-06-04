package com.disertatie.blocking.Rest;

import com.disertatie.blocking.algorithms.Algorithms;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
