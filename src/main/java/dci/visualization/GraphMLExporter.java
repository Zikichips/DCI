package dci.visualization;

import dci.graph.DynamicCallGraph;
import dci.metrics.DCIComputer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GraphMLExporter {
    
    /**
     * Exports the dynamic call graph to GraphML format with enhanced visualization attributes.
     * This creates a rich GraphML file that displays properly in Gephi with labels, colors, and sizing.
     * 
     * @param callGraph The dynamic call graph containing service relationships
     * @param statusMap Map of service names to their coupling status
     * @param outputFile The output GraphML file
     * @throws IOException If there's an error writing the file
     */
    public static void export(DynamicCallGraph callGraph, Map<String, String> statusMap, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write GraphML header with enhanced attribute definitions
            writeGraphMLHeader(writer);
            
            // Write graph opening tag
            writer.write("  <graph id=\"G\" edgedefault=\"directed\">\n");

            // Export all nodes with enhanced attributes
            exportNodes(writer, callGraph, statusMap);

            // Export all edges with enhanced attributes
            exportEdges(writer, callGraph);

            // Close graph and GraphML
            writer.write("  </graph>\n");
            writer.write("</graphml>\n");
        }
    }
    
    /**
     * Writes the GraphML header with all necessary attribute definitions.
     * These definitions tell Gephi how to interpret and display the data.
     * 
     * @param writer The buffered writer for the GraphML file
     * @throws IOException If there's an error writing
     */
    private static void writeGraphMLHeader(BufferedWriter writer) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
        
        // Node attributes - using Gephi's standard naming conventions
        writer.write("  <key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");
        writer.write("  <key id=\"status\" for=\"node\" attr.name=\"status\" attr.type=\"string\"/>\n");
        writer.write("  <key id=\"dci_score\" for=\"node\" attr.name=\"dci_score\" attr.type=\"double\"/>\n");
        writer.write("  <key id=\"size\" for=\"node\" attr.name=\"size\" attr.type=\"double\"/>\n");
        writer.write("  <key id=\"color\" for=\"node\" attr.name=\"color\" attr.type=\"string\"/>\n");
        writer.write("  <key id=\"r\" for=\"node\" attr.name=\"r\" attr.type=\"int\"/>\n");
        writer.write("  <key id=\"g\" for=\"node\" attr.name=\"g\" attr.type=\"int\"/>\n");
        writer.write("  <key id=\"b\" for=\"node\" attr.name=\"b\" attr.type=\"int\"/>\n");
        writer.write("  <key id=\"incoming_calls\" for=\"node\" attr.name=\"incoming_calls\" attr.type=\"int\"/>\n");
        writer.write("  <key id=\"outgoing_calls\" for=\"node\" attr.name=\"outgoing_calls\" attr.type=\"int\"/>\n");
        writer.write("  <key id=\"x\" for=\"node\" attr.name=\"x\" attr.type=\"double\"/>\n");
        writer.write("  <key id=\"y\" for=\"node\" attr.name=\"y\" attr.type=\"double\"/>\n");
        
        // Edge attributes - using Gephi's standard naming conventions
        writer.write("  <key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        writer.write("  <key id=\"calls\" for=\"edge\" attr.name=\"calls\" attr.type=\"int\"/>\n");
    }
    
    /**
     * Exports all nodes with enhanced attributes for better visualization.
     * Each node includes label, status, DCI score, size, color, and call statistics.
     * 
     * @param writer The buffered writer for the GraphML file
     * @param callGraph The dynamic call graph
     * @param statusMap Map of service names to coupling status
     * @throws IOException If there's an error writing
     */
    private static void exportNodes(BufferedWriter writer, DynamicCallGraph callGraph, Map<String, String> statusMap) throws IOException {
        // Calculate DCI scores for all services
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(callGraph);
        
        // Get all services for positioning
        String[] services = callGraph.getAllServicesInSystem().toArray(new String[0]);
        
        // Export each service as a node
        for (int i = 0; i < services.length; i++) {
            String service = services[i];
            String status = statusMap.getOrDefault(service, "Unknown");
            double dciScore = dciScores.getOrDefault(service, 0.0);
            
            // Calculate node size based on DCI score (10 to 50 range for better visibility in Gephi)
            double nodeSize = 10.0 + (dciScore * 40.0);
            
            // Determine node color based on coupling status
            String nodeColor = getNodeColor(status);
            int[] rgbValues = hexToRgb(nodeColor);
            
            // Get call statistics
            int incomingCalls = callGraph.getTotalIncomingCalls(service);
            int outgoingCalls = callGraph.getTotalOutgoingCalls(service);
            
            // Calculate position in a circle layout for better initial display
            double angle = (2 * Math.PI * i) / services.length;
            double radius = 100.0;
            double x = 200.0 + radius * Math.cos(angle);
            double y = 200.0 + radius * Math.sin(angle);
            
            // Write node with all attributes (no graphics elements)
            writer.write("    <node id=\"" + service + "\">\n");
            writer.write("      <data key=\"label\">" + service + "</data>\n");
            writer.write("      <data key=\"status\">" + status + "</data>\n");
            writer.write("      <data key=\"dci_score\">" + String.format("%.3f", dciScore) + "</data>\n");
            writer.write("      <data key=\"size\">" + String.format("%.2f", nodeSize) + "</data>\n");
            writer.write("      <data key=\"color\">" + nodeColor + "</data>\n");
            writer.write("      <data key=\"r\">" + rgbValues[0] + "</data>\n");
            writer.write("      <data key=\"g\">" + rgbValues[1] + "</data>\n");
            writer.write("      <data key=\"b\">" + rgbValues[2] + "</data>\n");
            writer.write("      <data key=\"incoming_calls\">" + incomingCalls + "</data>\n");
            writer.write("      <data key=\"outgoing_calls\">" + outgoingCalls + "</data>\n");
            writer.write("      <data key=\"x\">" + String.format("%.2f", x) + "</data>\n");
            writer.write("      <data key=\"y\">" + String.format("%.2f", y) + "</data>\n");
            writer.write("    </node>\n");
        }
    }
    
    /**
     * Exports all edges with enhanced attributes for better visualization.
     * Each edge includes weight (based on call count) and call count information.
     * 
     * @param writer The buffered writer for the GraphML file
     * @param callGraph The dynamic call graph
     * @throws IOException If there's an error writing
     */
    private static void exportEdges(BufferedWriter writer, DynamicCallGraph callGraph) throws IOException {
        int edgeId = 0;
        
        // Export each service call as an edge
        for (String caller : callGraph.getAllServicesInSystem()) {
            for (Map.Entry<String, Integer> entry : callGraph.getOutgoingCalls(caller).entrySet()) {
                String callee = entry.getKey();
                int callCount = entry.getValue();
                
                // Calculate edge weight (normalize call count for better visualization)
                double edgeWeight = Math.max(0.1, Math.min(5.0, callCount / 10.0));
                
                writer.write("    <edge id=\"e" + (edgeId++) + "\" source=\"" + caller + "\" target=\"" + callee + "\">\n");
                writer.write("      <data key=\"weight\">" + String.format("%.2f", edgeWeight) + "</data>\n");
                writer.write("      <data key=\"calls\">" + callCount + "</data>\n");
                writer.write("    </edge>\n");
            }
        }
    }
    
    /**
     * Determines the color for a node based on its coupling status.
     * This provides visual distinction between different coupling levels in Gephi.
     * 
     * @param status The coupling status of the service
     * @return A hex color code for the node
     */
    private static String getNodeColor(String status) {
        switch (status) {
            case "High Coupling":
                return "#ff0000"; // Red for high coupling
            case "Moderate Coupling":
                return "#ff8800"; // Orange for moderate coupling
            case "Low Coupling":
                return "#ffff00"; // Yellow for low coupling
            case "No Coupling":
                return "#888888"; // Gray for no coupling
            default:
                return "#cccccc"; // Light gray for unknown status
        }
    }
    
    /**
     * Converts a hex color code (e.g., #ff0000) to an array of RGB values (0-255).
     * 
     * @param hex The hex color code (e.g., #ff0000)
     * @return An array of three integers [r, g, b]
     */
    private static int[] hexToRgb(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }
}

