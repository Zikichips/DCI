package dci.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dci.model.TraceCall;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DCITraceParser handles the extraction of service-to-service calls from trace data.
 * 
 * This class processes Zipkin and OpenTelemetry JSON trace files to extract
 * service interaction information for DCI analysis. The parser identifies
 * service-to-service calls from trace spans and aggregates call frequency
 * information for accurate coupling analysis.
 * 
 * Key Features:
 * - Supports Zipkin and OpenTelemetry trace formats
 * - Extracts caller-callee relationships from trace spans
 * - Aggregates call counts for duplicate interactions
 * - Provides detailed progress information during parsing
 * - Handles various trace format variations gracefully
 * 
 * The parser is designed to work with distributed tracing systems that
 * capture service interactions in microservice architectures.
 * 
 * @author Research Team
 * @version 1.0
 */
public class DCITraceParser {
    /**
     * Jackson ObjectMapper for JSON parsing and processing.
     * This is used to parse trace files and extract service information
     * from the JSON structure of trace spans.
     */
    private final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Parses a Zipkin or OpenTelemetry JSON trace file and extracts service-to-service calls.
     * 
     * This method processes trace files containing distributed tracing data and
     * extracts all service-to-service interactions. The parsing process:
     * 
     * 1. Reads the JSON trace file as an array of spans
     * 2. Extracts service names from localEndpoint and remoteEndpoint
     * 3. Identifies service-to-service calls (excludes self-calls)
     * 4. Aggregates call counts for duplicate interactions
     * 5. Returns structured TraceCall objects for DCI analysis
     * 
     * The method handles various trace formats and provides detailed progress
     * information to help with debugging and validation.
     * 
     * @param jsonFile The trace file to parse (Zipkin or OpenTelemetry format)
     * @return List of TraceCall objects representing service-to-service calls
     */
    public List<TraceCall> parseTraces(File jsonFile) {
        // Map to aggregate call counts: caller -> (callee -> total_calls)
        Map<String, Map<String, Integer>> callCounts = new HashMap<>();
        
        try {
            // Parse JSON file as an array of trace spans
            // Zipkin and OpenTelemetry typically store traces as JSON arrays
            List<JsonNode> spans = mapper.readValue(jsonFile, new TypeReference<List<JsonNode>>(){});
            
            // Validate that the trace file contains data
            if (spans.isEmpty()) {
                System.err.println("Warning: Empty trace file");
                return Collections.emptyList();
            }
            
            System.out.println("Processing " + spans.size() + " spans...");
            
            // Process each span to extract service interaction information
            for (JsonNode span : spans) {
                // Extract service names from the span
                String caller = extractServiceName(span, "localEndpoint");
                String callee = extractServiceName(span, "remoteEndpoint");
                
                // Only count valid service-to-service calls (exclude self-calls)
                if (caller != null && callee != null && !caller.equals(callee)) {
                    // Aggregate call counts for the same caller-callee pair
                    callCounts.computeIfAbsent(caller, k -> new HashMap<>())
                              .merge(callee, 1, Integer::sum);
                }
            }
            
            System.out.println("Found " + callCounts.size() + " unique caller services");
            
        } catch (IOException e) {
            System.err.println("Error parsing trace file: " + e.getMessage());
            return Collections.emptyList();
        }
        
        // Convert aggregated call counts to TraceCall objects
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
     * Extract service name from endpoint object in trace span.
     * 
     * This method handles the extraction of service names from trace spans,
     * supporting both Zipkin and OpenTelemetry formats. The method looks for
     * service names in the specified endpoint type (localEndpoint or remoteEndpoint)
     * and handles cases where the endpoint or service name might be missing.
     * 
     * Trace format variations handled:
     * - Zipkin format: {"localEndpoint": {"serviceName": "service-a"}}
     * - OpenTelemetry format: Similar structure with service names
     * - Missing endpoints: Returns null for incomplete data
     * - Empty service names: Returns null for invalid data
     * 
     * @param span The JSON span object containing endpoint information
     * @param endpointType The endpoint type to extract ("localEndpoint" or "remoteEndpoint")
     * @return Service name if found, null otherwise
     */
    private String extractServiceName(JsonNode span, String endpointType) {
        // Check if the span has the specified endpoint type
        if (span.has(endpointType) && span.get(endpointType).has("serviceName")) {
            String serviceName = span.get(endpointType).get("serviceName").asText();
            // Return null for empty service names to avoid invalid data
            return serviceName.isEmpty() ? null : serviceName;
        }
        return null;
    }
    
    /**
     * Validates that a trace file has the correct format and contains data.
     * 
     * This method performs a quick validation check on trace files to ensure
     * they contain valid JSON data in the expected format before attempting
     * full parsing. This helps provide early error detection and better
     * error messages for users.
     * 
     * @param jsonFile The trace file to validate
     * @return true if the file appears to be a valid trace file, false otherwise
     */
    public boolean isValidTraceFile(File jsonFile) {
        try {
            // Attempt to parse the file as a JSON array of spans
            List<JsonNode> spans = mapper.readValue(jsonFile, new TypeReference<List<JsonNode>>(){});
            // Consider the file valid if it contains at least one span
            return !spans.isEmpty();
        } catch (IOException e) {
            // File is not valid if it can't be parsed as JSON
            return false;
        }
    }
} 