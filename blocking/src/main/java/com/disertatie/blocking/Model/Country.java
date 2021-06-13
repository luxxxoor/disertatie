package com.disertatie.blocking.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "country")
public class Country {
    @Id
    @Column(name = "country_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name = "country")
    public String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    public List<City> cities;
}
