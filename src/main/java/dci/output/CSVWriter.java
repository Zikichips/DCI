package dci.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CSVWriter {
    
    /**
     * Write RMT-based DCI scores to CSV
     */
    public void writeDCIScores(Map<String, Double> dciScores, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Service,DCI,Status\n");
            for (Map.Entry<String, Double> entry : dciScores.entrySet()) {
                String service = entry.getKey();
                double dci = entry.getValue();
                String status = getStatus(dci);
                String dciFormatted = String.format("%.3f", dci);
                writer.write(service + "," + dciFormatted + "," + status + "\n");
            }
        }
    }
    
    /**
     * Get status for RMT DCI score
     */
    private String getStatus(double dci) {
        if (dci >= 0.7) {
            return "High Coupling";
        } else if (dci >= 0.4) {
            return "Moderate Coupling";
        } else if (dci > 0) {
            return "Low Coupling";
        } else {
            return "No Coupling";
        }
    }
} 