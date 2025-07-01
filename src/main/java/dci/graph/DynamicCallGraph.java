package dci.graph;

import dci.model.TraceCall;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicCallGraph {
    // Map: caller -> (callee -> count)
    private Map<String, Map<String, Integer>> callGraph = new HashMap<>();

    public void addCall(TraceCall call) {
        callGraph.computeIfAbsent(call.getCaller(), k -> new HashMap<>())
                 .merge(call.getCallee(), call.getCount(), Integer::sum);
    }

    public Map<String, Map<String, Integer>> getCallGraph() {
        return callGraph;
    }

    public Set<String> getAllServices() {
        Set<String> services = new HashSet<>(callGraph.keySet());
        for (Map<String, Integer> callees : callGraph.values()) {
            services.addAll(callees.keySet());
        }
        return services;
    }

    public Map<String, Integer> getOutgoingCalls(String service) {
        return callGraph.getOrDefault(service, new HashMap<>());
    }
} 