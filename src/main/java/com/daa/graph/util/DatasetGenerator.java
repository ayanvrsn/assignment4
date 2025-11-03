package com.daa.graph.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Generator for creating test datasets with different graph structures.
 */
public class DatasetGenerator {
    private static final Gson gson = new Gson();
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    /**
     * Generate a dataset and save to file.
     * @param filename output filename
     * @param numNodes number of nodes
     * @param type type of graph structure
     */
    public static void generateDataset(String filename, int numNodes, GraphType type) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray tasks = new JsonArray();

        // Generate tasks
        for (int i = 0; i < numNodes; i++) {
            JsonObject task = new JsonObject();
            task.addProperty("id", i);
            task.addProperty("name", "task" + i);
            task.addProperty("duration", random.nextInt(10) + 1); // Duration 1-10

            // Generate dependencies based on graph type
            List<Integer> dependencies = generateDependencies(i, numNodes, type);
            JsonArray depsArray = new JsonArray();
            for (Integer dep : dependencies) {
                depsArray.add(dep);
            }
            task.add("dependencies", depsArray);
            
            tasks.add(task);
        }

        root.add("tasks", tasks);

        // Write to file
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(root, writer);
        }
    }

    /**
     * Generate dependencies based on graph type.
     */
    private static List<Integer> generateDependencies(int nodeId, int numNodes, GraphType type) {
        List<Integer> deps = new ArrayList<>();
        
        switch (type) {
            case PURE_DAG:
                // DAG: only depend on nodes with lower IDs
                if (nodeId > 0) {
                    int maxDeps = Math.min(3, nodeId);
                    int numDeps = maxDeps > 0 ? random.nextInt(maxDeps) + 1 : 1;
                    Set<Integer> selected = new HashSet<>();
                    while (selected.size() < numDeps && selected.size() < nodeId) {
                        int dep = random.nextInt(nodeId);
                        selected.add(dep);
                    }
                    deps.addAll(selected);
                }
                break;

            case SINGLE_CYCLE:
                // Create a single cycle
                if (nodeId > 0) {
                    // Each node depends on previous one, last one depends on first
                    if (nodeId < numNodes - 1) {
                        deps.add(nodeId - 1);
                    } else {
                        // Last node depends on first and previous
                        deps.add(0);
                        if (nodeId > 1) {
                            deps.add(nodeId - 1);
                        }
                    }
                }
                break;

            case TWO_CYCLES:
                // Create two cycles
                int mid = numNodes / 2;
                if (nodeId < mid) {
                    // First cycle
                    if (nodeId > 0) {
                        deps.add(nodeId - 1);
                    } else if (nodeId == 0 && mid > 1) {
                        deps.add(mid - 1);
                    }
                } else {
                    // Second cycle
                    if (nodeId > mid) {
                        deps.add(nodeId - 1);
                    } else if (nodeId == mid && numNodes > mid + 1) {
                        deps.add(numNodes - 1);
                    }
                }
                break;

            case MIXED_STRUCTURE:
                // Mixed: some DAG edges, some cycles
                if (nodeId > 0) {
                    // 60% chance of depending on previous node (cyclic)
                    if (random.nextDouble() < 0.6) {
                        deps.add(nodeId - 1);
                    }
                    // 40% chance of depending on lower ID node (DAG-like)
                    if (random.nextDouble() < 0.4 && nodeId > 2) {
                        deps.add(random.nextInt(nodeId - 1));
                    }
                }
                break;

            case MULTIPLE_SCCS:
                // Create multiple SCCs by grouping nodes
                int sccSize = Math.max(2, numNodes / 5);
                int sccId = nodeId / sccSize;
                int localId = nodeId % sccSize;
                
                // Within SCC, create cycle
                if (localId > 0) {
                    deps.add(sccId * sccSize + (localId - 1));
                } else if (localId == 0 && sccSize > 1) {
                    deps.add(sccId * sccSize + (sccSize - 1));
                }
                
                // Between SCCs, create DAG edges
                if (sccId > 0 && random.nextDouble() < 0.3) {
                    int prevSccNode = (sccId - 1) * sccSize + random.nextInt(sccSize);
                    deps.add(prevSccNode);
                }
                break;

            case DENSE_DAG:
                // Dense DAG: many dependencies
                for (int i = 0; i < nodeId; i++) {
                    if (random.nextDouble() < 0.5) {
                        deps.add(i);
                    }
                }
                break;

            case SPARSE_DAG:
                // Sparse DAG: few dependencies
                if (nodeId > 0 && random.nextDouble() < 0.3) {
                    deps.add(random.nextInt(Math.max(1, nodeId)));
                }
                break;

            case COMPLEX_MIXED:
                // Complex structure with various patterns
                if (nodeId > 0) {
                    // Create small cycles
                    if (nodeId % 3 == 0 && nodeId >= 3) {
                        deps.add(nodeId - 3);
                    } else if (nodeId > 0) {
                        deps.add(nodeId - 1);
                    }
                    // Add some cross edges
                    if (random.nextDouble() < 0.2 && nodeId > 2) {
                        deps.add(random.nextInt(Math.max(1, nodeId - 1)));
                    }
                }
                break;
        }

        return deps;
    }

    /**
     * Graph type enumeration.
     */
    public enum GraphType {
        PURE_DAG,
        SINGLE_CYCLE,
        TWO_CYCLES,
        MIXED_STRUCTURE,
        MULTIPLE_SCCS,
        DENSE_DAG,
        SPARSE_DAG,
        COMPLEX_MIXED
    }

    /**
     * Generate all required datasets.
     */
    public static void generateAllDatasets() throws IOException {
        // Small datasets (6-10 nodes)
        generateDataset("data/small_pure_dag.json", 8, GraphType.PURE_DAG);
        generateDataset("data/small_single_cycle.json", 7, GraphType.SINGLE_CYCLE);
        generateDataset("data/small_two_cycles.json", 9, GraphType.TWO_CYCLES);

        // Medium datasets (10-20 nodes)
        generateDataset("data/medium_mixed.json", 15, GraphType.MIXED_STRUCTURE);
        generateDataset("data/medium_multiple_sccs.json", 18, GraphType.MULTIPLE_SCCS);
        generateDataset("data/medium_dense.json", 12, GraphType.DENSE_DAG);

        // Large datasets (20-50 nodes)
        generateDataset("data/large_sparse.json", 25, GraphType.SPARSE_DAG);
        generateDataset("data/large_complex.json", 35, GraphType.COMPLEX_MIXED);
        generateDataset("data/large_multiple_sccs.json", 30, GraphType.MULTIPLE_SCCS);

        System.out.println("Generated 9 datasets in data/ directory");
    }

    public static void main(String[] args) throws IOException {
        generateAllDatasets();
    }
}

