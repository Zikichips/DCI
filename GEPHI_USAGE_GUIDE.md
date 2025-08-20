# Gephi Usage Guide for Enhanced DCI GraphML

## 🎯 **Overview**

This guide explains how to properly import and visualize the enhanced GraphML file in Gephi for your DCI research presentation. **Important: Gephi settings do not persist automatically, so you need to configure the visualization every time you open the file.**

## 📁 **File Information**

The enhanced GraphML file (`output.graphml`) now includes:

- ✅ **Node positioning** (x, y coordinates)
- ✅ **Service labels** (A, B, C, D, E, F)
- ✅ **Color coding** by coupling status
- ✅ **Node sizing** based on DCI scores
- ✅ **Edge weights** for call frequency
- ✅ **Rich metadata** for analysis

## 🚀 **Step-by-Step Gephi Import & Configuration**

### **Step 1: Open Gephi**

1. Launch Gephi application
2. Click "New Project" or "Open Graph File"

### **Step 2: Import the GraphML File**

1. Go to **File → Open**
2. Navigate to your project folder
3. Select `output.graphml`
4. Click "Open"

### **Step 3: Configure Import Settings**

When the import dialog appears:

- **Graph Type**: Directed (should be auto-detected)
- **Import as**: New workspace
- Click **OK**

**Verify Import Success:**

- You should see 6 nodes and 9 edges imported
- Go to **Data Laboratory** → **Nodes** to confirm data is loaded

## 🎨 **REQUIRED: Configure Visualization Settings**

**⚠️ IMPORTANT: These settings must be configured EVERY TIME you open the file. Gephi does not save these settings automatically.**

### **Step 4: Configure Node Colors**

1. Go to **Appearance** tab
2. Select **Nodes** (if not already selected)
3. Click **Color** button (paintbrush icon)
4. Select **Partition** → **color** attribute
5. Click **Apply**

**Expected Result:** Nodes should now be color-coded:

- 🟠 **Orange**: Moderate Coupling (Services A, B, C)
- 🟡 **Yellow**: Low Coupling (Services D, E)
- ⚫ **Gray**: No Coupling (Service F)

### **Step 5: Configure Node Sizes**

1. Still in **Appearance** → **Nodes**
2. Click **Size** button (circle icon)
3. Select **Ranking** → **size** attribute
4. Set size range: **Min: 10, Max: 30**
5. Click **Apply**

**Expected Result:** Nodes should now have different sizes:

- **Larger nodes**: Higher DCI scores (Service A = largest)
- **Smaller nodes**: Lower DCI scores (Service F = smallest)

### **Step 6: Configure Node Labels**

1. Still in **Appearance** → **Nodes**
2. Click **Text** button (T icon)
3. Select **Partition** → **label** attribute
4. Click **Apply**

**Expected Result:** Service names (A, B, C, D, E, F) should appear on nodes

### **Step 7: Configure Edge Sizes**

1. Select **Edges** in Appearance tab
2. Click **Size** button
3. Select **Ranking** → **weight** attribute
4. Set size range: **Min: 1, Max: 5**
5. Click **Apply**

**Expected Result:** Edges should have different thicknesses based on call frequency

### **Step 8: Apply Layout (if needed)**

If nodes appear clustered or overlapping:

1. Go to **Layout** tab
2. Select **ForceAtlas 2** or **Fruchterman Reingold**
3. Click **Run** for a few seconds
4. Click **Stop** when satisfied

## 📊 **Understanding the Visualization**

### **Node Colors**

- 🔴 **Red**: High Coupling (≥0.7) - Potential architectural concerns
- 🟠 **Orange**: Moderate Coupling (0.4-0.7) - Balanced dependencies
- 🟡 **Yellow**: Low Coupling (0.0-0.4) - Minimal dependencies
- ⚫ **Gray**: No Coupling (0.0) - Isolated services

### **Node Sizes**

- **Larger nodes**: Higher DCI scores (more coupling)
- **Smaller nodes**: Lower DCI scores (less coupling)
- **Size range**: 0.5 to 2.0 (normalized for visualization)

### **Edge Thickness**

- **Thicker edges**: Higher call frequency between services
- **Thinner edges**: Lower call frequency
- **Direction**: Arrows show call direction (caller → callee)

## 🔍 **Viewing Node Information**

### **Method 1: Data Laboratory (Most Reliable)**

1. Go to **Data Laboratory** tab
2. Click **Nodes** tab
3. Click on any row to see all node attributes:
   - **Id**: Service name (A, B, C, etc.)
   - **Label**: Service name
   - **status**: Coupling status (Moderate/Low/No Coupling)
   - **dci_score**: DCI score (0.000 to 1.000)
   - **size**: Node size value
   - **color**: Hex color code
   - **incoming_calls**: Number of services calling this service
   - **outgoing_calls**: Number of services this service calls
   - **x, y**: Node position coordinates

### **Method 2: Context Panel**

1. In **Overview** tab, select a node (click on it)
2. Look at the **Context** panel on the right side
3. It should show node information

### **Method 3: Right-Click Menu**

1. **Right-click** on any node
2. Look for options like:
   - **Show Node Data**
   - **Node Information**
   - **Properties**

### **Method 4: Hover Tooltip**

1. **Hover** over a node
2. Tooltip should show basic information

## 📈 **Research Presentation Tips**

### **For Academic Presentations**

1. **Start with Overview**: Show the complete graph
2. **Highlight Patterns**: Point out color clusters and size variations
3. **Explain Coupling**: Use node colors to explain coupling levels
4. **Show Relationships**: Use edge thickness to show call frequency
5. **Interactive Demo**: Let audience see node details on hover

### **Key Talking Points**

- **"Red nodes indicate high coupling - these services may need refactoring"**
- **"Larger nodes have higher DCI scores, showing more dependencies"**
- **"Thicker edges show frequent service interactions"**
- **"Gray nodes are isolated services with no outgoing calls"**

## 🛠 **Troubleshooting**

### **If nodes don't appear:**

1. Check that the file imported successfully
2. Try applying a layout algorithm
3. Zoom out to see all nodes

### **If colors don't show:**

1. Go to Appearance → Nodes → Color
2. Select "Partition" → "color" attribute
3. Click "Apply"

### **If labels don't show:**

1. Go to Appearance → Nodes → Text
2. Select "Partition" → "label" attribute
3. Click "Apply"

### **If edges are too thin/thick:**

1. Go to Appearance → Edges → Size
2. Adjust the size range (Min/Max values)
3. Click "Apply"

### **If no node information appears:**

1. **Go to Data Laboratory** → **Nodes** (this always works)
2. Check that the import was successful
3. Verify all columns are present in the data table

## 📋 **Complete Configuration Checklist**

**Every time you open the GraphML file, run through this checklist:**

- [ ] **Import successful** (6 nodes, 9 edges)
- [ ] **Node colors configured** (Appearance → Nodes → Color → Partition → color)
- [ ] **Node sizes configured** (Appearance → Nodes → Size → Ranking → size)
- [ ] **Node labels configured** (Appearance → Nodes → Text → Partition → label)
- [ ] **Edge sizes configured** (Appearance → Edges → Size → Ranking → weight)
- [ ] **Layout applied** (if needed)
- [ ] **Node information accessible** (Data Laboratory → Nodes)

## 📋 **Example Analysis Script**

Here's a sample script for presenting your DCI analysis:

### **Introduction**

"Today I'll show you our Dynamic Coupling Index analysis using Relative Measurement Theory. This visualization shows service coupling patterns in our microservice architecture."

### **Graph Overview**

"Here we have 6 services (A through F) with their coupling relationships. Notice the color coding: orange for moderate coupling, yellow for low coupling, and gray for no coupling."

### **Key Findings**

"Service A shows the highest coupling with a DCI score of 0.600, calling 3 out of 5 possible services. Service F is completely isolated with no outgoing calls."

### **Architectural Insights**

"The thick edges between services show frequent interactions, while the isolated gray node suggests a potential integration opportunity or architectural isolation."

## 🎓 **Research Validation**

### **For Peer Review**

- **Methodology**: Explain RMT formula and relative measurement
- **Visualization**: Show how colors and sizes represent coupling
- **Validation**: Demonstrate with node details and edge information
- **Reproducibility**: Share the GraphML file and analysis process

### **For Publication**

- **Export high-quality images**: Use Gephi's export features
- **Include legend**: Explain color and size meanings
- **Provide data**: Share CSV results for statistical analysis
- **Document process**: Include import and analysis steps

## ✅ **Success Indicators**

You'll know the GraphML is working correctly when you see:

- ✅ **6 nodes** positioned in a circle or spread out
- ✅ **Color-coded nodes** (orange, yellow, gray)
- ✅ **Size variation** (larger nodes for higher DCI scores)
- ✅ **Directed edges** with arrows showing call direction
- ✅ **Node labels** (A, B, C, D, E, F) visible on nodes
- ✅ **Node information accessible** in Data Laboratory
- ✅ **Smooth interaction** (zoom, pan, select)

## 🎉 **Ready for Research!**

Your enhanced GraphML file is now ready for:

- **Academic presentations** with professional visualizations
- **Research papers** with publication-quality graphics
- **Interactive demonstrations** showing coupling patterns
- **Peer review** with comprehensive methodology documentation

**Remember: Configure the visualization settings every time you open the file for the best experience!**

The visualization clearly shows coupling relationships and provides the foundation for meaningful architectural analysis and research validation.
