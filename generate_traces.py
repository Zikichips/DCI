#!/usr/bin/env python3
"""
Synthetic Trace Generator for DCI Testing
Generates Zipkin-compatible trace data for testing RMT DCI calculations
"""

import json
import random
import sys
from typing import List, Dict, Tuple

def generate_microservice_traces(services: List[str], 
                                call_patterns: List[Tuple[str, str, int]],
                                num_traces: int = 100) -> List[Dict]:
    """
    Generate synthetic traces based on defined call patterns
    
    Args:
        services: List of service names
        call_patterns: List of (caller, callee, frequency) tuples
        num_traces: Total number of traces to generate
    
    Returns:
        List of trace objects in Zipkin format
    """
    traces = []
    trace_id = 1
    
    # Calculate total frequency for weighted selection
    total_frequency = sum(freq for _, _, freq in call_patterns)
    
    for _ in range(num_traces):
        # Select call pattern based on frequency weights
        rand_val = random.uniform(0, total_frequency)
        cumulative = 0
        
        for caller, callee, freq in call_patterns:
            cumulative += freq
            if rand_val <= cumulative:
                break
        
        # Generate trace
        trace = {
            "traceId": str(trace_id),
            "id": str(trace_id),
            "localEndpoint": {"serviceName": caller},
            "remoteEndpoint": {"serviceName": callee}
        }
        traces.append(trace)
        trace_id += 1
    
    return traces

def create_common_patterns():
    """Create common microservice architecture patterns"""
    
    patterns = {
        "simple_chain": {
            "services": ["frontend", "api-gateway", "user-service", "database"],
            "calls": [
                ("frontend", "api-gateway", 10),
                ("api-gateway", "user-service", 8),
                ("user-service", "database", 6)
            ]
        },
        
        "ecommerce": {
            "services": ["web", "api", "user", "order", "payment", "inventory", "notification"],
            "calls": [
                ("web", "api", 15),
                ("api", "user", 8),
                ("api", "order", 6),
                ("api", "payment", 4),
                ("order", "inventory", 3),
                ("order", "payment", 2),
                ("payment", "notification", 1)
            ]
        },
        
        "microservice_mesh": {
            "services": ["gateway", "auth", "user", "product", "order", "payment", "shipping", "analytics"],
            "calls": [
                ("gateway", "auth", 12),
                ("gateway", "user", 10),
                ("gateway", "product", 8),
                ("gateway", "order", 6),
                ("user", "auth", 4),
                ("order", "payment", 3),
                ("order", "shipping", 2),
                ("order", "analytics", 1),
                ("payment", "analytics", 1)
            ]
        },
        
        "event_driven": {
            "services": ["producer", "event-bus", "consumer1", "consumer2", "consumer3", "database"],
            "calls": [
                ("producer", "event-bus", 20),
                ("event-bus", "consumer1", 8),
                ("event-bus", "consumer2", 6),
                ("event-bus", "consumer3", 4),
                ("consumer1", "database", 3),
                ("consumer2", "database", 2)
            ]
        }
    }
    
    return patterns

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 generate_traces.py <pattern_name> [num_traces]")
        print("\nAvailable patterns:")
        patterns = create_common_patterns()
        for name in patterns.keys():
            print(f"  - {name}")
        sys.exit(1)
    
    pattern_name = sys.argv[1]
    num_traces = int(sys.argv[2]) if len(sys.argv) > 2 else 100
    
    patterns = create_common_patterns()
    
    if pattern_name not in patterns:
        print(f"Unknown pattern: {pattern_name}")
        print("Available patterns:", list(patterns.keys()))
        sys.exit(1)
    
    pattern = patterns[pattern_name]
    services = pattern["services"]
    calls = pattern["calls"]
    
    print(f"Generating {num_traces} traces for pattern: {pattern_name}")
    print(f"Services: {', '.join(services)}")
    print(f"Call patterns: {len(calls)}")
    
    traces = generate_microservice_traces(services, calls, num_traces)
    
    output_file = f"traces_{pattern_name}.json"
    with open(output_file, 'w') as f:
        json.dump(traces, f, indent=2)
    
    print(f"Generated {len(traces)} traces in {output_file}")
    
    # Print expected DCI analysis
    print("\nExpected DCI Analysis:")
    print("Services and their outgoing calls:")
    service_calls = {}
    for service in services:
        service_calls[service] = set()
    
    for caller, callee, _ in calls:
        service_calls[caller].add(callee)
    
    max_possible = len(services) - 1
    print(f"Max possible couplings: {max_possible}")
    print()
    
    for service in services:
        actual_calls = len(service_calls[service])
        dci = actual_calls / max_possible if max_possible > 0 else 0
        status = "High" if dci >= 0.7 else "Moderate" if dci >= 0.4 else "Low" if dci > 0 else "No"
        print(f"{service}: {actual_calls}/{max_possible} = {dci:.3f} ({status} Coupling)")

if __name__ == "__main__":
    main()

