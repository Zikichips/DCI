# Dynamic Coupling Index (DCI) with Relative Measurement Theory (RMT)

## Overview

This implementation provides a **minimal, focused** Dynamic Coupling Index (DCI) using **Relative Measurement Theory (RMT)**, enabling coupling analysis that considers system-wide context with maximum simplicity.

## Key Features

### ✅ **Minimal RMT Implementation**

- **RMT DCI**: Simple relative coupling measurement
- **Core Formula**: (Actual Coupling / Max Possible Coupling)
- **Clean Architecture**: Integrated into DCIComputer

### ✅ **System Context Awareness**

- Tracks all services in the system (including isolated ones)
- Calculates maximum possible couplings
- Simple relative measurement

## Architecture

```
dci/
├── metrics/
│   └── DCIComputer.java            # RMT calculations integrated
├── graph/
│   └── DynamicCallGraph.java       # Service tracking
├── output/
│   └── CSVWriter.java              # Simple CSV output
└── DCIMain.java                    # Main entry point
```

## Usage

### Simple Usage

```bash
mvn exec:java -Dexec.mainClass="dci.DCIMain" -Dexec.args="zepkin_trace.json output.csv"
```

### Testing

```bash
./test_rmt_integration.sh
```

## RMT Formula

### RMT DCI (Simplified)

```
RMT_DCI = Actual_Couplings / Max_Possible_Couplings
```

Where:

- **Actual Couplings**: Number of services this service calls
- **Max Possible Couplings**: Total services in system minus 1

## Output Format

### RMT DCI Output

```csv
Service,DCI,Status
service-a,0.650,High Coupling
service-b,0.350,Moderate Coupling
service-c,0.100,Low Coupling
```

## Status Classification

| RMT DCI Score | Status            |
| ------------- | ----------------- |
| ≥ 0.7         | High Coupling     |
| 0.4 - 0.7     | Moderate Coupling |
| 0.0 - 0.4     | Low Coupling      |
| 0.0           | No Coupling       |

## Example Results

```csv
Service,DCI,Status
A,0.600,Moderate Coupling  # Calls 3 out of 5 possible services
B,0.400,Moderate Coupling  # Calls 2 out of 5 possible services
C,0.400,Moderate Coupling  # Calls 2 out of 5 possible services
D,0.200,Low Coupling       # Calls 1 out of 5 possible services
E,0.200,Low Coupling       # Calls 1 out of 5 possible services
F,0.000,No Coupling        # Calls 0 out of 5 possible services
```

## Validation

Use the validation script to analyze results:

```bash
python3 validate_rmt_dci.py dci_output_rmt.csv
```

## Research Applications

### 1. **Static vs Dynamic Coupling Analysis**

Compare how well static coupling (MCI) predicts runtime coupling (RMT DCI)

### 2. **Service Architecture Insights**

Identify services with different coupling patterns

### 3. **Coupling Pattern Classification**

Categorize services based on relative coupling strength

## Benefits

### 1. **Minimal Complexity**

- Single formula with clear intent
- Easy to understand and explain
- ~50% less code to maintain

### 2. **Research Ready**

- Implements Relative Measurement Theory
- Provides relative coupling perspective
- Enables fair comparison with static metrics

### 3. **Easy to Validate**

- Clear methodology
- Simple output format
- Straightforward interpretation

## Technical Notes

- **Single Purpose**: RMT DCI only
- **Performance**: Efficient calculations
- **Maintainability**: Minimal codebase
- **Documentation**: Clear and concise
- **Testing**: Simple validation

## Project Structure

```
MicroserviceMeasure/
├── src/main/java/dci/          # Core implementation
├── validate_rmt_dci.py         # Validation tool
├── test_rmt_integration.sh     # Testing script
├── zepkin_trace.json           # Sample trace data
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

This simplified RMT implementation provides a clean, focused approach to dynamic coupling measurement using Relative Measurement Theory.
