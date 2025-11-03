# Assignment 4: Smart City / Smart Campus Scheduling

## Overview

This project implements graph algorithms for analyzing task dependencies in smart city scheduling scenarios. It consolidates two course topics:

1. **Strongly Connected Components (SCC) & Topological Ordering**
2. **Shortest Paths in DAGs**

The implementation detects cyclic dependencies using SCC algorithms, compresses them into a DAG, and then computes optimal scheduling using topological ordering and path algorithms.

## Project Structure

```
assignment4/
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── daa/
│   │               ├── Main.java                    # Main driver program
│   │               └── graph/
│   │                   ├── scc/                    # SCC package
│   │                   │   └── StronglyConnectedComponents.java
│   │                   ├── topo/                   # Topological sort package
│   │                   │   └── TopologicalSort.java
│   │                   ├── dagsp/                  # DAG shortest path package
│   │                   │   └── DAGShortestPath.java
│   │                   └── util/                   # Utilities
│   │                       ├── Graph.java
│   │                       ├── Metrics.java
│   │                       ├── TaskGraphLoader.java
│   │                       └── DatasetGenerator.java
│   └── test/
│       └── java/
│           └── com/
│               └── daa/
│                   └── graph/
│                       ├── scc/
│                       │   └── StronglyConnectedComponentsTest.java
│                       ├── topo/
│                       │   └── TopologicalSortTest.java
│                       └── dagsp/
│                           └── DAGShortestPathTest.java
└── data/                            # Generated datasets
    ├── small_*.json                 # Small datasets (6-10 nodes)
    ├── medium_*.json                # Medium datasets (10-20 nodes)
    └── large_*.json                 # Large datasets (20-50 nodes)
```

## Algorithms Implemented

### 1. Strongly Connected Components (Kosaraju's Algorithm)
- **Purpose**: Detect and compress cyclic dependencies
- **Algorithm**: Two-pass DFS on original and reversed graphs
- **Output**: List of SCCs, each containing vertices in a cycle
- **Condensation**: Builds a DAG where each SCC becomes a single node

### 2. Topological Sort
- **Purpose**: Order tasks for scheduling (no cycles)
- **Algorithm**: Kahn's algorithm (queue-based)
- **Output**: Valid ordering of components and derived vertex ordering

### 3. Shortest/Longest Paths in DAG
- **Purpose**: Find optimal scheduling paths
- **Shortest Path**: Uses edge weights, computed via dynamic programming over topological order
- **Longest Path (Critical Path)**: Uses node durations, finds the longest completion path
- **Output**: Shortest distances from source, critical path with length

## Data Model

### Weight Model Choice
**Node Durations**: The implementation uses **node durations** for critical path analysis, where:
- Each task has a duration (time to complete)
- Path length = sum of node durations along the path
- Edge weights are used for shortest path computation (edge traversal costs)

### JSON Format
```json
{
  "tasks": [
    {
      "id": 0,
      "name": "task0",
      "duration": 5,
      "dependencies": [1, 2]
    },
    ...
  ]
}
```

## Datasets

### Small Datasets (6-10 nodes)
1. **small_pure_dag.json** (8 nodes)
   - Pure DAG structure, no cycles
   - Simple dependency chain

2. **small_single_cycle.json** (7 nodes)
   - Single cycle connecting all nodes
   - Tests SCC detection

3. **small_two_cycles.json** (9 nodes)
   - Two separate cycles
   - Multiple SCCs

### Medium Datasets (10-20 nodes)
4. **medium_mixed.json** (15 nodes)
   - Mixed structure: some cyclic, some acyclic
   - Various dependency patterns

5. **medium_multiple_sccs.json** (18 nodes)
   - Multiple SCCs with DAG connections between them
   - Tests condensation graph building

6. **medium_dense.json** (12 nodes)
   - Dense DAG with many dependencies
   - Tests performance on dense graphs

### Large Datasets (20-50 nodes)
7. **large_sparse.json** (25 nodes)
   - Sparse DAG structure
   - Few dependencies per node

8. **large_complex.json** (35 nodes)
   - Complex mixed structure
   - Multiple patterns combined

9. **large_multiple_sccs.json** (30 nodes)
   - Large graph with multiple SCCs
   - Performance testing

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build the Project
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Generate Datasets
The datasets are automatically generated when you run the main program. Alternatively:
```bash
mvn exec:java -Dexec.mainClass="com.daa.graph.util.DatasetGenerator"
```

### Run Main Program
```bash
mvn exec:java -Dexec.mainClass="com.daa.Main"
```

Or compile and run:
```bash
mvn package
java -cp target/assignment4-1.0-SNAPSHOT.jar com.daa.Main
```

## Expected Output

The program processes all 9 datasets and outputs:
1. **SCC Results**: List of strongly connected components and their sizes
2. **Condensation Graph**: DAG structure with SCCs as nodes
3. **Topological Order**: Valid ordering for scheduling
4. **Shortest Paths**: Distances from source in condensation DAG
5. **Critical Path**: Longest path and its length
6. **Metrics**: Operation counters and execution time for each algorithm

## Instrumentation

The `Metrics` class tracks:
- **DFS Visits**: Number of vertices visited during DFS
- **Edge Traversals**: Number of edges traversed
- **Kahn Operations**: Pops and pushes in queue-based topological sort
- **Relaxations**: Edge relaxations in path algorithms
- **Time**: Execution time in milliseconds (using `System.nanoTime()`)

## Code Quality Features

- **Package Structure**: Organized into `graph.scc`, `graph.topo`, `graph.dagsp`, `graph.util`
- **Javadoc**: Public classes and methods documented
- **Modularity**: Clean separation of concerns
- **Error Handling**: Proper exception handling for cycles, invalid inputs
- **JUnit Tests**: Comprehensive test coverage for all algorithms

## Analysis Results

### Performance Characteristics

#### SCC Algorithm (Kosaraju)
- **Time Complexity**: O(V + E) for two DFS passes
- **Space Complexity**: O(V + E) for adjacency lists and recursion stack
- **Bottlenecks**: 
  - Dense graphs increase edge traversals
  - Large cycles increase DFS depth
  - Effect of structure: More cycles → fewer but larger SCCs

#### Topological Sort
- **Time Complexity**: O(V + E) - Queue operations + edge processing
- **Space Complexity**: O(V) for queue and in-degree array
- **Bottlenecks**:
  - Many sources increase initial queue size
  - Dense graphs increase edge processing

#### DAG Shortest/Longest Path
- **Time Complexity**: O(V + E) with topological ordering
- **Space Complexity**: O(V) for distance arrays
- **Bottlenecks**:
  - Large graphs increase relaxations
  - Dense graphs: more edges to relax per vertex

### Structure Effects

1. **Density**:
   - Dense graphs: More edges → more operations
   - Sparse graphs: Fewer edges → faster execution

2. **SCC Sizes**:
   - Large SCCs: Fewer but larger components in condensation
   - Many small SCCs: More components, larger condensation graph

3. **Cycle Patterns**:
   - Pure DAGs: No cycles, all vertices in separate SCCs
   - Cycles: Compress multiple vertices into single SCC

## Practical Recommendations

1. **When to use each algorithm**:
   - **SCC (Kosaraju)**: Always first when dealing with potentially cyclic dependencies
   - **Topological Sort (Kahn)**: Use for DAGs to find valid ordering
   - **Shortest Path**: For cost-optimization scenarios
   - **Longest Path**: For time-critical scheduling (critical path method)

2. **Optimization tips**:
   - Pre-compress cycles with SCC before other operations
   - Use topological ordering for any DAG-based computation
   - Consider graph density when choosing data structures

3. **Real-world applications**:
   - Task scheduling systems
   - Build systems (compilation dependencies)
   - Project management (critical path analysis)
   - Course prerequisite chains

## Testing

Run all tests:
```bash
mvn test
```

Test coverage includes:
- Edge cases (empty graphs, single vertices)
- Simple cases (pure DAGs, single cycles)
- Complex cases (multiple SCCs, mixed structures)
- Algorithm correctness verification

## License

This project is created for educational purposes as part of the Design and Analysis of Algorithms course.

