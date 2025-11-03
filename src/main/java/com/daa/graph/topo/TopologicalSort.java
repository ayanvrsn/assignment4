package com.daa.graph.topo;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;

import java.util.*;


public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;
    private List<Integer> topologicalOrder;

    /**
     * Constructor.
     * @param graph the input DAG
     * @param metrics metrics tracker
     */
    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Compute topological order using Kahn's algorithm.
     * @return topological ordering as a list of vertex indices
     */
    public List<Integer> kahnTopologicalSort() {
        metrics.startTimer();
        metrics.reset();

        int n = graph.getNumVertices();
        int[] inDegree = new int[n];
        topologicalOrder = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (Graph.Edge edge : graph.getOutgoingEdges(i)) {
                inDegree[edge.to]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementKahnPushes();
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementKahnPops();
            topologicalOrder.add(u);

            for (Graph.Edge edge : graph.getOutgoingEdges(u)) {
                int v = edge.to;
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementKahnPushes();
                }
            }
        }

        if (topologicalOrder.size() != n) {
            throw new IllegalArgumentException("Graph contains a cycle! Topological sort not possible.");
        }

        metrics.stopTimer();
        return topologicalOrder;
    }

    /**
     * Get the topological order (computes if not already computed).
     * Uses Kahn's algorithm.
     * @return topological ordering
     */
    public List<Integer> getTopologicalOrder() {
        if (topologicalOrder == null || topologicalOrder.isEmpty()) {
            kahnTopologicalSort();
        }
        return new ArrayList<>(topologicalOrder);
    }

    /**
     * Convert SCC-based topological order to original task order.
     * @param sccOrder topological order of SCCs
     * @param vertexToScc mapping from vertex to SCC index
     * @param sccs list of SCCs
     * @return topological order of original vertices
     */
    public static List<Integer> convertSccOrderToVertexOrder(
            List<Integer> sccOrder,
            Map<Integer, Integer> vertexToScc,
            List<List<Integer>> sccs) {
        List<Integer> vertexOrder = new ArrayList<>();
        
        for (int sccId : sccOrder) {
            vertexOrder.addAll(sccs.get(sccId));
        }
        
        return vertexOrder;
    }

    /**
     * Get formatted output of topological order.
     * @return formatted string
     */
    public String getTopologicalOrderString() {
        if (topologicalOrder == null || topologicalOrder.isEmpty()) {
            kahnTopologicalSort();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Topological Order:\n");
        sb.append(topologicalOrder);
        sb.append("\n");
        return sb.toString();
    }
}

