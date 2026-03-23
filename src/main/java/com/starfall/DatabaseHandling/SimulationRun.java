package com.starfall.DatabaseHandling;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SimulationRun {

    @Id
    @Column(name = "Simulation_Id")
    public String s_ID;

    @Column(name = "Impact_angle_deg")
    public double angleOI;

    @Column(name = "Execution_time")
    public double toe;

    @Column(name = "Missed_distance_km")
    public double distanceMissed;

    @Column(name = "Fuel_Cost")
    public double fuel;

    @Column(name = "Success")
    public boolean success;

    @Column(name = "Simulated_at")
    private String sDate;
}
