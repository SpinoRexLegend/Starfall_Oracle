import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {

    private final String fileName;
    private final String[] headers = {"Simulation_ID", "ImpactAngle", "ExecutionTime", "MissedDistance", "Success"};

    public CsvHandler() {
        this.fileName = "D:\\PersonalProject\\Starfall_Oracle\\src\\main\\CSV_Files\\Simulation.csv";
    }

    public void writeRow(String[] values) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(String.join(",", values));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeCsv() {
        if (!file.exists() || file.length() == 0) {
            try (FileWriter fw = new FileWriter(file);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {

                out.println(String.join(",", headers));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}