package com.starfall.DatabaseHandling;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class FinalDecision {

    @Id
    @Column(name = "Decision_ID")
    public String des_ID;

    @Column(name = "Strategy_ID")
    public String stratID;

    @Column(name = "Impact_Pro")
    public double impactProb;

    @Column(name = "Estimated_damage")
    public String eDmg;

    @Column(name = "Risk_Score")
    public double rScore;

    @Column(name = "Assessed_at")
    public String aAt;
}
