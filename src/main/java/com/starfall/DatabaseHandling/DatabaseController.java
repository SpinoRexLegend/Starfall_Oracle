package com.starfall.DatabaseHandling;

import com.starfall.DatabaseHandling.Asteroid;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
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
        if (!tx.isActive())
            tx = session.beginTransaction();
        for (String[] a : simulationRun) {

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
        tx = session.beginTransaction();
    }

    public void setRiskAssessment(List<String[]> risks) {
        if (!tx.isActive())
            tx = session.beginTransaction();
        for (String[] a : risks) {

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
        tx = session.beginTransaction();
    }

    public void setQuantumOptimise(List<String[]> optimers) {
        if (!tx.isActive())
            tx = session.beginTransaction();
        for (String[] a : optimers) {

            QuantumOptimise quantumOptimise = new QuantumOptimise();

            quantumOptimise.setAss_ID(a[0]);
            quantumOptimise.setQubits(Integer.parseInt(a[1]));
            quantumOptimise.setIteration(Integer.parseInt(a[2]));
            quantumOptimise.setOpProb(Double.parseDouble(a[3]));
            quantumOptimise.setODate(a[4]);

            session.persist(quantumOptimise);
        }
        tx.commit();
        tx = session.beginTransaction();
    }

    public void setFinalDecision(String[] finalDec) {
        if (!tx.isActive())
            tx = session.beginTransaction();
        FinalDecision finalDecision = new FinalDecision();

        finalDecision.setDes_ID(finalDec[0]);
        finalDecision.setStratID(finalDec[1]);
        finalDecision.setImpactProb(Double.parseDouble(finalDec[2]));
        finalDecision.setEDmg(finalDec[3]);
        finalDecision.setRScore(Double.parseDouble(finalDec[4]));
        finalDecision.setAAt(finalDec[5]);

        session.persist(finalDecision);
        tx.commit();
        tx = session.beginTransaction();
    }

    public void setDefStrat() {
        // Tedious work, will do it later
    }

    public void saveSimulationFromResult(List<HashMap<String, Object>> mlDataList) {
        if (!tx.isActive()) {
            tx = session.beginTransaction();
        }

        for (HashMap<String, Object> mlData : mlDataList) {
            SimulationRun simulation = new SimulationRun();

            // Generate automatic UUID for s_ID if missing from python map
            String s_ID = mlData.containsKey("SimulationID") ? String.valueOf(mlData.get("SimulationID")) :
                          (mlData.containsKey("s_ID") ? String.valueOf(mlData.get("s_ID")) : UUID.randomUUID().toString());
            simulation.setS_ID(s_ID);

            // Date Handling
            String sDate = mlData.containsKey("time and date of simulation") ? String.valueOf(mlData.get("time and date of simulation")) :
                           (mlData.containsKey("Simulated_at") ? String.valueOf(mlData.get("Simulated_at"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            simulation.setSDate(sDate);

            // Mapping properties securely
            if (mlData.containsKey("Angle at which force to be applied")) {
                simulation.setAngleOI(Double.parseDouble(String.valueOf(mlData.get("Angle at which force to be applied"))));
            } else if (mlData.containsKey("Impact_angle_deg")) {
                simulation.setAngleOI(Double.parseDouble(String.valueOf(mlData.get("Impact_angle_deg"))));
            } else if (mlData.containsKey("angleOI")) { // Fallback custom key
                simulation.setAngleOI(Double.parseDouble(String.valueOf(mlData.get("angleOI"))));
            }

            if (mlData.containsKey("Execution time for hitting the earth")) {
                simulation.setToe(Double.parseDouble(String.valueOf(mlData.get("Execution time for hitting the earth"))));
            } else if (mlData.containsKey("Execution_time")) {
                simulation.setToe(Double.parseDouble(String.valueOf(mlData.get("Execution_time"))));
            } else if (mlData.containsKey("executionTime")) {
                simulation.setToe(Double.parseDouble(String.valueOf(mlData.get("executionTime"))));
            }

            if (mlData.containsKey("Distance from which it will pass if force is applied at that angle")) {
                simulation.setDistanceMissed(Double.parseDouble(String.valueOf(mlData.get("Distance from which it will pass if force is applied at that angle"))));
            } else if (mlData.containsKey("Missed_distance_km")) {
                simulation.setDistanceMissed(Double.parseDouble(String.valueOf(mlData.get("Missed_distance_km"))));
            } else if (mlData.containsKey("distanceMissed")) {
                simulation.setDistanceMissed(Double.parseDouble(String.valueOf(mlData.get("distanceMissed"))));
            }

            if (mlData.containsKey("Fuel Cost")) {
                simulation.setFuel(Double.parseDouble(String.valueOf(mlData.get("Fuel Cost"))));
            } else if (mlData.containsKey("Fuel_Cost")) {
                simulation.setFuel(Double.parseDouble(String.valueOf(mlData.get("Fuel_Cost"))));
            } else if (mlData.containsKey("fuelCost")) {
                simulation.setFuel(Double.parseDouble(String.valueOf(mlData.get("fuelCost"))));
            }

            if (mlData.containsKey("Chances of success")) {
                simulation.setSuccess(Double.parseDouble(String.valueOf(mlData.get("Chances of success"))) > 0.5);
            } else if (mlData.containsKey("Success")) {
                simulation.setSuccess(Boolean.parseBoolean(String.valueOf(mlData.get("Success"))));
            } else if (mlData.containsKey("success")) {
                simulation.setSuccess(Boolean.parseBoolean(String.valueOf(mlData.get("success"))));
            }

            session.persist(simulation);
        }
        tx.commit();

        // Ensure a new transaction is ready for the next operation as we handled
        // singleton tx pattern
        tx = session.beginTransaction();
    }

    public void saveRiskAssessmentFromResult(HashMap<String, Object> riskData) {
        if (!tx.isActive()) {
            tx = session.beginTransaction();
        }

        RiskAssessment risk = new RiskAssessment();

        if (riskData.containsKey("assessmentID")) {
            risk.setAss_ID(Integer.parseInt(String.valueOf(riskData.get("assessmentID"))));
        }

        if (riskData.containsKey("asteroidID")) {
            risk.setAID(String.valueOf(riskData.get("asteroidID")));
        }

        if (riskData.containsKey("impactProbability")) {
            risk.setIp(Double.parseDouble(String.valueOf(riskData.get("impactProbability"))));
        }

        if (riskData.containsKey("kineticEnergyMT")) {
            risk.setKineticEnergy(Double.parseDouble(String.valueOf(riskData.get("kineticEnergyMT"))));
        }

        if (riskData.containsKey("estimatedDamage")) {
            risk.setDamage(String.valueOf(riskData.get("estimatedDamage")));
        }

        if (riskData.containsKey("riskScore")) {
            risk.setRiskScore(Double.parseDouble(String.valueOf(riskData.get("riskScore"))));
        }

        if (riskData.containsKey("requiresDeflection")) {
            risk.setDefReq(Boolean.parseBoolean(String.valueOf(riskData.get("requiresDeflection"))));
        }

        if (riskData.containsKey("rawResponse")) {
            risk.setResponse(String.valueOf(riskData.get("rawResponse")));
        }

        if (riskData.containsKey("createdAt")) {
            risk.setCDate(String.valueOf(riskData.get("createdAt")));
        } else {
            risk.setCDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        session.persist(risk);
        tx.commit();

        tx = session.beginTransaction();
    }

    public void saveQuantumOptimiseFromResult(List<HashMap<String, Object>> quantumDataList) {
        if (!tx.isActive()) {
            tx = session.beginTransaction();
        }

        for (HashMap<String, Object> quantumData : quantumDataList) {
            QuantumOptimise quantum = new QuantumOptimise();

            if (quantumData.containsKey("QuantumID")) {
                quantum.setAss_ID(String.valueOf(quantumData.get("QuantumID")));
            } else {
                quantum.setAss_ID("QTM-" + UUID.randomUUID().toString().substring(0, 8));
            }

            if (quantumData.containsKey("Qubits_used")) {
                quantum.setQubits(Integer.parseInt(String.valueOf(quantumData.get("Qubits_used"))));
            }

            if (quantumData.containsKey("Iterations")) {
                quantum.setIteration(Integer.parseInt(String.valueOf(quantumData.get("Iterations"))));
            }

            if (quantumData.containsKey("Optimal_probability")) {
                quantum.setOpProb(Double.parseDouble(String.valueOf(quantumData.get("Optimal_probability"))));
            }

            if (quantumData.containsKey("Optimised_at")) {
                quantum.setODate(String.valueOf(quantumData.get("Optimised_at")));
            } else {
                quantum.setODate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            session.persist(quantum);
        }
        tx.commit();

        tx = session.beginTransaction();
    }

    public void closeSession() {
        session.close();
        sf.close();
    }
}
