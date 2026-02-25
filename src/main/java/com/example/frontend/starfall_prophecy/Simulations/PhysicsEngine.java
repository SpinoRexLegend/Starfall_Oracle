package com.example.frontend.starfall_prophecy.Simulations;

import java.util.Collections;
import java.util.PriorityQueue;

public class PhysicsEngine {

    PriorityQueue<Simulation> groverImp= new PriorityQueue<>(16, Collections.reverseOrder());

    public double diameter(double mass, double density){
        return Math.pow(((6*mass)/(Math.PI*density)), ((double) 1/3));
    }

    public double impactEnergy(double mass, double velocity, float thetaRad){
        return (double)((double) 1 /2 * mass * velocity * velocity * Math.sin(thetaRad) * Math.sin(thetaRad));
    }

    public double threatScore(double mass, double velocity, float theta){
        return impactEnergy(mass, velocity, theta) / 2;
    }

    public double calculateDeflection(double mass, double velocity, double thetaRad) {
        return (velocity * Math.cos(thetaRad)) / mass;
    }

    public double calculateScore(double mass, double velocity, double thetaRad) {
        return calculateDeflection(mass, velocity, thetaRad) / threatScore(mass, velocity, thetaRad);
    }

    public PriorityQueue<Simulation> getBest(double thetaRad){


    }
}
