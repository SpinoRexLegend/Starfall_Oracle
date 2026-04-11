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

            // Update Quantum UI Histogram
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

            // Update final decision text
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

});