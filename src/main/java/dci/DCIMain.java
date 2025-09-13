package dci;

import dci.model.TraceCall;
import dci.parser.DCITraceParser;
import dci.graph.DynamicCallGraph;
import dci.metrics.DCIComputer;
import dci.output.CSVWriter;
import dci.visualization.GraphMLExporter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main application class for Dynamic Coupling Index (DCI) calculation using Relative Measurement Theory (RMT).
 * 
 * This class orchestrates the entire DCI analysis pipeline:
 * 1. Parse trace files (Zipkin/OpenTelemetry format)
 * 2. Build dynamic call graph from service interactions
 * 3. Calculate RMT-based DCI scores for each service
 * 4. Generate comprehensive output (CSV + GraphML visualization)
 * 
 * The RMT approach provides relative coupling measurement by comparing actual couplings
 * to the maximum possible couplings in the system, enabling fair comparison across
 * different system sizes and architectures.
 */
public class DCIMain {
    
    /**
     * Main entry point for the DCI analysis application.
     * 
     * This method implements the complete DCI calculation pipeline:
     * - Input validation and trace parsing
     * - Dynamic call graph construction
     * - RMT-based DCI computation
     * - Results export and visualization
     * 
     * @param args Command line arguments: [trace_file.json] [output.csv]
     */
    public static void main(String[] args) {
        // Step 1: Validate command line arguments
        validateArguments(args);
        
        String tracePath = args[0];
        String outputPath = args[1];

        // Step 2: Validate input file exists and is accessible
        File traceFile = validateInputFile(tracePath);

        // Step 3: Parse the trace file to extract service-to-service calls
        List<TraceCall> traceCalls = parseTraceFile(traceFile);

        // Step 4: Build the dynamic call graph from parsed trace data
        DynamicCallGraph graph = buildCallGraph(traceCalls);

        // Step 5: Compute RMT-based DCI scores for each service
        Map<String, Double> dciScores = computeDCIScores(graph);
        
        // Step 6: Display comprehensive system statistics
        displaySystemStatistics(graph, traceCalls);

        // Step 7: Write detailed results to CSV file
        writeCSVResults(dciScores, outputPath);

        // Step 8: Generate enhanced GraphML visualization for Gephi
        generateGraphMLVisualization(graph, dciScores);

        // Step 9: Display completion summary
        displayCompletionSummary(dciScores, outputPath);
    }
    
    /**
     * Validates command line arguments and provides usage information.
     * 
     * This method ensures the application receives the correct number of arguments
     * and provides helpful usage information if arguments are missing or incorrect.
     * 
     * @param args Command line arguments array
     */
    private static void validateArguments(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java dci.DCIMain <zipkin_trace.json> <output.csv>");
            System.err.println("Example: java dci.DCIMain zepkin_trace.json dci_output_rmt.csv");
            System.err.println("");
            System.err.println("Arguments:");
            System.err.println("  zipkin_trace.json  - Input trace file in Zipkin/OpenTelemetry format");
            System.err.println("  output.csv         - Output file for DCI results");
            System.exit(1);
        }
    }
    
    /**
     * Validates that the input trace file exists and is accessible.
     * 
     * This method performs basic file system checks to ensure the trace file
     * can be read before attempting to parse it, providing early error detection.
     *
     * @return File object representing the validated trace file
     */
    private static File validateInputFile(String tracePath) {
        File traceFile = new File(tracePath);
        if (!traceFile.exists()) {
            System.err.println("Error: Trace file not found: " + tracePath);
            System.err.println("Please ensure the file exists and the path is correct.");
            System.exit(1);
        }
        return traceFile;
    }
    
    /**
     * Parses the trace file to extract service-to-service call information.
     * 
     * This method processes Zipkin/OpenTelemetry JSON trace files and extracts
     * all service-to-service calls, aggregating call counts for each unique
     * caller-callee pair. The parser handles various trace formats and provides
     * detailed progress information during parsing.
     *
     * @return List of TraceCall objects representing service interactions
     */
    private static List<TraceCall> parseTraceFile(File traceFile) {
        System.out.println("Parsing trace file: " + traceFile.getName());
        
        DCITraceParser parser = new DCITraceParser();
        List<TraceCall> traceCalls = parser.parseTraces(traceFile);
        
        if (traceCalls.isEmpty()) {
            System.err.println("No service-to-service calls found in trace file.");
            System.err.println("Please ensure the trace file contains valid service interaction data.");
            System.exit(2);
        }
        
        System.out.println("Found " + traceCalls.size() + " service-to-service calls");
        return traceCalls;
    }
    
    /**
     * Builds the dynamic call graph from parsed trace data.
     * 
     * This method constructs a comprehensive graph representation of service
     * interactions, tracking all services in the system (including isolated ones)
     * and maintaining call frequency information. The graph serves as the foundation
     * for all subsequent DCI calculations.
     * 
     * @param traceCalls List of service-to-service calls from trace parsing
     * @return DynamicCallGraph containing all service relationships
     */
    private static DynamicCallGraph buildCallGraph(List<TraceCall> traceCalls) {
        System.out.println("Building dynamic call graph...");
        
        DynamicCallGraph graph = new DynamicCallGraph();
        for (TraceCall call : traceCalls) {
            graph.addCall(call);
        }
        
        return graph;
    }
    
    /**
     * Computes RMT-based DCI scores for each service in the system.
     * 
     * This method implements the core Relative Measurement Theory (RMT) calculation:
     * RMT_DCI = Actual_Couplings / Max_Possible_Couplings
     * 
     * The calculation considers all services in the system to provide a relative
     * coupling measurement that enables fair comparison across different system sizes.
     * 
     * @param graph The dynamic call graph containing service relationships
     * @return Map of service names to their RMT-based DCI scores
     */
    private static Map<String, Double> computeDCIScores(DynamicCallGraph graph) {
        System.out.println("Computing RMT-based DCI scores...");
        
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        return dciScores;
    }
    
    /**
     * Displays comprehensive system statistics for analysis and validation.
     * 
     * This method provides detailed information about the analyzed system,
     * including total services, active services (those making calls), isolated
     * services (those only receiving calls), and total service interactions.
     * This information helps validate the analysis and understand system structure.
     * 
     * @param graph The dynamic call graph
     * @param traceCalls List of all service calls for statistics
     */
    private static void displaySystemStatistics(DynamicCallGraph graph, List<TraceCall> traceCalls) {
        System.out.println("\nSystem Statistics:");
        System.out.println("  Total Services: " + graph.getTotalServicesInSystem());
        System.out.println("  Active Services: " + graph.getActiveServicesCount());
        System.out.println("  Isolated Services: " + graph.getIsolatedServicesCount());
        System.out.println("  Total Service Calls: " + traceCalls.size());
        

        System.out.println("\nCoupling Analysis Insights:");
        System.out.println("  - Services with high coupling (≥0.7): " + 
            graph.getAllServicesInSystem().stream()
                .mapToDouble(service -> {
                    DCIComputer computer = new DCIComputer();
                    return computer.computeDCI(graph).get(service);
                })
                .filter(dci -> dci >= 0.7)
                .count());
        System.out.println("  - Services with no coupling (0.0): " + 
            graph.getIsolatedServicesCount());
    }
    
    /**
     * Writes DCI results to CSV file for further analysis and reporting.
     * 
     * This method generates a comprehensive CSV file containing DCI scores,
     * coupling status classifications, and service information. The CSV format
     * enables easy import into spreadsheet applications, statistical tools,
     * and research analysis software.
     * 
     * @param dciScores Map of service names to DCI scores
     * @param outputPath Path for the output CSV file
     */
    private static void writeCSVResults(Map<String, Double> dciScores, String outputPath) {
        System.out.println("\nWriting results to: " + outputPath);
        
        CSVWriter writer = new CSVWriter();
        try {
            writer.writeDCIScores(dciScores, new File(outputPath));
            System.out.println("✓ DCI scores written successfully");
        } catch (Exception e) {
            System.err.println("Error writing CSV: " + e.getMessage());
            System.err.println("Please ensure the output directory is writable.");
            System.exit(3);
        }
    }
    
    /**
     * Generates enhanced GraphML visualization file for Gephi analysis.
     * 
     * This method creates a rich GraphML file with enhanced attributes including
     * node labels, colors, sizes, and edge weights. The visualization enables
     * interactive exploration of service coupling patterns in Gephi, providing
     * visual insights into system architecture and coupling relationships.
     * 
     * @param graph The dynamic call graph
     * @param dciScores Map of service names to DCI scores
     */
    private static void generateGraphMLVisualization(DynamicCallGraph graph, Map<String, Double> dciScores) {
        // Build status map for visualization
        Map<String, String> statusMap = new HashMap<>();
        DCIComputer computer = new DCIComputer();
        
        for (Map.Entry<String, Double> entry : dciScores.entrySet()) {
            String service = entry.getKey();
            double dci = entry.getValue();
            String status = computer.getStatus(dci);
            statusMap.put(service, status);
        }

        // Export enhanced GraphML for visualization
        try {
            GraphMLExporter.export(graph, statusMap, new File("output.graphml"));
            System.out.println("✓ GraphML file written to output.graphml");
            System.out.println("  - Open in Gephi for interactive visualization");
            System.out.println("  - Nodes are color-coded by coupling status");
            System.out.println("  - Node sizes reflect DCI scores");
            System.out.println("  - Edge thickness shows call frequency");
        } catch (Exception e) {
            System.err.println("Warning: Error writing GraphML: " + e.getMessage());
            System.err.println("CSV results are still available for analysis.");
        }
    }
    
    /**
     * Displays completion summary with key information about the analysis.
     * 
     * This method provides a final summary of the DCI analysis, including
     * the number of services analyzed, output file locations, and next steps
     * for further analysis or research validation.
     * 
     * @param dciScores Map of service names to DCI scores
     * @param outputPath Path to the generated CSV file
     */
    private static void displayCompletionSummary(Map<String, Double> dciScores, String outputPath) {
        System.out.println("\n=== DCI Analysis Complete ===");
        System.out.println("RMT-based Dynamic Coupling Index calculated for " + dciScores.size() + " services.");
        System.out.println("");
        System.out.println("Output files:");
        System.out.println("  - " + outputPath + " (CSV results)");
        System.out.println("  - output.graphml (visualization)");
        System.out.println("");
        System.out.println("Next steps:");
        System.out.println("  1. Review CSV results for coupling analysis");
        System.out.println("  2. Open GraphML file in Gephi for visualization");
        System.out.println("  3. Run validation script: python3 validate_rmt_dci.py " + outputPath);
        System.out.println("  4. Compare with MCI data for research validation");
    }
} 