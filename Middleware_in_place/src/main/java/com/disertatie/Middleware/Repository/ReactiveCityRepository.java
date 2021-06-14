package com.disertatie.Middleware.Repository;

import com.disertatie.Middleware.Model.City;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveCityRepository extends ReactiveCrudRepository<City, Long> {
    Flux<City> findAllByCountryId(Long countryId);
}

