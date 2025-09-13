package dci.graph;

import dci.model.TraceCall;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DynamicCallGraph represents the runtime service interaction graph for DCI analysis.
 * 
 * This class maintains a comprehensive graph of service-to-service calls extracted
 * from trace data, tracking both active services (those making calls) and isolated
 * services (those only receiving calls). The graph serves as the foundation for
 * all coupling calculations and provides insights into system architecture.
 * 
 * Key Features:
 * - Tracks all services in the system (active + isolated)
 * - Maintains call frequency information
 * - Provides statistics for system analysis
 * - Supports both incoming and outgoing call analysis
 * 
 * The graph structure enables efficient querying of service relationships and
 * provides the data foundation for RMT-based coupling calculations.
 */
public class DynamicCallGraph {
    /**
     * Main call graph structure: caller -> (callee -> call_count)
     * This map tracks all outgoing calls from each service, with call frequency
     * information for each service-to-service relationship.
     */
    private Map<String, Map<String, Integer>> callGraph = new HashMap<>();
    
    /**
     * Set of all services in the system, including both active services
     * (those making calls) and isolated services (those only receiving calls).
     * This ensures complete system coverage for RMT calculations.
     */
    private Set<String> allServices = new HashSet<>();

    /**
     * Default constructor initializes an empty dynamic call graph.
     */
    public DynamicCallGraph() {
    }

    /**
     * Adds a service-to-service call to the dynamic call graph.
     * 
     * This method processes individual trace calls and builds the comprehensive
     * service interaction graph. For each call, it:
     * 1. Tracks both caller and callee services
     * 2. Maintains call frequency counts
     * 3. Ensures complete service coverage for RMT calculations
     * 
     * The method handles duplicate calls by aggregating call counts, ensuring
     * accurate frequency information for coupling analysis.
     * 
     * @param call The TraceCall object containing caller, callee, and count information
     */
    public void addCall(TraceCall call) {
        // Track all services (both caller and callee) for complete system coverage
        // This ensures isolated services are included in RMT calculations
        allServices.add(call.getCaller());
        allServices.add(call.getCallee());
        
        // Add or update the call relationship in the graph
        // If the caller doesn't exist, create a new map for their outgoing calls
        // If the callee already exists, add to the existing call count
        callGraph.computeIfAbsent(call.getCaller(), k -> new HashMap<>())
                 .merge(call.getCallee(), call.getCount(), Integer::sum);
    }

    /**
     * Returns the complete call graph structure for advanced analysis.
     * 
     * This method provides access to the internal graph representation,
     * enabling custom analysis and visualization of service relationships.
     * The returned structure is: caller -> (callee -> call_count)
     * 
     * @return Complete call graph as a nested map structure
     */
    public Map<String, Map<String, Integer>> getCallGraph() {
        return callGraph;
    }

    /**
     * Returns all services that have made outgoing calls (active services).
     * 
     * These are services that have initiated calls to other services,
     * representing the "caller" side of service interactions.
     * 
     * @return Set of service names that have made outgoing calls
     */
    public Set<String> getAllServices() {
        return callGraph.keySet();
    }
    
    /**
     * Returns all services in the system, including isolated ones.
     * 
     * This method provides complete system coverage by including:
     * - Active services: Services that have made outgoing calls
     * - Isolated services: Services that only receive calls but never make them
     * 
     * Complete service coverage is essential for accurate RMT calculations,
     * as the maximum possible couplings depends on the total number of services.
     * 
     * @return Set of all service names in the system
     */
    public Set<String> getAllServicesInSystem() {
        return new HashSet<>(allServices);
    }
    
    /**
     * Returns services that have made calls (active services).
     * 
     * These services have initiated at least one call to another service,
     * indicating they are actively participating in service interactions.
     * 
     * @return Set of service names that have made outgoing calls
     */
    public Set<String> getActiveServices() {
        return new HashSet<>(callGraph.keySet());
    }
    
    /**
     * Returns services that exist but have made no outgoing calls (isolated services).
     * 
     * These services are part of the system but only receive calls from other services.
     * They represent potential architectural concerns or services that may need
     * attention for coupling optimization.
     * 
     * Isolated services are calculated as: all_services - active_services
     * 
     * @return Set of service names that have made no outgoing calls
     */
    public Set<String> getIsolatedServices() {
        Set<String> isolated = new HashSet<>(allServices);
        isolated.removeAll(callGraph.keySet());
        return isolated;
    }
    
    /**
     * Calculates the total number of services in the system.
     * 
     * This count includes both active services (those making calls) and
     * isolated services (those only receiving calls). This total is used
     * in RMT calculations to determine maximum possible couplings.
     * 
     * @return Total number of services in the system
     */
    public int getTotalServicesInSystem() {
        return allServices.size();
    }
    
    /**
     * Calculates the total number of active services (services that made calls).
     * 
     * Active services are those that have initiated at least one call to
     * another service, indicating participation in service interactions.
     * 
     * @return Number of services that have made outgoing calls
     */
    public int getActiveServicesCount() {
        return callGraph.size();
    }
    
    /**
     * Calculates the total number of isolated services.
     * 
     * Isolated services are those that exist in the system but have made
     * no outgoing calls. They only receive calls from other services.
     * 
     * @return Number of services that have made no outgoing calls
     */
    public int getIsolatedServicesCount() {
        return allServices.size() - callGraph.size();
    }
    
    /**
     * Gets incoming calls for a specific service.
     * 
     * This method analyzes which services are calling the specified service
     * and how many calls each service makes. This information is useful for
     * understanding service dependencies and identifying potential bottlenecks.
     * 
     * @param service The name of the service to analyze
     * @return Map of caller service names to their call counts
     */
    public Map<String, Integer> getIncomingCalls(String service) {
        Map<String, Integer> incomingCalls = new HashMap<>();
        
        // Iterate through all services that have made calls
        for (String caller : callGraph.keySet()) {
            Map<String, Integer> outgoing = callGraph.get(caller);
            // Check if this caller has called the specified service
            if (outgoing.containsKey(service)) {
                incomingCalls.put(caller, outgoing.get(service));
            }
        }
        
        return incomingCalls;
    }
    
    /**
     * Gets the total number of incoming calls for a specific service.
     * 
     * This method provides a quick way to understand how frequently a service
     * is being called by other services, which can indicate its importance
     * or potential for becoming a bottleneck.
     * 
     * @param service The name of the service to analyze
     * @return Total number of incoming calls to this service
     */
    public int getTotalIncomingCalls(String service) {
        return getIncomingCalls(service).values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Gets the total number of outgoing calls for a specific service.
     * 
     * This method provides insight into how actively a service calls other
     * services, which is directly related to its coupling behavior and
     * the basis for DCI calculations.
     * 
     * @param service The name of the service to analyze
     * @return Total number of outgoing calls from this service
     */
    public int getTotalOutgoingCalls(String service) {
        return getOutgoingCalls(service).values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Gets outgoing calls for a specific service.
     * 
     * This method returns all services that the specified service calls,
     * along with the call frequency for each relationship. This information
     * is the primary input for DCI calculations, as it represents the
     * actual coupling behavior of the service.
     * 
     * @param service The name of the service to analyze
     * @return Map of callee service names to their call counts, or empty map if service has no outgoing calls
     */
    public Map<String, Integer> getOutgoingCalls(String service) {
        return callGraph.getOrDefault(service, new HashMap<>());
    }
} 