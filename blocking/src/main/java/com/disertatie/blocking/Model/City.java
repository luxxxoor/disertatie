package com.disertatie.blocking.Model;

import javax.persistence.*;

@Entity
@Table(name = "city")
public class City {
    @Id
    @Column(name = "city_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name = "city")
    public String name;
}
