package dci.metrics;

import dci.graph.DynamicCallGraph;
import java.util.HashMap;
import java.util.Map;

public class DCIComputer {
    
    /**
     * Computes RMT-based DCI for each service using Relative Measurement Theory.
     * RMT DCI = (Actual Coupling / Max Possible Coupling)
     * @param graph the dynamic call graph
     * @return map of service name to RMT DCI score
     */
    public Map<String, Double> computeDCI(DynamicCallGraph graph) {
        Map<String, Double> dciScores = new HashMap<>();
        int totalServices = graph.getTotalServicesInSystem();
        int maxPossibleCoupling = Math.max(0, totalServices - 1); // Can couple to all others except itself
        
        for (String service : graph.getAllServicesInSystem()) {
            Map<String, Integer> outgoingCalls = graph.getOutgoingCalls(service);
            double dci = calculateRMTCoupling(service, outgoingCalls, maxPossibleCoupling);
            dciScores.put(service, dci);
        }
        
        return dciScores;
    }
    
    /**
     * Calculate RMT coupling for a service
     * RMT = (Actual Couplings / Max Possible Couplings)
     */
    private double calculateRMTCoupling(String serviceName, Map<String, Integer> outgoingCalls, int maxPossibleCoupling) {
        int actualCouplings = outgoingCalls.size();
        
        if (maxPossibleCoupling == 0) return 0.0;
        return (double) actualCouplings / maxPossibleCoupling;
    }
    
    /**
     * Get RMT-based status for a service.
     * @param rmtScore the RMT coupling score
     * @return RMT-based coupling status
     */
    public String getStatus(double rmtScore) {
        if (rmtScore >= 0.7) {
            return "High Coupling";
        } else if (rmtScore >= 0.4) {
            return "Moderate Coupling";
        } else if (rmtScore > 0) {
            return "Low Coupling";
        } else {
            return "No Coupling";
        }
    }
} 