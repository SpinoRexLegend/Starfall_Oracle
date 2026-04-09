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
            console.log("Backend response:", data);

            nextSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

        } catch (error) {
            console.error("Error sending data:", error);
        }
    });

});