package dci;

import dci.model.TraceCall;
import dci.parser.DCITraceParser;
import dci.graph.DynamicCallGraph;
import dci.metrics.DCIComputer;
import dci.output.CSVWriter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DCIMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java dci.DCIMain <zipkin_trace.json> <output.csv>");
            System.exit(1);
        }
        String tracePath = args[0];
        String outputPath = args[1];

        // 1. Parse the trace file
        DCITraceParser parser = new DCITraceParser();
        List<TraceCall> traceCalls = parser.parseTraces(new File(tracePath));
        if (traceCalls.isEmpty()) {
            System.err.println("No service-to-service calls found in trace file.");
            System.exit(2);
        }

        // 2. Build the dynamic call graph
        DynamicCallGraph graph = new DynamicCallGraph();
        for (TraceCall call : traceCalls) {
            graph.addCall(call);
        }

        // 3. Compute DCI scores
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);

        // 4. Write results to CSV
        CSVWriter writer = new CSVWriter();
        try {
            writer.writeDCIScores(dciScores, new File(outputPath));
            System.out.println("DCI scores written to " + outputPath);
        } catch (Exception e) {
            System.err.println("Error writing CSV: " + e.getMessage());
            System.exit(3);
        }
    }
} 