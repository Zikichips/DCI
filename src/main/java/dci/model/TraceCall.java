package dci.model;

/**
 * TraceCall represents a service-to-service call extracted from trace data.
 * 
 * This class encapsulates the fundamental unit of service interaction data
 * used throughout the DCI analysis pipeline. Each TraceCall object represents
 * a single service calling another service, with associated frequency information.
 * 
 * Key Components:
 * - caller: The service initiating the call
 * - callee: The service receiving the call
 * - count: The number of times this specific call occurred
 * 
 * The TraceCall objects are created during trace parsing and serve as the
 * building blocks for constructing the dynamic call graph and calculating
 * coupling metrics.
 * 
 * @author Research Team
 * @version 1.0
 */
public class TraceCall {
    /**
     * The name of the service that initiated the call.
     * This represents the "caller" in the service-to-service interaction.
     */
    private String caller;
    
    /**
     * The name of the service that received the call.
     * This represents the "callee" in the service-to-service interaction.
     */
    private String callee;
    
    /**
     * The number of times this specific service-to-service call occurred.
     * This count is aggregated from multiple trace spans that represent
     * the same caller-callee interaction.
     */
    private int count;

    /**
     * Constructor for creating a new TraceCall object.
     * 
     * This constructor initializes a TraceCall with the specified caller,
     * callee, and call count. The TraceCall represents a service interaction
     * that will be used for coupling analysis.
     * 
     * @param caller The name of the service making the call
     * @param callee The name of the service receiving the call
     * @param count The number of times this call occurred
     */
    public TraceCall(String caller, String callee, int count) {
        this.caller = caller;
        this.callee = callee;
        this.count = count;
    }

    /**
     * Gets the name of the service that initiated the call.
     * 
     * The caller is the service that actively makes a call to another service.
     * This information is used to track outgoing coupling relationships
     * and calculate DCI scores for the calling service.
     * 
     * @return The name of the calling service
     */
    public String getCaller() {
        return caller;
    }

    /**
     * Sets the name of the service that initiated the call.
     * 
     * This method allows modification of the caller information, which may
     * be useful for data correction or normalization purposes.
     * 
     * @param caller The new name for the calling service
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * Gets the name of the service that received the call.
     * 
     * The callee is the service that receives a call from another service.
     * This information is used to track incoming coupling relationships
     * and understand service dependencies.
     * 
     * @return The name of the called service
     */
    public String getCallee() {
        return callee;
    }

    /**
     * Sets the name of the service that received the call.
     * 
     * This method allows modification of the callee information, which may
     * be useful for data correction or normalization purposes.
     * 
     * @param callee The new name for the called service
     */
    public void setCallee(String callee) {
        this.callee = callee;
    }

    /**
     * Gets the number of times this service-to-service call occurred.
     * 
     * The count represents the frequency of this specific service interaction
     * as observed in the trace data. This information is used for:
     * - Understanding call frequency patterns
     * - Weighting coupling relationships
     * - Identifying high-frequency service interactions
     * 
     * Note: For DCI calculations, the presence of a call (count > 0) is more
     * important than the specific count, as DCI focuses on coupling presence
     * rather than frequency.
     * 
     * @return The number of times this call occurred
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of times this service-to-service call occurred.
     * 
     * This method allows modification of the call count, which may be useful
     * for data correction, normalization, or testing purposes.
     * 
     * @param count The new count for this service call
     */
    public void setCount(int count) {
        this.count = count;
    }
} 