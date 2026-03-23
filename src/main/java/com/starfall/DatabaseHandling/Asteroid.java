package com.starfall.DatabaseHandling;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Asteroid {

    @Id
    @OneToMany(fetch = FetchType.EAGER)
    private String asteroid_Id;

    private String name;

    @Column(name="Diameter_km")
    private double diameter;

    @Column(name="Velocity_kmps")
    private double velocity;

    private String composition;

    @Column(name="Detection_Source")
    private String dSource;

    @Column(name="Detection_Date")
    private String dDate;
}
