/**
 * Project Aegis - Frontend JavaScript
 * Handles WebSocket connection, form submission, and UI updates
 */

// API Configuration
const API_BASE = 'http://localhost:8000';
const WS_URL = 'ws://localhost:8000/ws';

// State
let websocket = null;
let isConnected = false;
let isAnalyzing = false;

// Demo asteroid data
// Demo asteroid data will be fetched from backend
let availableAsteroids = {};


// ============================================================================
// INITIALIZATION
// ============================================================================

document.addEventListener('DOMContentLoaded', () => {
    initScrollEffects();
    initCommandCenter();
    initQuantumSearch();

    connectWebSocket();

    console.log('Project Aegis: Frontend initialized');
});

function initQuantumSearch() {
    const btn = document.getElementById('run-quantum-btn');
    const container = document.getElementById('quantum-plot-container');
    const img = document.getElementById('quantum-plot');
    // const quantumCard = document.getElementById('quantum-card'); // Not used effectively yet

    if (!btn) return;

    btn.addEventListener('click', async () => {
        btn.disabled = true;
        const originalText = btn.innerHTML;
        btn.innerHTML = '⚡ Running Quantum Circuit...';

        try {
            const response = await fetch('http://localhost:5000/run-grover');
            const data = await response.json();

            if (data.status === 'success') {
                img.src = data.data.plot_image;
                container.style.display = 'block';

                // Update quantum card with real results
                const optimalIndexEl = document.getElementById('optimal-index');
                if (optimalIndexEl) optimalIndexEl.textContent = data.data.maneuver.id;

                const quantumAdvEl = document.getElementById('quantum-advantage');
                if (quantumAdvEl) quantumAdvEl.textContent = "Confirmed";

                const successProbEl = document.getElementById('success-prob');
                if (successProbEl) successProbEl.textContent = `${(data.data.maneuver.score * 100).toFixed(1)}%`;

                const optimalParamsEl = document.getElementById('optimal-params');
                if (optimalParamsEl) {
                    optimalParamsEl.innerHTML =
                        `<strong>Target Confirmed:</strong> ID ${data.data.maneuver.id} <br>` +
                        `Score: ${data.data.maneuver.score}`;
                }
            } else {
                alert('Error running quantum search: ' + data.message);
            }
        } catch (error) {
            console.error('Quantum search error:', error);
            alert('Failed to connect to Quantum Backend (Make sure server.py is running)');
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    });
}




// ============================================================================
// SCROLL EFFECTS
// ============================================================================

function initScrollEffects() {
    const videoBg = document.getElementById('video-bg');
    const contentSection = document.getElementById('dashboard');
    const mainTitle = document.getElementById('main-title');
    const subtitle = document.getElementById('subtitle');
    const titleContainer = document.getElementById('title-container');
    const topBar = document.getElementById('top-bar');

    const fadeEnd = window.innerHeight;
    let ticking = false;

    window.addEventListener('scroll', () => {
        if (!ticking) {
            window.requestAnimationFrame(() => {
                updateScrollEffects();
                ticking = false;
            });
            ticking = true;
        }
    });

    function updateScrollEffects() {
        const scrollY = window.scrollY;
        let progress = Math.min(Math.max(scrollY / fadeEnd, 0), 1);

        // Video fade
        videoBg.style.opacity = 1 - progress;

        // Content section fade in
        contentSection.style.opacity = Math.max(0, (progress - 0.3) * 1.43);
        if (progress >= 0.7) {
            contentSection.style.opacity = 1;
        }

        // Title fade out
        const titleOpacity = Math.max(0, 1 - (progress * 3));
        mainTitle.style.opacity = titleOpacity;
        subtitle.style.opacity = titleOpacity;

        // Hide container
        if (progress > 0.35) {
            titleContainer.style.visibility = 'hidden';
        } else {
            titleContainer.style.visibility = 'visible';
        }

        // Top bar fade in
        topBar.style.opacity = progress;
        if (progress > 0.1) {
            topBar.classList.add('visible');
        } else {
            topBar.classList.remove('visible');
        }
    }

    updateScrollEffects();
}

// ============================================================================
// WEBSOCKET
// ============================================================================

function connectWebSocket() {
    try {
        websocket = new WebSocket(WS_URL);

        websocket.onopen = () => {
            console.log('[WebSocket] Connected');
            isConnected = true;
            updateConnectionStatus(true);
        };

        websocket.onclose = () => {
            console.log('[WebSocket] Disconnected');
            isConnected = false;
            updateConnectionStatus(false);

            // Reconnect after 3 seconds
            setTimeout(connectWebSocket, 3000);
        };

        websocket.onerror = (error) => {
            console.error('[WebSocket] Error:', error);
            updateConnectionStatus(false);
        };

        websocket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                handleWebSocketMessage(data);
            } catch (e) {
                console.error('[WebSocket] Parse error:', e);
            }
        };
    } catch (error) {
        console.error('[WebSocket] Connection failed:', error);
        updateConnectionStatus(false);
        setTimeout(connectWebSocket, 3000);
    }
}

function updateConnectionStatus(connected) {
    const statusEl = document.getElementById('connection-status');
    const textEl = statusEl.querySelector('.status-text');

    if (connected) {
        statusEl.className = 'status-connected';
        textEl.textContent = 'Connected';
    } else {
        statusEl.className = 'status-disconnected';
        textEl.textContent = 'Disconnected';
    }
}

function handleWebSocketMessage(data) {
    console.log('[WebSocket] Message:', data);

    switch (data.event) {
        case 'connected':
            console.log('[WebSocket] Server confirmed connection');
            break;

        case 'workflow_started':
            showResults();
            resetAgentSteps();
            updateVerdict('analyzing', '⏳', 'Analyzing...');
            break;

        case 'agent_started':
            updateAgentStatus(data.agent, 'running', data.data.message || 'Processing...');
            break;

            document.getElementById('disp-probability').textContent = asteroid.impact_probability;
            document.getElementById('target-info').classList.remove('hidden');
            break;

        case 'candidates_generated':
            updateAgentStatus(data.agent, 'running', data.data.message);

            // Render simulation candidates
            if (data.data.candidates) {
                renderSimulationTable(data.data.candidates);
            }

            // Advance Quantum UI state
            document.getElementById('quantum-step').classList.add('running');
            document.getElementById('quantum-status').textContent = 'Executing Grover algorithm...';
            break;

        case 'agent_completed':
            updateAgentStatus(data.agent, 'completed', 'Complete');
            updateAgentResult(data.agent, data.data);
            break;



        case 'workflow_completed':
            isAnalyzing = false;
            document.getElementById('analyze-btn').disabled = false;

            if (data.status === 'APPROVED') {
                updateVerdict('approved', '✅', 'MISSION APPROVED');
            } else if (data.status === 'REJECTED') {
                updateVerdict('rejected', '❌', 'MISSION REJECTED');
            } else if (data.status === 'NO_ACTION') {
                updateVerdict('approved', '🌍', 'NO ACTION NEEDED');
            }

            // Update quantum step
            document.getElementById('quantum-step').classList.remove('running');
            document.getElementById('quantum-step').classList.add('completed');
            document.getElementById('quantum-status').textContent = 'Complete';

            break;

        case 'workflow_error':
            isAnalyzing = false;
            document.getElementById('analyze-btn').disabled = false;
            updateVerdict('rejected', '⚠️', 'Error: ' + data.data.error);
            break;
    }
}

// ============================================================================
// COMMAND CENTER INTERFACE
// ============================================================================

function initCommandCenter() {
    const analyzeBtn = document.getElementById('analyze-btn');
    const promptInput = document.getElementById('command-prompt');
    const targetInfo = document.getElementById('target-info');

    if (!analyzeBtn || !promptInput) return;

    analyzeBtn.addEventListener('click', async (e) => {
        e.preventDefault();

        const prompt = promptInput.value.trim();
        if (!prompt) {
            alert("Please enter a mission command.");
            return;
        }

        if (isAnalyzing) return;

        // UI Feedback
        const originalText = analyzeBtn.innerHTML;
        analyzeBtn.innerHTML = '🚀 Sending Command...';
        analyzeBtn.disabled = true;

        try {
            // Send prompt directly to analysis pipeline
            // Agent 1 will handle the search
            await startAnalysis({ prompt: prompt });

        } catch (error) {
            console.error("Command Center Error:", error);
            alert("System Malfunction: Unable to execute command.");
        } finally {
            analyzeBtn.disabled = false;
            analyzeBtn.innerHTML = originalText;
        }
    });

    // Allow Enter key to submit
    promptInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            analyzeBtn.click();
        }
    });
}



async function startAnalysis(asteroidData) {
    if (!isConnected) {
        alert('Not connected to server. Please wait for connection.');
        return;
    }

    isAnalyzing = true;
    document.getElementById('analyze-btn').disabled = true;

    try {
        const response = await fetch(`${API_BASE}/analyze`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(asteroidData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        console.log('[API] Analysis started:', result);

    } catch (error) {
        console.error('[API] Error:', error);
        alert('Failed to start analysis. Is the API server running?');
        isAnalyzing = false;
        document.getElementById('analyze-btn').disabled = false;
    }
}

// ============================================================================
// UI UPDATES
// ============================================================================

function showResults() {
    document.getElementById('results-placeholder').classList.add('hidden');
    document.getElementById('results-content').classList.remove('hidden');
}

function resetAgentSteps() {
    ['agent1', 'agent2', 'agent3', 'quantum'].forEach(agent => {
        const step = document.getElementById(`${agent}-step`);
        if (step) {
            step.classList.remove('running', 'completed');
        }
        const status = document.getElementById(`${agent}-status`);
        if (status) {
            status.textContent = 'Waiting...';
        }
        const result = document.getElementById(`${agent}-result`);
        if (result) {
            result.textContent = '';
        }
    });
}

function updateAgentStatus(agent, status, message) {
    let stepId;
    switch (agent) {
        case 'agent_1': stepId = 'agent1-step'; break;
        case 'agent_2': stepId = 'agent2-step'; break;
        case 'agent_3': stepId = 'agent3-step'; break;
        default: return;
    }

    const step = document.getElementById(stepId);
    const statusEl = document.getElementById(stepId.replace('-step', '-status'));

    if (step) {
        step.classList.remove('running', 'completed');
        step.classList.add(status);
    }

    if (statusEl) {
        statusEl.textContent = message;
    }
}

function updateAgentResult(agent, data) {
    let resultId;
    switch (agent) {
        case 'agent_1': resultId = 'agent1-result'; break;
        case 'agent_2': resultId = 'agent2-result'; break;
        case 'agent_3': resultId = 'agent3-result'; break;
        default: return;
    }

    const resultEl = document.getElementById(resultId);
    if (!resultEl) return;

    switch (agent) {
        case 'agent_1':
            resultEl.innerHTML = `Risk Score: <strong>${data.risk_score?.toFixed(1) || '-'}/10</strong> | ` +
                `Deflection: ${data.requires_deflection ? 'Required' : 'Not needed'}`;

            // Update threat card
            document.getElementById('risk-score').textContent = `${data.risk_score?.toFixed(1) || '-'}/10`;
            document.getElementById('kinetic-energy').textContent = `${data.kinetic_energy_mt?.toFixed(2) || '-'} MT`;
            document.getElementById('damage-category').textContent = data.kinetic_energy_mt > 1000 ? 'Global' : 'Regional';
            break;

        case 'agent_2':
            resultEl.innerHTML = `Optimal: Index ${data.optimal_index} | ` +
                `Velocity: ${data.optimal_velocity}km/s | ` +
                `Quantum: ${data.quantum_advantage?.toFixed(1) || '-'}x`;

            // Update quantum card
            document.getElementById('optimal-index').textContent = data.optimal_index;
            document.getElementById('quantum-advantage').textContent = `${data.quantum_advantage?.toFixed(1) || '-'}x`;
            document.getElementById('success-prob').textContent = `${(data.success_probability * 100)?.toFixed(1) || '-'}%`;
            document.getElementById('optimal-params').innerHTML =
                `<strong>Optimal Solution:</strong> Velocity ${data.optimal_velocity} km/s, ` +
                `Angle ${data.optimal_angle}°`;
            break;

        case 'agent_3':
            resultEl.innerHTML = `Verdict: <strong>${data.verdict}</strong> | ` +
                `Fragmentation: ${data.fragmentation_risk_pct?.toFixed(1)}%`;

            // Update safety card
            document.getElementById('frag-risk').textContent = `${data.fragmentation_risk_pct?.toFixed(1) || '-'}%`;
            document.getElementById('miss-distance').textContent = `${data.miss_distance_km?.toLocaleString() || '-'} km`;
            document.getElementById('confidence').textContent = `${data.confidence_score?.toFixed(1) || '-'}%`;
            break;
    }
}

function updateVerdict(type, icon, text) {
    const banner = document.getElementById('verdict-banner');
    banner.className = 'verdict-banner ' + type;

    document.getElementById('verdict-icon').textContent = icon;
    document.getElementById('verdict-text').textContent = text;
}

function renderSimulationTable(candidates) {
    const tableBody = document.getElementById('simulation-body');
    const container = document.getElementById('simulation-card');

    if (!tableBody || !container) return;

    container.style.display = 'block';
    tableBody.innerHTML = '';

    candidates.forEach(c => {
        const row = document.createElement('tr');
        if (c.id === 0) row.style.color = '#4ade80'; // Green for best

        row.innerHTML = `
            <td>${c.id}</td>
            <td>${c.velocity_km_s}</td>
            <td>${c.angle_degrees}</td>
            <td>${c.score.toFixed(3)}</td>
            <td>${c.validity ? '✅' : '❌'}</td>
        `;
        tableBody.appendChild(row);
    });
}
