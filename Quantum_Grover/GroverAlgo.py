import json
import math
import matplotlib
matplotlib.use('Agg')  # Use non-interactive backend for Flask compatibility
import matplotlib.pyplot as plt
import io
import base64
from qiskit import QuantumCircuit, transpile
from qiskit_aer import AerSimulator
from qiskit.visualization import plot_histogram


def build_oracle(n_qubits, target_index):

    oracle_qc = QuantumCircuit(n_qubits, name="Oracle")
    target_bin = format(target_index, f'0{n_qubits}b')[::-1]
    
    for i, bit in enumerate(target_bin):
        if bit == '0': oracle_qc.x(i)
    
    oracle_qc.h(n_qubits - 1)
    oracle_qc.mcx(list(range(n_qubits - 1)), n_qubits - 1)
    oracle_qc.h(n_qubits - 1)
    
    for i, bit in enumerate(target_bin):
        if bit == '0': oracle_qc.x(i)
    
    print("\n--- Oracle Circuit ---")
    print(oracle_qc.draw(output='text'))
    return oracle_qc


def build_diffuser(n_qubits):

    diff_qc = QuantumCircuit(n_qubits, name="Diffuser")
    diff_qc.h(range(n_qubits))
    diff_qc.x(range(n_qubits))
    diff_qc.h(n_qubits - 1)
    diff_qc.mcx(list(range(n_qubits - 1)), n_qubits - 1)
    diff_qc.h(n_qubits - 1)
    diff_qc.x(range(n_qubits))
    diff_qc.h(range(n_qubits))

    print("\n--- Diffuser Circuit ---")
    print(diff_qc.draw(output='text'))

    return diff_qc


def run_quantum_maneuver_search(file_path, return_image=False):

    with open(file_path, 'r') as f:
        data = json.load(f)
        maneuvers = data.get('maneuvers', [])

    best_idx = max((i for i, m in enumerate(maneuvers) if m.get('validity')), 
                   key=lambda i: maneuvers[i].get('score', 0), default=None)

    if best_idx is None:
        print("No valid maneuvers found.")
        return None

    n_qubits = math.ceil(math.log2(len(maneuvers)))
    qc = QuantumCircuit(n_qubits)
    
    qc.h(range(n_qubits))
    
    iterations = max(1, round(math.pi/4 * math.sqrt(2**n_qubits)))
    oracle = build_oracle(n_qubits, best_idx)
    diffuser = build_diffuser(n_qubits)

    for _ in range(iterations):
        qc.append(oracle, range(n_qubits))
        qc.append(diffuser, range(n_qubits))

    qc.measure_all()

    backend = AerSimulator()
    tqc = transpile(qc, backend)
    result = backend.run(tqc, shots=2048).result()
    counts = result.get_counts()

    print(f"Targeting Maneuver at Index: {best_idx}")
    
    if return_image:
        plt.figure()
        plot_histogram(counts, title="Quantum Search Results (Amplified Target)")
        img = io.BytesIO()
        plt.savefig(img, format='png')
        img.seek(0)
        plot_url = base64.b64encode(img.getvalue()).decode()
        plt.close()
        return {
            "maneuver": maneuvers[best_idx],
            "plot_image": f"data:image/png;base64,{plot_url}"
        }
    else:
        plot_histogram(counts, title="Quantum Search Results (Amplified Target)")
        plt.show()

        print("\n--- Quantum Search Result ---")
        print(json.dumps(maneuvers[best_idx], indent=4))

if __name__ == '__main__':
    run_quantum_maneuver_search('maneuver_demo.json')