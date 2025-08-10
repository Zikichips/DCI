package dci.visualization;

import dci.graph.DynamicCallGraph;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.util.Map;

public class DCIVisualizer {
    public static void visualize(DynamicCallGraph callGraph, Map<String, String> statusMap) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Service Call Graph");

        // Add nodes with status as attribute
        for (String service : callGraph.getAllServices()) {
            Node node = graph.addNode(service);
            node.setAttribute("ui.label", service);
            String status = statusMap.getOrDefault(service, "");
            if ("Highly Coupled".equals(status)) node.setAttribute("ui.class", "high");
            else if ("Distributed Coupling".equals(status)) node.setAttribute("ui.class", "dist");
            else if ("Moderate Coupling".equals(status)) node.setAttribute("ui.class", "mod");
            else if ("No Outgoing Calls".equals(status)) node.setAttribute("ui.class", "none");
        }

        // Add edges with call counts as labels
        for (String caller : callGraph.getAllServices()) {
            for (Map.Entry<String, Integer> entry : callGraph.getOutgoingCalls(caller).entrySet()) {
                String callee = entry.getKey();
                int count = entry.getValue();
                String edgeId = caller + "->" + callee;
                if (graph.getEdge(edgeId) == null) {
                    Edge edge = graph.addEdge(edgeId, caller, callee, true);
                    edge.setAttribute("ui.label", String.valueOf(count));
                }
            }
        }

        // Style for coloring nodes
        graph.setAttribute("ui.stylesheet",
                "node.high { fill-color: red; }" +
                        "node.dist { fill-color: green; }" +
                        "node.mod { fill-color: orange; }" +
                        "node.none { fill-color: gray; }" +
                        "node { text-size: 16; }" +
                        "edge { text-size: 14; }"
        );

        graph.display();
    }
}
