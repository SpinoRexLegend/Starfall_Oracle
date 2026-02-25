package com.example.frontend.starfall_prophecy.Simulations;

import java.io.*;

public class SimulationManager {

    private String fileName;

    public SimulationManager() {
        this.fileName = "D:\\PersonalProject\\Starfall_Oracle\\src\\main\\CSV_Files\\Simulation.csv";
    }

    public String[] readAsteroidData(String asteroidId) {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length > 0 && values[0].replace("\"", "").equals(asteroidId)) {
                    String diameter = values[2].replace("\"", "");
                    String velocity = values[3].replace("\"", "");
                    String composition = values[4].replace("\"", "");
                    return new String[]{diameter, velocity, composition};
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null; // not found
    }
}