from flask import Flask, request, jsonify

app = Flask(__name__)

import random
import uuid
from datetime import datetime
import RiskAssessor
import QuantumOptimizer

@app.route("/predict", methods=["POST"])
def predict():

    data = request.get_json()

    if not data:
        return jsonify({"error": "No data received"}), 400

    material = data.get("Material", "Unknown")
    diameter = data.get("Diameter")
    velocity = data.get("Velocity")

    if diameter is None or velocity is None:
        return jsonify({"error": "Diameter and Velocity are required"}), 400

    simulations = []
    base_fuel = (diameter * velocity) / 10.0
    
    # Calculate a "best" base for successful impacts
    for i in range(8):
        # vary angle and base attributes
        angle_variance = random.uniform(-15.0, 15.0)
        fuel_variance = random.uniform(0.8, 1.5)
        success_chance = random.random() # 0.0 to 1.0

        simulations.append({
            "SimulationID": str(uuid.uuid4()),
            "Angle at which force to be applied": round(90.0 + angle_variance, 2),
            "Execution time for hitting the earth": round(random.uniform(5.0, 30.0), 2),
            "Distance from which it will pass if force is applied at that angle": round(random.uniform(1000.0, 50000.0), 2),
            "Fuel Cost": round(base_fuel * fuel_variance, 2),
            "Chances of success": 1 if success_chance > 0.5 else 0,
            "time and date of simulation": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        })

    response = {
        "status": "success",
        "output": simulations
    }

    return jsonify(response), 200


@app.route("/assess", methods=["POST"])
def assess():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data received"}), 400
        
    asteroid_id = data.get("asteroidID")
    diameter = data.get("Diameter")
    velocity = data.get("Velocity")
    material = data.get("Material")
    best_fuel = data.get("BestFuel")
    best_distance = data.get("BestDistance")
    
    result = RiskAssessor.perform_assessment(asteroid_id, diameter, velocity, material, best_fuel, best_distance)
    
    return jsonify({
        "status": "success",
        "output": result
    }), 200


@app.route("/quantum", methods=["POST"])
def quantum():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data received"}), 400
        
    risk_score = data.get("riskScore", 0.0)
    requires_deflection = data.get("requiresDeflection", False)
    simulation_ids = data.get("simulationIDs", [])
    
    result = QuantumOptimizer.perform_quantum_optimization(risk_score, requires_deflection, simulation_ids)
    
    return jsonify({
        "status": "success",
        "output": result
    }), 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)