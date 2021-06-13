package com.disertatie.nonblocking.Model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table("city")
public class City {
    @Id
    @Column("city_id")
    public Long id;

    @Column("city")
    public String name;

    @Column("country_id")
    private Long countryId;
}
