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

public class DCIMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java dci.DCIMain <zipkin_trace.json> <output.csv>");
            System.err.println("Example: java dci.DCIMain zepkin_trace.json dci_output_rmt.csv");
            System.exit(1);
        }
        
        String tracePath = args[0];
        String outputPath = args[1];

        // Validate input file exists
        File traceFile = new File(tracePath);
        if (!traceFile.exists()) {
            System.err.println("Error: Trace file not found: " + tracePath);
            System.exit(1);
        }

        // 1. Parse the trace file
        System.out.println("Parsing trace file: " + tracePath);
        DCITraceParser parser = new DCITraceParser();
        List<TraceCall> traceCalls = parser.parseTraces(traceFile);
        if (traceCalls.isEmpty()) {
            System.err.println("No service-to-service calls found in trace file.");
            System.exit(2);
        }
        System.out.println("Found " + traceCalls.size() + " service-to-service calls");

        // 2. Build the dynamic call graph
        System.out.println("Building dynamic call graph...");
        DynamicCallGraph graph = new DynamicCallGraph();
        for (TraceCall call : traceCalls) {
            graph.addCall(call);
        }

        // 3. Compute RMT-based DCI scores
        System.out.println("Computing RMT-based DCI scores...");
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        // 4. Print basic statistics
        System.out.println("\nSystem Statistics:");
        System.out.println("  Total Services: " + graph.getTotalServicesInSystem());
        System.out.println("  Active Services: " + graph.getActiveServicesCount());
        System.out.println("  Isolated Services: " + graph.getIsolatedServicesCount());
        System.out.println("  Total Service Calls: " + traceCalls.size());

        // 5. Write results to CSV
        System.out.println("\nWriting results to: " + outputPath);
        CSVWriter writer = new CSVWriter();
        try {
            writer.writeDCIScores(dciScores, new File(outputPath));
            System.out.println("✓ DCI scores written successfully");
        } catch (Exception e) {
            System.err.println("Error writing CSV: " + e.getMessage());
            System.exit(3);
        }

        // 6. Build status map for visualization
        Map<String, String> statusMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : dciScores.entrySet()) {
            String service = entry.getKey();
            double dci = entry.getValue();
            String status = computer.getStatus(dci);
            statusMap.put(service, status);
        }

        // 7. Export GraphML for visualization
        try {
            GraphMLExporter.export(graph, statusMap, new File("output.graphml"));
            System.out.println("✓ GraphML file written to output.graphml");
        } catch (Exception e) {
            System.err.println("Warning: Error writing GraphML: " + e.getMessage());
        }

        // 8. Print summary
        System.out.println("\n=== DCI Analysis Complete ===");
        System.out.println("RMT-based Dynamic Coupling Index calculated for " + dciScores.size() + " services.");
        System.out.println("Output files:");
        System.out.println("  - " + outputPath + " (CSV results)");
        System.out.println("  - output.graphml (visualization)");
    }
} 