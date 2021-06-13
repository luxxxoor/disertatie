package com.disertatie.blocking.Repository;

import com.disertatie.blocking.Model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBRepository extends JpaRepository<Country, Long> {
    Country findOneByName(String name);
}
