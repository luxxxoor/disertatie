package com.disertatie.blocking.Service;

import com.disertatie.blocking.Exceptions.UserAlreadyExistsException;
import com.disertatie.blocking.Exceptions.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CpuIntensiveService {
    private static final int defaultStrenght = 15;
    private final Map<String, String> users = new HashMap<>();
    final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(defaultStrenght);

    public void register(final String userName, final String password) throws UserAlreadyExistsException {
        if (users.containsKey(userName)) {
            throw new UserAlreadyExistsException();
        }

        final var encodedPassword = bcrypt.encode(password);
        users.put(userName, encodedPassword);
    }

    public boolean login(final String userName, final String password) throws UserNotFoundException{
        if (!users.containsKey(userName)) {
            throw new UserNotFoundException();
        }

        return bcrypt.matches(password, users.get(userName));
    }
}
