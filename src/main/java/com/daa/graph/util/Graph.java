package com.daa.graph.util;

import java.util.*;


public class Graph {
    private final int numVertices;
    private final List<List<Edge>> adjacencyList;
    private final Map<Integer, Integer> nodeDurations; // Node durations for critical path
    private final List<Edge> edges;

   
    public static class Edge {
        public final int from;
        public final int to;
        public final int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("%d -> %d (w=%d)", from, to, weight);
        }
    }

    /**
     * Constructor for a graph with n vertices.
     * @param numVertices number of vertices
     */
    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.adjacencyList = new ArrayList<>();
        this.nodeDurations = new HashMap<>();
        this.edges = new ArrayList<>();
        
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
            nodeDurations.put(i, 1); // Default duration of 1
        }
    }

    /**
     * Add a directed edge from 'from' to 'to' with given weight.
     * @param from source vertex
     * @param to destination vertex
     * @param weight edge weight
     */
    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= numVertices || to < 0 || to >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex indices");
        }
        Edge edge = new Edge(from, to, weight);
        adjacencyList.get(from).add(edge);
        edges.add(edge);
    }

    /**
     * Set the duration for a node (used for critical path analysis).
     * @param node vertex index
     * @param duration duration value
     */
    public void setNodeDuration(int node, int duration) {
        nodeDurations.put(node, duration);
    }

    /**
     * Get the duration for a node.
     * @param node vertex index
     * @return duration
     */
    public int getNodeDuration(int node) {
        return nodeDurations.getOrDefault(node, 1);
    }

    /**
     * Get all outgoing edges from a vertex.
     * @param vertex vertex index
     * @return list of outgoing edges
     */
    public List<Edge> getOutgoingEdges(int vertex) {
        return adjacencyList.get(vertex);
    }

    /**
     * Get all edges in the graph.
     * @return list of all edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Get the number of vertices.
     * @return number of vertices
     */
    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Get the reversed graph (all edges reversed).
     * @return reversed graph
     */
    public Graph getReversed() {
        Graph reversed = new Graph(numVertices);
        for (Map.Entry<Integer, Integer> entry : nodeDurations.entrySet()) {
            reversed.setNodeDuration(entry.getKey(), entry.getValue());
        }
        for (Edge edge : edges) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
        }
        return reversed;
    }

    /**
     * Check if there is an edge from u to v.
     * @param u source vertex
     * @param v destination vertex
     * @return true if edge exists
     */
    public boolean hasEdge(int u, int v) {
        return adjacencyList.get(u).stream().anyMatch(e -> e.to == v);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with ").append(numVertices).append(" vertices:\n");
        for (int i = 0; i < numVertices; i++) {
            sb.append(i).append(" -> ");
            List<Edge> edges = adjacencyList.get(i);
            if (edges.isEmpty()) {
                sb.append("[]");
            } else {
                sb.append(edges);
            }
            sb.append(" (duration=").append(getNodeDuration(i)).append(")\n");
        }
        return sb.toString();
    }
}

