package dci.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CSVWriter {
    public void writeDCIScores(Map<String, Double> dciScores, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Service,DCI,Status\n");
            for (Map.Entry<String, Double> entry : dciScores.entrySet()) {
                String service = entry.getKey();
                double dci = entry.getValue();
                String status;
                if (dci >= 0.8) {
                    status = "Highly Coupled";
                } else if (dci <= 0.3 && dci > 0) {
                    status = "Distributed Coupling";
                } else if (dci > 0.3 && dci < 0.8) {
                    status = "Moderate Coupling";
                } else {
                    status = "No Outgoing Calls";
                }
                String dciFormatted = String.format("%.2f", dci);
                writer.write(service + "," + dciFormatted + "," + status + "\n");
            }
        }
    }
} 