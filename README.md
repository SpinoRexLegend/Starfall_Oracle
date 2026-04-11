# Project Starfall

**Project Starfall** is an advanced asteroid threat simulation and quantum analysis platform. It provides a robust end-to-end pipeline to evaluate potential asteroid impacts, simulate deflection strategies using machine learning, and optimize the final decision-making process using a quantum-inspired algorithm.

## Overview

The application combines a visually rich frontend for user interaction with a powerful Java Spring Boot backend that delegates complex analytical tasks to an external Python-based machine learning service. 

It handles:
1. **Simulation of Deflection Strategies**: Evaluates various angles, fuel costs, and required execute times to successfully dodge an asteroid.
2. **Risk Assessment**: Computes a risk score taking into account the probability of impact, kinetic energy, estimating potential damage and evaluating if deflection is required based on the optimal simulation result.
3. **Quantum Optimization**: Employs a quantum-inspired algorithm to choose the safest, most resource-efficient strategy among multiple candidate simulations.

## Architecture & Technology Stack

* **Frontend**: HTML5, Vanilla JavaScript, and an extensive custom CSS implementation resulting in a cinematic, highly interactive interface. Includes grid layers, animated histograms, and dynamic tabular data rendering.
* **Backend**: Java 23, powered by the **Spring Boot** framework (v3.2.4).
    * `AsteroidController`: Exposes the initial REST API to receive the Asteroid ID from the frontend.
    * `AsteroidChecker` (Core Logic): Interacts with the database to gather details, pushes the data to the ML layer, computes bounds, updates databases and manages the risk/quantum pipelines.
    * `MLController` & `MLAnalysisService`: Serves as the bridge to a separate Python ML service using Spring's `RestTemplate` for JSON payload exchange.
    * `DatabaseController`: Manages interactions natively with the relational database.
* **Persistence Layer**: **Hibernate ORM** with **MySQL** handles the storage of entities representing the asteroid, multiple simulation runs, risk assessments, and the final quantum optimized choices.
* **External AI/ML Process**: Relies on a standalone Python HTTP service (runs on port 5000) for running heavy numerical modeling, risk scoring models, and mock quantum probability optimization.

## Key Features

* **Real-time API Delegation**: Spring Boot effortlessly handles the frontend's REST calls and delegates the heavy lifting to Python processes.
* **Simulation Result Processing**: Automatically sorts through dozens of potential impact simulations to determine the "Best Strategy" (e.g. lowest fuel cost, highest distance missed) while deliberately discarding/hiding the optimal one from the user until the quantum check.
* **Quantum Histogram Visuals**: Displays the exact likelihood optimization graph from the algorithm dynamically rendering HTML CSS bars based on probabilities fetched post-operation.
* **Aesthetic Dashboard**: Custom cinematic design complete with background videos (Earth, Black Hole phases), dynamic CSS transitions, and futuristic terminal-like logging overlays.

## Setup Requirements

* Java 23 JDK
* Maven
* MySQL 9+
* Node/Live Server (For quick Frontend hosting)
* The external Python ML Service endpoint running at `http://localhost:5000` (Provides `/predict`, `/assess`, and `/quantum` APIs)
