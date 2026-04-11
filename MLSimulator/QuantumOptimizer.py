import uuid
import math
import random
from datetime import datetime

def perform_quantum_optimization(risk_score, requires_deflection, simulation_ids):
    # Mocking Grover's Algorithm Amplitude Amplification
    N = len(simulation_ids) if simulation_ids else 8
    
    # In true Grover's, iterative sweeps limit is roughly (PI/4) * sqrt(N)
    iterations = int(max(1, math.floor((math.pi / 4.0) * math.sqrt(N))))
    qubits_used = 3 # Hardcoded request
    
    results = []
    bar_chart_data = []
    
    base_prob = 1.0 / N if N > 0 else 0.125
    
    # If requires_deflection is False, we just randomize a bit without a solid marked state.
    # We will pick a random "Optimal index" just to show grovers output visually.
    optimal_index = random.randint(0, N-1)
    
    for i, sim_id in enumerate(simulation_ids):
        
        # Mocking the probability amplitude after Grover's diffusion operator
        if i == optimal_index:
            op_prob = round(random.uniform(0.70, 0.95), 4)
            is_optimal = True
        else:
            op_prob = round(random.uniform(0.01, base_prob + 0.10), 4)
            is_optimal = False
            
        qtm_id = "QTM-" + str(uuid.uuid4())[:6].upper()
        odate = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        db_entry = {
            "QuantumID": qtm_id,
            "Qubits_used": qubits_used,
            "Iterations": iterations,
            "Optimal_probability": op_prob,
            "Optimised_at": odate,
            "Sim_ID": sim_id
        }
        results.append(db_entry)
        
        short_id = sim_id[:4].upper()
        bar_chart_data.append({
            "label": short_id,
            "probabilityHeight": round(op_prob * 100.0, 2),
            "isOptimal": is_optimal
        })
        
    return {
        "database_entries": results,
        "bar_chart": bar_chart_data
    }
