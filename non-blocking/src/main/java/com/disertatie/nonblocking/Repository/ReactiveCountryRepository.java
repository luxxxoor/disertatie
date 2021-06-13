package com.disertatie.nonblocking.Repository;

import com.disertatie.nonblocking.Model.Country;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveCountryRepository extends ReactiveCrudRepository<Country, Long> {
    Mono<Country> findOneByName(String name);
}