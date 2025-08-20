package dci.visualization;

import dci.graph.DynamicCallGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GraphMLExporter {
    public static void export(DynamicCallGraph callGraph, Map<String, String> statusMap, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
            writer.write("  <key id=\"status\" for=\"node\" attr.name=\"status\" attr.type=\"string\"/>\n");
            writer.write("  <key id=\"calls\" for=\"edge\" attr.name=\"calls\" attr.type=\"int\"/>\n");
            writer.write("  <graph id=\"G\" edgedefault=\"directed\">\n");

            // Nodes - include ALL services in system (including isolated ones)
            for (String service : callGraph.getAllServicesInSystem()) {
                String status = statusMap.getOrDefault(service, "");
                writer.write("    <node id=\"" + service + "\">\n");
                writer.write("      <data key=\"status\">" + status + "</data>\n");
                writer.write("    </node>\n");
            }

            // Edges
            int edgeId = 0;
            for (String caller : callGraph.getAllServicesInSystem()) {
                for (Map.Entry<String, Integer> entry : callGraph.getOutgoingCalls(caller).entrySet()) {
                    String callee = entry.getKey();
                    int count = entry.getValue();
                    writer.write("    <edge id=\"e" + (edgeId++) + "\" source=\"" + caller + "\" target=\"" + callee + "\">\n");
                    writer.write("      <data key=\"calls\">" + count + "</data>\n");
                    writer.write("    </edge>\n");
                }
            }

            writer.write("  </graph>\n");
            writer.write("</graphml>\n");
        }
    }
}

