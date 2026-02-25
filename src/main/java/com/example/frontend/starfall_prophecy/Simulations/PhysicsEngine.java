package com.example.frontend.starfall_prophecy.Simulations;

public class PhysicsEngine {
    ArrayList<Double> thetas = new ArrayList<>();

    double mass, density, thetaRad, velocity;

    public PhysicsEngine(double diameter, double density, double velocity, double thetaRad){
        this.density = density;
        this.diameter = diameter;
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

    public double mass() {
        return (Math.PI * density * Math.pow(diameter, 3)) / 6.0;
    }

    public double impactEnergy(){
        return (double)((double) 1 /2 * mass() * velocity * velocity * Math.sin(thetaRad) * Math.sin(thetaRad));
    }

    public double threatScore(){
        return impactEnergy() / 2;
    }

    public double calculateDeflection() {
        return (velocity * Math.cos(thetaRad)) / mass();
    }

    public double calculateScore() {
        return calculateDeflection() / threatScore();
    }

    public double executionTime() {
        double g = 9.81;
        return (2 * velocity * Math.sin(thetaRad)) / g;
    }

    public double missedDistance() {
        double g = 9.81;
        return (Math.pow(velocity, 2) * Math.sin(2 * thetaRad)) / g;
    }

}
