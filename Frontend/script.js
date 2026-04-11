document.addEventListener("DOMContentLoaded", () => {

    const startBtn = document.querySelector(".start-btn");
    const inputField = document.querySelector(".id-input");
    const nextSection = document.querySelector(".analysis-section");

    startBtn.addEventListener("click", async () => {
        const asteroidID = inputField.value.trim();

        if (asteroidID === "") {
            inputField.focus();
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/asteroid", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    asteroidId: asteroidID
                })
            });

            const data = await response.json();

            if (!response.ok) {
                alert(data.message || "Failed to process asteroid");
                return;
            }


            const logBody = document.getElementById("log-body");
            const simData = data.simulations || (Array.isArray(data) ? data : []);

            if (logBody && simData.length > 0) {
                logBody.innerHTML = ""; // Clear existing table rows
                simData.forEach(sim => {
                    const simId = sim.SimulationID ? sim.SimulationID.substring(0, 8) + "..." : "Unknown";
                    const angle = sim["Angle at which force to be applied"] || "N/A";
                    const execTime = sim["Execution time for hitting the earth"] || "N/A";
                    const missDist = sim["Distance from which it will pass if force is applied at that angle"] || "N/A";
                    const fuel = sim["Fuel Cost"] || "N/A";

                    let success = false;
                    const successVal = sim["Chances of success"] || sim["Success"];
                    if (successVal !== undefined) {
                        success = (successVal === 1 || successVal === true || successVal === "1" || successVal === "true");
                    }

                    const newRow = document.createElement("tr");

                    let statusClass = success ? "threat-low" : "threat-high";
                    let statusText = success ? "SUCCESS" : "FAILURE";

                    newRow.innerHTML = `
                        <td>${simId}</td>
                        <td>${parseFloat(angle).toFixed(2)}°</td>
                        <td>${parseFloat(execTime).toFixed(2)}s</td>
                        <td>${parseFloat(missDist).toFixed(2)}</td>
                        <td>${parseFloat(fuel).toFixed(2)}</td>
                        <td class="${statusClass}">${statusText}</td>
                    `;

                    logBody.prepend(newRow);
                });
            }

            const histBars = document.querySelector(".hist-bars");
            if (histBars && data.quantum && data.quantum.bar_chart) {
                histBars.innerHTML = ""; // Wipe statically hardcoded CSS bars
                data.quantum.bar_chart.forEach(point => {
                    const highlightClass = point.isOptimal ? " highlight" : "";
                    const html = `
                        <div class="bar-group">
                            <div class="bar${highlightClass}" style="height:${point.probabilityHeight}%"></div>
                            <span class="bar-label">${point.label}</span>
                        </div>
                    `;
                    histBars.innerHTML += html;
                });
            }

            const outputText = document.getElementById("output-text");
            if (outputText && Object.keys(data).length > 0) {
                const risk = data.riskAssessment || {};
                const opt = data.optimalStrategy || {};
                
                const riskLevel = risk.impactProbability ? (parseFloat(risk.impactProbability) * 100).toFixed(1) + "%" : "Unknown";
                const impactEnergy = risk.kineticEnergyMT ? risk.kineticEnergyMT + " MT (" + risk.estimatedDamage + " Damage)" : "Unknown";
                
                const missDistKey = "Distance from which it will pass if force is applied at that angle";
                const timeKey = "Execution time for hitting the earth";
                const fuelKey = "Fuel Cost";
                
                const missDistance = opt[missDistKey] ? parseFloat(opt[missDistKey]).toFixed(2) + " km miss distance" : "Unknown";
                const fuelCost = opt[fuelKey] ? parseFloat(opt[fuelKey]).toFixed(2) + " fuel units over " + parseFloat(opt[timeKey]).toFixed(2) + "s" : "Unknown";
                
                let isSuccess = false;
                const successVal = opt["Chances of success"] || opt["Success"] || opt["success"];
                if (successVal !== undefined) {
                    isSuccess = (successVal === 1 || successVal === true || successVal === "1" || successVal === "true");
                }
                const finalOutcome = isSuccess ? "a safe and successful deflection" : "critical planetary compromise";

                outputText.innerText = `Asteroid ${asteroidID} analysis indicates a ${riskLevel} impact probability with an estimated damage potential of ${impactEnergy}.\n\nOptimal deflection strategy—validated through quantum-inspired optimization—achieves ${missDistance} at a cost of ${fuelCost}, ensuring ${finalOutcome}.`;
            }

            const hiddenLayers = document.getElementById("hidden-analysis-layers");
            if (hiddenLayers) {
                hiddenLayers.style.display = "block";
                // Force a reflow to make the opacity transition work smoothly
                void hiddenLayers.offsetWidth; 
                hiddenLayers.style.opacity = "1";
            }

            // Small delay to allow the layout to render the display change before scrolling
            setTimeout(() => {
                nextSection.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
            }, 100);

        } catch (error) {
            console.error("Error:", error);
        }
    });

    const glowCard = document.getElementById('final-decision-card');

    if (glowCard) {
        function getCenterOfElement(el) {
            const rect = el.getBoundingClientRect();
            return [rect.width / 2, rect.height / 2];
        }

        function getEdgeProximity(el, x, y) {
            const [cx, cy] = getCenterOfElement(el);
            const dx = x - cx;
            const dy = y - cy;
            let kx = Infinity;
            let ky = Infinity;
            if (dx !== 0) kx = cx / Math.abs(dx);
            if (dy !== 0) ky = cy / Math.abs(dy);
            return Math.min(Math.max(1 / Math.min(kx, ky), 0), 1);
        }

        function getCursorAngle(el, x, y) {
            const [cx, cy] = getCenterOfElement(el);
            const dx = x - cx;
            const dy = y - cy;
            if (dx === 0 && dy === 0) return 0;
            const radians = Math.atan2(dy, dx);
            let degrees = radians * (180 / Math.PI) + 90;
            if (degrees < 0) degrees += 360;
            return degrees;
        }

        glowCard.addEventListener('pointermove', (e) => {
            const rect = glowCard.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const edge = getEdgeProximity(glowCard, x, y);
            const angle = getCursorAngle(glowCard, x, y);

            const edgePercentage = Math.max((edge * 100), 20);

            glowCard.style.setProperty('--edge-proximity', edgePercentage.toFixed(1));
            glowCard.style.setProperty('--cursor-angle', `${angle.toFixed(1)}deg`);
        });

        glowCard.addEventListener('pointerleave', () => {
            glowCard.style.setProperty('--edge-proximity', '0');
        });
    }

});