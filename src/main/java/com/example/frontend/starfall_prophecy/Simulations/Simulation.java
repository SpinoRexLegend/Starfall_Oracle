package com.example.frontend.starfall_prophecy.Simulations;

public class Simulation {
    int s_id;
    double thetaRad;
    double score;

    public Simulation(int s_id, double thetaRad,  double score){
        this.s_id=s_id;
        this.thetaRad = thetaRad;
        this.score = score;
    }
}
