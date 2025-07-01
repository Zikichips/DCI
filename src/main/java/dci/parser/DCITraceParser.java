package dci.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dci.model.TraceCall;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DCITraceParser {
    /**
     * Parses a Zipkin or OpenTelemetry JSON trace file and extracts service-to-service calls.
     * @param jsonFile the trace file
     * @return list of TraceCall objects representing service-to-service calls
     */
    public List<TraceCall> parseTraces(File jsonFile) {
        Map<String, Map<String, Integer>> callCounts = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Zipkin traces are usually a JSON array of spans
            List<JsonNode> spans = mapper.readValue(jsonFile, new TypeReference<List<JsonNode>>(){});
            for (JsonNode span : spans) {
                String caller = null;
                String callee = null;
                // Extract caller (localEndpoint.serviceName)
                if (span.has("localEndpoint") && span.get("localEndpoint").has("serviceName")) {
                    caller = span.get("localEndpoint").get("serviceName").asText();
                }
                // Extract callee (remoteEndpoint.serviceName)
                if (span.has("remoteEndpoint") && span.get("remoteEndpoint").has("serviceName")) {
                    callee = span.get("remoteEndpoint").get("serviceName").asText();
                }
                // Only count if both caller and callee are present and not equal
                if (caller != null && callee != null && !caller.equals(callee)) {
                    callCounts.computeIfAbsent(caller, k -> new HashMap<>())
                              .merge(callee, 1, Integer::sum);
                }
            }
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
} 