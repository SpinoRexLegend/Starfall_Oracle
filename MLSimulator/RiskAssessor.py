import random
import json
from datetime import datetime

def perform_assessment(asteroid_id, diameter, velocity, material, best_fuel, best_distance):
    # Assessment ID (integer representation)
    ass_id = random.randint(10000, 99999)
    
    # Parse params
    dia_f = float(diameter) if diameter else 10.0
    vel_f = float(velocity) if velocity else 20.0
    dist_f = float(best_distance) if best_distance else 10000.0
    
    # Impact Probability (heuristic based on distance and velocity)
    ip_base = 1.0 - (dist_f / 100000.0)
    if ip_base < 0:
        ip_base = 0.05
    ip = min(1.0, max(0.01, ip_base + (vel_f / 200.0)))
    
    # Kinetic energy estimation (v^2 * D^3 approximation)
    ke = 0.5 * (dia_f ** 3) * (vel_f ** 2)
    # Convert to some Megaton unit heuristically
    ke_mt = round(ke / 1000.0, 2)
    
    # Risk Score (0 to 100)
    risk_score = min(100.0, round(ip * 100.0 + (ke_mt / 100.0), 2))
    
    # Estimated Damage
    if risk_score > 80:
        damage = "Critical"
    elif risk_score > 50:
        damage = "High"
    elif risk_score > 20:
        damage = "Medium"
    else:
        damage = "Low"
        
    # Requires Deflection
    requires_deflection = True if risk_score > 40 else False
    
    raw_response_dict = {
        "calculated_ip": round(ip, 4),
        "calculated_ke": ke_mt,
        "input_distance": dist_f,
        "raw_score": risk_score
    }
    raw_response_str = json.dumps(raw_response_dict)
    
    # Date time
    created_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    return {
        "assessmentID": ass_id,
        "asteroidID": asteroid_id,
        "impactProbability": round(ip, 4),
        "kineticEnergyMT": ke_mt,
        "estimatedDamage": damage,
        "riskScore": risk_score,
        "requiresDeflection": requires_deflection,
        "rawResponse": raw_response_str,
        "createdAt": created_at
    }
