package dci;

import dci.graph.DynamicCallGraph;
import dci.metrics.DCIComputer;
import dci.model.TraceCall;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

public class DCIComputerTest {
    
    @Test
    public void testSimpleRMTCalculation() {
        // Create a simple call graph: A -> B, A -> C, B -> C
        DynamicCallGraph graph = new DynamicCallGraph();
        graph.addCall(new TraceCall("A", "B", 1));
        graph.addCall(new TraceCall("A", "C", 1));
        graph.addCall(new TraceCall("B", "C", 1));
        
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        // 3 services total, max possible coupling = 2
        assertEquals(3, graph.getTotalServicesInSystem());
        assertEquals(2, graph.getTotalServicesInSystem() - 1);
        
        // A has 2 couplings out of 2 possible = 1.0
        assertEquals(1.0, dciScores.get("A"), 0.001);
        // B has 1 coupling out of 2 possible = 0.5
        assertEquals(0.5, dciScores.get("B"), 0.001);
        // C has 0 couplings out of 2 possible = 0.0
        assertEquals(0.0, dciScores.get("C"), 0.001);
    }
    
    @Test
    public void testIsolatedServiceRMT() {
        // Create graph with isolated service
        DynamicCallGraph graph = new DynamicCallGraph();
        graph.addCall(new TraceCall("A", "B", 1));
        graph.addCall(new TraceCall("B", "C", 1));
        // D is isolated (no calls) - but we need to add it to the graph
        // by adding a call that includes D as either caller or callee
        graph.addCall(new TraceCall("A", "D", 1)); // This will add D to the system
        
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        // 4 services total, max possible coupling = 3
        assertEquals(4, graph.getTotalServicesInSystem());
        assertEquals(2, graph.getIsolatedServicesCount()); // C and D are isolated
        
        // Isolated service D should have 0.0 DCI
        assertEquals(0.0, dciScores.get("D"), 0.001);
        assertEquals("No Coupling", computer.getStatus(dciScores.get("D")));
        
        // Isolated service C should also have 0.0 DCI
        assertEquals(0.0, dciScores.get("C"), 0.001);
        assertEquals("No Coupling", computer.getStatus(dciScores.get("C")));
    }
    
    @Test
    public void testStatusClassification() {
        DCIComputer computer = new DCIComputer();
        
        assertEquals("High Coupling", computer.getStatus(0.8));
        assertEquals("High Coupling", computer.getStatus(0.7));
        assertEquals("Moderate Coupling", computer.getStatus(0.6));
        assertEquals("Moderate Coupling", computer.getStatus(0.4));
        assertEquals("Low Coupling", computer.getStatus(0.3));
        assertEquals("Low Coupling", computer.getStatus(0.1));
        assertEquals("No Coupling", computer.getStatus(0.0));
    }
    
    @Test
    public void testSingleServiceSystem() {
        // Edge case: only one service in system
        DynamicCallGraph graph = new DynamicCallGraph();
        graph.addCall(new TraceCall("A", "A", 1)); // Self-call (should be ignored)
        
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        // Single service, max possible coupling = 0
        assertEquals(1, graph.getTotalServicesInSystem());
        assertEquals(0.0, dciScores.get("A"), 0.001);
    }
    
    @Test
    public void testMultipleCallsSameService() {
        // Test that multiple calls to same service count as one coupling
        DynamicCallGraph graph = new DynamicCallGraph();
        graph.addCall(new TraceCall("A", "B", 5)); // 5 calls to B
        graph.addCall(new TraceCall("A", "C", 3)); // 3 calls to C
        
        DCIComputer computer = new DCIComputer();
        Map<String, Double> dciScores = computer.computeDCI(graph);
        
        // A has 2 couplings (B, C) out of 2 possible = 1.0
        assertEquals(1.0, dciScores.get("A"), 0.001);
    }
}
