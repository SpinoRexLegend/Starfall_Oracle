package com.starfall.DatabaseHandling;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RiskAssessment {

    @Id
    @Column(name = "assessment_id")
    public int ass_ID;

    @Column(name = "asteroid_id")
    public String aID;

    @Column(name = "impact_probability")
    public double ip;

    @Column(name = "kinetic_energy_mt")
    public double kineticEnergy;

    @Column(name = "estimated_damage")
    public String damage;

    @Column(name = "risk_score")
    public double riskScore;

    @Column(name = "requires_deflection")
    private boolean defReq;

    @Column(name = "raw_response")
    private String response;

    @Column(name = "created_at")
    private String cDate;
}
