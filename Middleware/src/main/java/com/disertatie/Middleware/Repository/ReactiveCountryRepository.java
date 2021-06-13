package com.disertatie.Middleware.Repository;

import com.disertatie.Middleware.Model.Country;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveCountryRepository extends ReactiveCrudRepository<Country, Long> {
    Mono<Country> findOneByName(String name);
}