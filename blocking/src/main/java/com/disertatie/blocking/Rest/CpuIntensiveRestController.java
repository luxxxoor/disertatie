package com.disertatie.blocking.Rest;

import com.disertatie.blocking.Exceptions.UserAlreadyExistsException;
import com.disertatie.blocking.Exceptions.UserNotFoundException;
import com.disertatie.blocking.Model.UserDTO;
import com.disertatie.blocking.Service.CpuIntensiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Controller
public class CpuIntensiveRestController {
    private final CpuIntensiveService service;

    @Autowired
    public CpuIntensiveRestController(CpuIntensiveService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody final UserDTO userDTO) {
        try {
            service.register(userDTO.getUsername(), userDTO.getPassword());
            return new ResponseEntity<>(OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final UserDTO userDTO) {
        try {
            if (service.login(userDTO.getUsername(), userDTO.getPassword())) {
                return new ResponseEntity<>(OK);
            } else {
                return new ResponseEntity<>(UNAUTHORIZED);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }
}
