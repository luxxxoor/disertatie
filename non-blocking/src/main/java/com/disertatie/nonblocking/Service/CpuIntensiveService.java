package com.disertatie.nonblocking.Service;

import com.disertatie.nonblocking.Exceptions.UserAlreadyExistsException;
import com.disertatie.nonblocking.Exceptions.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CpuIntensiveService {
    private static final int defaultStrenght = 15;
    private final Map<String, String> users = new HashMap<>();
    final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(defaultStrenght);

    public Mono<Void> register(final String userName, final String password) {
        return Mono.defer(() -> {
            if (users.containsKey(userName)) {
                return Mono.error(new UserAlreadyExistsException());
            }

            final var encodedPassword = bcrypt.encode(password);
            users.put(userName, encodedPassword);

            return Mono.empty();
        });
    }

    public Mono<Boolean> login(final String userName, final String password){
        return Mono.defer(() -> {
            if (!users.containsKey(userName)) {
                return Mono.error(new UserNotFoundException());
            }

            return Mono.just(bcrypt.matches(password, users.get(userName)));
        });
    }
}
