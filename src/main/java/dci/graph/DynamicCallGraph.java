package dci.graph;

import dci.model.TraceCall;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicCallGraph {
    // Map: caller -> (callee -> count)
    private Map<String, Map<String, Integer>> callGraph = new HashMap<>();
    private Set<String> allServices = new HashSet<>();

    public DynamicCallGraph() {
    }

    public void addCall(TraceCall call) {
        // Track all services (both caller and callee)
        allServices.add(call.getCaller());
        allServices.add(call.getCallee());
        
        callGraph.computeIfAbsent(call.getCaller(), k -> new HashMap<>())
                 .merge(call.getCallee(), call.getCount(), Integer::sum);
    }

    public Map<String, Map<String, Integer>> getCallGraph() {
        return callGraph;
    }

    public Set<String> getAllServices() {
        return callGraph.keySet();
    }
    
    /**
     * Get all services in the system, including isolated ones
     */
    public Set<String> getAllServicesInSystem() {
        return new HashSet<>(allServices);
    }
    
    /**
     * Get services that made calls (active services)
     */
    public Set<String> getActiveServices() {
        return new HashSet<>(callGraph.keySet());
    }
    
    /**
     * Get services that exist but made no calls (isolated services)
     */
    public Set<String> getIsolatedServices() {
        Set<String> isolated = new HashSet<>(allServices);
        isolated.removeAll(callGraph.keySet());
        return isolated;
    }
    
    /**
     * Calculate total number of services in the system
     */
    public int getTotalServicesInSystem() {
        return allServices.size();
    }
    
    /**
     * Calculate total number of active services (made calls)
     */
    public int getActiveServicesCount() {
        return callGraph.size();
    }
    
    /**
     * Calculate total number of isolated services
     */
    public int getIsolatedServicesCount() {
        return allServices.size() - callGraph.size();
    }
    
    /**
     * Get incoming calls for a specific service
     */
    public Map<String, Integer> getIncomingCalls(String service) {
        Map<String, Integer> incomingCalls = new HashMap<>();
        
        for (String caller : callGraph.keySet()) {
            Map<String, Integer> outgoing = callGraph.get(caller);
            if (outgoing.containsKey(service)) {
                incomingCalls.put(caller, outgoing.get(service));
            }
        }
        
        return incomingCalls;
    }
    
    /**
     * Get total incoming calls count for a service
     */
    public int getTotalIncomingCalls(String service) {
        return getIncomingCalls(service).values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Get total outgoing calls count for a service
     */
    public int getTotalOutgoingCalls(String service) {
        return getOutgoingCalls(service).values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public Map<String, Integer> getOutgoingCalls(String service) {
        return callGraph.getOrDefault(service, new HashMap<>());
    }
} 