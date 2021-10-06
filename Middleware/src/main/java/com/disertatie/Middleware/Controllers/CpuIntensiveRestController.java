package com.disertatie.Middleware.Controllers;

import com.disertatie.Middleware.Service.CpuIntensiveService;
import com.disertatie.Middleware.Model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
public class CpuIntensiveRestController {
    private final CpuIntensiveService service;

    @Autowired
    public CpuIntensiveRestController(CpuIntensiveService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@RequestBody final UserDTO userDTO) {
        return service.register(userDTO.getUsername(), userDTO.getPassword())
                .map(e -> new ResponseEntity<Void>(OK))
                .onErrorReturn(new ResponseEntity<>(CONFLICT));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(@RequestBody final UserDTO userDTO) {
        return service.login(userDTO.getUsername(), userDTO.getPassword())
                .map(result -> {
                    if (result) {
                        return new ResponseEntity<Void>(OK);
                    } else {
                        return new ResponseEntity<Void>(UNAUTHORIZED);
                    }
                }).onErrorReturn(new ResponseEntity<>(NOT_FOUND));
    }
}
