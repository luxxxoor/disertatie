package com.disertatie.nonblocking.Model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table("country")
public class Country {
    @Id
    @Column("country_id")
    public Long id;

    @Column("country")
    public String name;
}
