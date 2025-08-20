package dci.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dci.model.TraceCall;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DCITraceParser {
    private final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Parses a Zipkin or OpenTelemetry JSON trace file and extracts service-to-service calls.
     * @param jsonFile the trace file
     * @return list of TraceCall objects representing service-to-service calls
     */
    public List<TraceCall> parseTraces(File jsonFile) {
        Map<String, Map<String, Integer>> callCounts = new HashMap<>();
        
        try {
            // Zipkin traces are usually a JSON array of spans
            List<JsonNode> spans = mapper.readValue(jsonFile, new TypeReference<List<JsonNode>>(){});
            
            if (spans.isEmpty()) {
                System.err.println("Warning: Empty trace file");
                return Collections.emptyList();
            }
            
            System.out.println("Processing " + spans.size() + " spans...");
            
            for (JsonNode span : spans) {
                String caller = extractServiceName(span, "localEndpoint");
                String callee = extractServiceName(span, "remoteEndpoint");
                
                // Only count if both caller and callee are present and not equal
                if (caller != null && callee != null && !caller.equals(callee)) {
                    callCounts.computeIfAbsent(caller, k -> new HashMap<>())
                              .merge(callee, 1, Integer::sum);
                }
            }
            
            System.out.println("Found " + callCounts.size() + " unique caller services");
            
        } catch (IOException e) {
            System.err.println("Error parsing trace file: " + e.getMessage());
            return Collections.emptyList();
        }
        
        // Convert to list of TraceCall
        List<TraceCall> traceCalls = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : callCounts.entrySet()) {
            String caller = entry.getKey();
            for (Map.Entry<String, Integer> calleeEntry : entry.getValue().entrySet()) {
                traceCalls.add(new TraceCall(caller, calleeEntry.getKey(), calleeEntry.getValue()));
            }
        }
        
        return traceCalls;
    }
    
    /**
     * Extract service name from endpoint object
     */
    private String extractServiceName(JsonNode span, String endpointType) {
        if (span.has(endpointType) && span.get(endpointType).has("serviceName")) {
            String serviceName = span.get(endpointType).get("serviceName").asText();
            return serviceName.isEmpty() ? null : serviceName;
        }
        return null;
    }
    
    /**
     * Validate trace file format
     */
    public boolean isValidTraceFile(File jsonFile) {
        try {
            List<JsonNode> spans = mapper.readValue(jsonFile, new TypeReference<List<JsonNode>>(){});
            return !spans.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }
} 