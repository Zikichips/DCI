package dci.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * CSVWriter handles the generation of CSV output files for DCI analysis results.
 * 
 * This class provides functionality to export DCI scores and coupling status
 * information to CSV format, enabling easy import into spreadsheet applications,
 * statistical analysis tools, and research software. The CSV output includes
 * comprehensive information for each service analyzed.
 * 
 * Output Format:
 * - Service: Name of the service
 * - DCI: RMT-based Dynamic Coupling Index score (0.0 to 1.0)
 * - Status: Human-readable coupling classification
 * 
 * The CSV format enables researchers and practitioners to:
 * - Import results into Excel, Google Sheets, or R/Python for analysis
 * - Create custom visualizations and reports
 * - Perform statistical analysis on coupling patterns
 * - Compare results across different systems or time periods
 * 
 * @author Research Team
 * @version 1.0
 */
public class CSVWriter {
    
    /**
     * Write RMT-based DCI scores to CSV file for analysis and reporting.
     * 
     * This method generates a comprehensive CSV file containing DCI analysis
     * results for each service in the system. The output includes:
     * 
     * 1. Service names for identification
     * 2. DCI scores (formatted to 3 decimal places for precision)
     * 3. Coupling status classifications for quick interpretation
     * 
     * The CSV format is designed to be:
     * - Human-readable with clear column headers
     * - Compatible with common spreadsheet applications
     * - Suitable for statistical analysis and visualization
     * - Easy to process programmatically
     * 
     * Example output:
     * Service,DCI,Status
     * service-a,0.650,High Coupling
     * service-b,0.350,Moderate Coupling
     * service-c,0.100,Low Coupling
     * 
     * @param dciScores Map of service names to their RMT DCI scores
     * @param outputFile The file to write the CSV output to
     * @throws IOException If there's an error writing to the file
     */
    public void writeDCIScores(Map<String, Double> dciScores, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write CSV header with column names
            writer.write("Service,DCI,Status\n");
            
            // Write data rows for each service
            for (Map.Entry<String, Double> entry : dciScores.entrySet()) {
                String service = entry.getKey();
                double dci = entry.getValue();
                
                // Get human-readable status classification
                String status = getStatus(dci);
                
                // Format DCI score to 3 decimal places for consistency
                String dciFormatted = String.format("%.3f", dci);
                
                // Write CSV row: service name, DCI score, coupling status
                writer.write(service + "," + dciFormatted + "," + status + "\n");
            }
        }
    }
    
    /**
     * Get status classification for RMT DCI score.
     * 
     * This method provides a human-readable classification of coupling status
     * based on the RMT DCI score. The classification helps researchers and
     * practitioners quickly understand coupling patterns and identify potential
     * architectural concerns.
     * 
     * Classification thresholds are based on research and practical experience:
     * - High Coupling (≥0.7): Service is highly dependent on many other services
     *   - Indicates potential architectural concerns
     *   - May need refactoring to reduce dependencies
     *   - Could be a bottleneck or single point of failure
     * 
     * - Moderate Coupling (0.4-0.7): Balanced coupling with reasonable dependencies
     *   - Generally acceptable coupling levels
     *   - Service has meaningful interactions without being overly dependent
     *   - Good balance between functionality and independence
     * 
     * - Low Coupling (0.0-0.4): Minimal coupling, potentially isolated
     *   - Service has few dependencies on other services
     *   - May be well-encapsulated or potentially underutilized
     *   - Could indicate good design or missed integration opportunities
     * 
     * - No Coupling (0.0): Completely isolated service
     *   - Service has no outgoing calls to other services
     *   - May be a standalone service or endpoint service
     *   - Could indicate architectural isolation or potential integration issues
     * 
     * These thresholds can be adjusted based on specific research needs or
     * organizational preferences for coupling management.
     * 
     * @param dci The RMT DCI score (0.0 to 1.0)
     * @return Human-readable coupling status classification
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