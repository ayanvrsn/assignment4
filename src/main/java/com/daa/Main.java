package com.daa;

import com.daa.graph.dagsp.DAGShortestPath;
import com.daa.graph.scc.StronglyConnectedComponents;
import com.daa.graph.topo.TopologicalSort;
import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;
import com.daa.graph.util.TaskGraphLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Main {
    private static final String[] DATASETS = {
        "data/small_pure_dag.json",
        "data/small_single_cycle.json",
        "data/small_two_cycles.json",
        "data/medium_mixed.json",
        "data/medium_multiple_sccs.json",
        "data/medium_dense.json",
        "data/large_sparse.json",
        "data/large_complex.json",
        "data/large_multiple_sccs.json"
    };

    public static void main(String[] args) {
        System.out.println("=== Assignment 4: Smart City Scheduling ===\n");

        try {
            com.daa.graph.util.DatasetGenerator.generateAllDatasets();
        } catch (IOException e) {
            System.err.println("Warning: Could not generate datasets. They may already exist.");
        }

        for (String datasetPath : DATASETS) {
            processDataset(datasetPath);
            System.out.println("\n" + "=".repeat(80) + "\n");
        }
    }

    private static void processDataset(String datasetPath) {
        System.out.println("Processing: " + datasetPath);
        System.out.println("-".repeat(80));

        try {
            Graph graph = TaskGraphLoader.loadGraph(datasetPath);
            System.out.println("Loaded graph with " + graph.getNumVertices() + " vertices and " 
                + graph.getEdges().size() + " edges");

            System.out.println("\n--- 1. Strongly Connected Components (SCC) ---");
            Metrics sccMetrics = new Metrics();
            StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, sccMetrics);
            
            List<List<Integer>> sccs = scc.findSCCs();
            System.out.println(scc.getSCCInfo());
            System.out.println("SCC Metrics: " + sccMetrics);

            Graph condensationGraph = scc.buildCondensationGraph();
            System.out.println("\nCondensation Graph:");
            System.out.println("  - Vertices (SCCs): " + condensationGraph.getNumVertices());
            System.out.println("  - Edges: " + condensationGraph.getEdges().size());

            System.out.println("\n--- 2. Topological Sort ---");
            
            Metrics topoMetrics = new Metrics();
            TopologicalSort topoSort = new TopologicalSort(condensationGraph, topoMetrics);
            
            try {
                List<Integer> sccTopoOrder = topoSort.kahnTopologicalSort();
                System.out.println("Topological Order (SCCs): " + sccTopoOrder);
                System.out.println("Topo Metrics: " + topoMetrics);

                Map<Integer, Integer> vertexToScc = scc.getVertexToScc();
                List<Integer> vertexOrder = TopologicalSort.convertSccOrderToVertexOrder(
                    sccTopoOrder, vertexToScc, sccs);
                System.out.println("Derived Vertex Order: " + vertexOrder);
            } catch (IllegalArgumentException e) {
                System.out.println("Cannot perform topological sort: " + e.getMessage());
            }

            System.out.println("\n--- 3. Shortest and Longest Paths in DAG ---");
            
            Metrics dagspMetrics = new Metrics();
            DAGShortestPath dagsp = new DAGShortestPath(condensationGraph, dagspMetrics);
            
            if (condensationGraph.getNumVertices() > 0) {
                try {
                    List<Integer> topoOrder = topoSort.getTopologicalOrder();
                    if (!topoOrder.isEmpty()) {
                        int source = topoOrder.get(0);
                        int[] shortestDistances = dagsp.shortestPathsFromSource(source);
                        
                        System.out.println("\nShortest Paths from source SCC " + source + ":");
                        for (int i = 0; i < shortestDistances.length; i++) {
                            if (shortestDistances[i] != Integer.MAX_VALUE) {
                                System.out.println("  SCC " + i + ": " + shortestDistances[i]);
                            }
                        }
                        System.out.println("DAGSP Metrics (shortest): " + dagspMetrics);
                    }
                } catch (Exception e) {
                    System.out.println("Shortest paths computation skipped: " + e.getMessage());
                }
            }

            Metrics longestMetrics = new Metrics();
            DAGShortestPath longestPath = new DAGShortestPath(condensationGraph, longestMetrics);
            
            try {
                DAGShortestPath.CriticalPathResult criticalPath = longestPath.findCriticalPath();
                System.out.println("\n" + criticalPath);
                System.out.println("Critical Path Metrics: " + longestMetrics);
            } catch (Exception e) {
                System.out.println("Critical path computation failed: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error loading dataset: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

