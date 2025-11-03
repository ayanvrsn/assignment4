package com.daa.graph.dagsp;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;

import java.util.*;


public class DAGShortestPath {
    private final Graph dag;
    private final Metrics metrics;
    private List<Integer> topologicalOrder;
    private int[] distances;
    private int[] predecessors;

    /**
     * Constructor.
     * @param dag the input DAG
     * @param metrics metrics tracker
     */
    public DAGShortestPath(Graph dag, Metrics metrics) {
        this.dag = dag;
        this.metrics = metrics;
    }

    /**
     * Compute shortest paths from a source vertex.
     * Uses edge weights for path length.
     * @param source source vertex
     * @return array of shortest distances
     */
    public int[] shortestPathsFromSource(int source) {
        metrics.startTimer();
        metrics.reset();

        int n = dag.getNumVertices();
        distances = new int[n];
        predecessors = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        com.daa.graph.topo.TopologicalSort topoSort = 
            new com.daa.graph.topo.TopologicalSort(dag, new com.daa.graph.util.Metrics());
        topologicalOrder = topoSort.kahnTopologicalSort();

        for (int u : topologicalOrder) {
            if (distances[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : dag.getOutgoingEdges(u)) {
                    metrics.incrementRelaxations();
                    int v = edge.to;
                    int newDist = distances[u] + edge.weight;
                    if (newDist < distances[v]) {
                        distances[v] = newDist;
                        predecessors[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
        return distances.clone();
    }

    /**
     * Compute longest path (critical path) in the DAG.
     * Uses node durations: path length = sum of node durations along path.
     * @return array of longest distances from source (first vertex in topo order)
     */
    public int[] longestPaths() {
        metrics.startTimer();
        metrics.reset();

        int n = dag.getNumVertices();
        distances = new int[n];
        predecessors = new int[n];
        Arrays.fill(distances, Integer.MIN_VALUE);
        Arrays.fill(predecessors, -1);

        com.daa.graph.topo.TopologicalSort topoSort = 
            new com.daa.graph.topo.TopologicalSort(dag, new com.daa.graph.util.Metrics());
        topologicalOrder = topoSort.kahnTopologicalSort();

        for (int i = 0; i < n; i++) {
            distances[i] = dag.getNodeDuration(i);
        }

        for (int u : topologicalOrder) {
            for (Graph.Edge edge : dag.getOutgoingEdges(u)) {
                metrics.incrementRelaxations();
                int v = edge.to;
               
                int newDist = distances[u] + dag.getNodeDuration(v);
                if (newDist > distances[v]) {
                    distances[v] = newDist;
                    predecessors[v] = u;
                }
            }
        }

        metrics.stopTimer();
        return distances.clone();
    }

    /**
     * Find the critical path (longest path in DAG).
     * @return CriticalPathResult containing the path and its length
     */
    public CriticalPathResult findCriticalPath() {
        int[] longestDistances = longestPaths();
        
        // Find the vertex with maximum distance
        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;
        for (int i = 0; i < longestDistances.length; i++) {
            if (longestDistances[i] > maxDist) {
                maxDist = longestDistances[i];
                endVertex = i;
            }
        }

        List<Integer> path = reconstructPath(endVertex);
        
        return new CriticalPathResult(path, maxDist);
    }

    /**
     * Reconstruct a path from source to target using predecessors array.
     * @param target target vertex
     * @return list of vertices in the path
     */
    public List<Integer> reconstructPath(int target) {
        List<Integer> path = new ArrayList<>();
        int current = target;
        
        while (current != -1) {
            path.add(current);
            current = predecessors[current];
        }
        
        Collections.reverse(path);
        return path;
    }

    /**
     * Get shortest distances from source.
     * @return array of distances
     */
    public int[] getDistances() {
        return distances != null ? distances.clone() : null;
    }

    
    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }

        @Override
        public String toString() {
            return String.format("Critical Path: %s (length: %d)", path, length);
        }
    }
}

