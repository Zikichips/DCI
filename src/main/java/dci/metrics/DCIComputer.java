package dci.metrics;

import dci.graph.DynamicCallGraph;
import java.util.HashMap;
import java.util.Map;

/**
 * DCIComputer implements the core Relative Measurement Theory (RMT) calculation
 * for Dynamic Coupling Index (DCI) computation.
 * 
 * This class provides the mathematical foundation for calculating coupling metrics
 * that are relative to the system context, enabling fair comparison across
 * different system sizes and architectures.
 * 
 * Key Concepts:
 * - RMT DCI = Actual_Couplings / Max_Possible_Couplings
 * - Actual Couplings: Number of unique services this service calls
 * - Max Possible Couplings: Total services in system minus 1 (can't call itself)
 * - Relative Measurement: Enables comparison across different system sizes
 * 
 * The RMT approach addresses the limitation of absolute coupling metrics by
 * providing a normalized measure that considers the system-wide context.
 * 
 * @author Research Team
 * @version 1.0
 */
public class DCIComputer {
    
    /**
     * Computes RMT-based DCI for each service using Relative Measurement Theory.
     * 
     * This is the core method that implements the RMT formula for dynamic coupling
     * measurement. For each service in the system, it calculates:
     * 
     * RMT_DCI = Actual_Couplings / Max_Possible_Couplings
     * 
     * Where:
     * - Actual_Couplings: Number of unique services this service calls
     * - Max_Possible_Couplings: Total services in system minus 1
     * 
     * The result is a value between 0.0 and 1.0, where:
     * - 0.0 = No coupling (service calls no other services)
     * - 1.0 = Maximum coupling (service calls all other services)
     * - Values in between represent relative coupling strength
     * 
     * This relative approach enables fair comparison across systems of different sizes
     * and provides insights into coupling patterns that absolute metrics cannot.
     * 
     * @param graph The dynamic call graph containing all service relationships
     * @return Map of service name to RMT DCI score (0.0 to 1.0)
     */
    public Map<String, Double> computeDCI(DynamicCallGraph graph) {
        Map<String, Double> dciScores = new HashMap<>();
        
        // Calculate the maximum possible couplings in the system
        // A service can couple to all other services except itself
        int totalServices = graph.getTotalServicesInSystem();
        int maxPossibleCoupling = Math.max(0, totalServices - 1);
        
        // Compute DCI for each service in the system
        for (String service : graph.getAllServicesInSystem()) {
            // Get all outgoing calls for this service
            Map<String, Integer> outgoingCalls = graph.getOutgoingCalls(service);
            
            // Calculate RMT coupling for this service
            double dci = calculateRMTCoupling(service, outgoingCalls, maxPossibleCoupling);
            dciScores.put(service, dci);
        }
        
        return dciScores;
    }
    
    /**
     * Calculate RMT coupling for a specific service.
     * 
     * This method implements the core RMT formula for a single service:
     * RMT = (Actual Couplings / Max Possible Couplings)
     * 
     * The calculation process:
     * 1. Count unique services this service calls (actual couplings)
     * 2. Divide by maximum possible couplings in the system
     * 3. Handle edge cases (empty system, single service)
     * 
     * Key insights from this calculation:
     * - Multiple calls to the same service count as one coupling
     * - Isolated services (no outgoing calls) have DCI = 0.0
     * - Services calling all others have DCI = 1.0
     * - Relative measurement enables cross-system comparison
     * 
     * @param serviceName The name of the service being analyzed
     * @param outgoingCalls Map of callee service names to call counts
     * @param maxPossibleCoupling Maximum possible couplings in the system
     * @return RMT DCI score between 0.0 and 1.0
     */
    private double calculateRMTCoupling(String serviceName, Map<String, Integer> outgoingCalls, int maxPossibleCoupling) {
        // Count actual couplings (unique services called)
        // Note: Multiple calls to same service count as one coupling
        int actualCouplings = outgoingCalls.size();
        
        // Handle edge case: no possible couplings (single service system)
        if (maxPossibleCoupling == 0) {
            return 0.0;
        }
        
        // Calculate relative coupling using RMT formula
        return (double) actualCouplings / maxPossibleCoupling;
    }
    
    /**
     * Get RMT-based status classification for a service.
     * 
     * This method provides a human-readable classification of coupling status
     * based on the RMT DCI score. The classification helps researchers and
     * practitioners quickly understand coupling patterns and identify potential
     * architectural concerns.
     * 
     * Classification thresholds are based on research and practical experience:
     * - High Coupling (≥0.7): Service is highly dependent on many other services
     * - Moderate Coupling (0.4-0.7): Balanced coupling with reasonable dependencies
     * - Low Coupling (0.0-0.4): Minimal coupling, potentially isolated
     * - No Coupling (0.0): Completely isolated service
     * 
     * These thresholds can be adjusted based on specific research needs or
     * organizational preferences for coupling management.
     * 
     * @param rmtScore The RMT coupling score (0.0 to 1.0)
     * @return Human-readable coupling status classification
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