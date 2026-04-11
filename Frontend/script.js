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

            console.log("Final Result:", data);

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
                outputText.innerText = `Asteroid ${asteroidID} has been analyzed. ${simData.length} potential simulation outcomes displayed. Optimal quantum amplitude factored in.`;
            }

            nextSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

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