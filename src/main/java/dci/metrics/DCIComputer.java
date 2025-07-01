package dci.metrics;

import dci.graph.DynamicCallGraph;
import java.util.HashMap;
import java.util.Map;

public class DCIComputer {
    /**
     * Computes the DCI for each service using the squared weights formula.
     * @param graph the dynamic call graph
     * @return map of service name to DCI score
     */
    public Map<String, Double> computeDCI(DynamicCallGraph graph) {
        Map<String, Double> dciScores = new HashMap<>();
        for (String caller : graph.getAllServices()) {
            Map<String, Integer> outgoing = graph.getOutgoingCalls(caller);
            int total = outgoing.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) {
                dciScores.put(caller, 0.0);
                continue;
            }
            double dci = outgoing.values().stream()
                .mapToDouble(count -> {
                    double w = (double) count / total;
                    return w * w;
                })
                .sum();
            dciScores.put(caller, dci);
        }
        // TODO: Adapt RMT logic from MCI for more advanced normalization if needed
        return dciScores;
    }
} 