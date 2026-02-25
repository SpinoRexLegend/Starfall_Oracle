package com.example.frontend.starfall_prophecy.Simulations;

public class PhysicsEngine {
    ArrayList<Double> thetas = new ArrayList<>();

    double mass, density, thetaRad, velocity;

    public PhysicsEngine(double mass, double density, double velocity, double thetaRad){
        this.density = density;
        this.mass = mass;
        this.velocity = velocity;
        this.thetaRad = thetaRad;
    }

    public boolean containsAngle(){
        if(thetas.contain(thetaRad)){
            thetas.add(thetaRad);
            return true;
        }
        return false;
    }

    public double diameter(){
        return Math.pow(((6*mass)/(Math.PI*density)), ((double) 1/3));
    }

    public double impactEnergy(){
        return (double)((double) 1 /2 * mass * velocity * velocity * Math.sin(thetaRad) * Math.sin(thetaRad));
    }

    public double threatScore(){
        return impactEnergy() / 2;
    }

    public double calculateDeflection() {
        return (velocity * Math.cos(thetaRad)) / mass;
    }

    public double calculateScore() {
        return calculateDeflection() / threatScore();
    }
}
