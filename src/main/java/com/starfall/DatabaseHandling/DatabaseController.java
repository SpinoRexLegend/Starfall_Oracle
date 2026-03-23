package com.starfall.DatabaseHandling;

import com.starfall.DatabaseHandling.Asteroid;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;


public class DatabaseController {

    SessionFactory sf = new Configuration()
            .addAnnotatedClass(com.starfall.DatabaseHandling.Asteroid.class)
            .addAnnotatedClass(com.starfall.DatabaseHandling.SimulationRun.class)
            .addAnnotatedClass(com.starfall.DatabaseHandling.FinalDecision.class)
            .addAnnotatedClass(com.starfall.DatabaseHandling.QuantumOptimise.class)
            .addAnnotatedClass(com.starfall.DatabaseHandling.RiskAssessment.class)
            .addAnnotatedClass(com.starfall.DatabaseHandling.DefStrat.class)
            .configure()
            .buildSessionFactory();

    Session session = sf.openSession();

    Transaction tx = session.beginTransaction();

    public Asteroid getAsteroid(String a_ID) {
        return session.find(Asteroid.class, a_ID);
    }

    public void setSimulationRun(List<String[]> simulationRun) {
        for(String[] a : simulationRun) {

            SimulationRun simulation = new SimulationRun();

            simulation.setS_ID(a[0]);
            simulation.setAngleOI(Double.parseDouble(a[1]));
            simulation.setToe(Double.parseDouble(a[2]));
            simulation.setDistanceMissed(Double.parseDouble(a[3]));
            simulation.setFuel(Double.parseDouble(a[4]));
            simulation.setSuccess(Boolean.parseBoolean(a[5]));
            simulation.setSDate(a[6]);

            session.persist(simulation);
        }
        tx.commit();
    }

    public void setRiskAssessment(List<String[]> risks) {
        for(String[] a : risks) {

            RiskAssessment risk = new RiskAssessment();

            risk.setAss_ID(Integer.parseInt(a[0]));
            risk.setAID(a[1]);
            risk.setIp(Double.parseDouble(a[2]));
            risk.setKineticEnergy(Double.parseDouble(a[3]));
            risk.setDamage(a[4]);
            risk.setRiskScore(Double.parseDouble(a[5]));
            risk.setDefReq(Boolean.parseBoolean(a[6]));
            risk.setResponse(a[7]);
            risk.setCDate(a[8]);

            session.persist(risk);
        }
        tx.commit();
    }

    public void setQuantumOptimise(List<String[]> optimers){
        for(String[] a : optimers) {

            QuantumOptimise quantumOptimise = new QuantumOptimise();

            quantumOptimise.setAss_ID(a[0]);
            quantumOptimise.setQubits(Integer.parseInt(a[1]));
            quantumOptimise.setIteration(Integer.parseInt(a[2]));
            quantumOptimise.setOpProb(Double.parseDouble(a[3]));
            quantumOptimise.setODate(a[4]);

            session.persist(quantumOptimise);
        }
        tx.commit();
    }

    public void setFinalDecision(String[] finalDec){
        FinalDecision finalDecision = new FinalDecision();

        finalDecision.setDes_ID(finalDec[0]);
        finalDecision.setStratID(finalDec[1]);
        finalDecision.setImpactProb(Double.parseDouble(finalDec[2]));
        finalDecision.setEDmg(finalDec[3]);
        finalDecision.setRScore(Double.parseDouble(finalDec[4]));
        finalDecision.setAAt(finalDec[5]);

        session.persist(finalDec);
        tx.commit();
    }

    public void setDefStrat(){
        //Tedious work, will do it later
    }


    public void closeSession() {
        session.close();
        sf.close();
    }
}
