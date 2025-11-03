package com.daa.graph.scc;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;

import java.util.*;


public class StronglyConnectedComponents {
    private final Graph graph;
    private final Metrics metrics;
    private List<List<Integer>> sccs;
    private Map<Integer, Integer> vertexToScc;
    private Graph condensationGraph;

    /**
     * Constructor.
     * @param graph the input graph
     * @param metrics metrics tracker
     */
    public StronglyConnectedComponents(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Find all strongly connected components.
     * @return list of SCCs, each SCC is a list of vertex indices
     */
    public List<List<Integer>> findSCCs() {
        metrics.startTimer();
        metrics.reset();
        
        int n = graph.getNumVertices();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        sccs = new ArrayList<>();
        vertexToScc = new HashMap<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                firstDFS(i, visited, stack);
            }
        }

        Graph reversedGraph = graph.getReversed();
        Arrays.fill(visited, false);

        int sccId = 0;
        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (!visited[v]) {
                List<Integer> scc = new ArrayList<>();
                secondDFS(v, reversedGraph, visited, scc, sccId);
                sccs.add(scc);
                sccId++;
            }
        }

        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToScc.put(vertex, i);
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    
    private void firstDFS(int v, boolean[] visited, Stack<Integer> stack) {
        visited[v] = true;
        metrics.incrementDfsVisits();

        for (Graph.Edge edge : graph.getOutgoingEdges(v)) {
            metrics.incrementEdgeTraversals();
            int neighbor = edge.to;
            if (!visited[neighbor]) {
                firstDFS(neighbor, visited, stack);
            }
        }

        stack.push(v);
    }

    
    private void secondDFS(int v, Graph reversedGraph, boolean[] visited, 
                           List<Integer> scc, int sccId) {
        visited[v] = true;
        scc.add(v);
        metrics.incrementDfsVisits();

        for (Graph.Edge edge : reversedGraph.getOutgoingEdges(v)) {
            metrics.incrementEdgeTraversals();
            int neighbor = edge.to;
            if (!visited[neighbor]) {
                secondDFS(neighbor, reversedGraph, visited, scc, sccId);
            }
        }
    }

    /**
     * Get the mapping from vertex to SCC index.
     * @return map from vertex index to SCC index
     */
    public Map<Integer, Integer> getVertexToScc() {
        return vertexToScc;
    }

    /**
     * Build the condensation graph (DAG of SCCs).
     * @return condensation graph
     */
    public Graph buildCondensationGraph() {
        if (vertexToScc == null || vertexToScc.isEmpty()) {
            findSCCs();
        }

        int numSccs = sccs.size();
        condensationGraph = new Graph(numSccs);

        for (int i = 0; i < sccs.size(); i++) {
            int maxDuration = 0;
            for (int vertex : sccs.get(i)) {
                maxDuration = Math.max(maxDuration, graph.getNodeDuration(vertex));
            }
            condensationGraph.setNodeDuration(i, maxDuration);
        }

        Set<String> addedEdges = new HashSet<>();
        for (Graph.Edge edge : graph.getEdges()) {
            int fromScc = vertexToScc.get(edge.from);
            int toScc = vertexToScc.get(edge.to);
            
            if (fromScc != toScc) {
                String edgeKey = fromScc + "->" + toScc;
                if (!addedEdges.contains(edgeKey)) {
                    addedEdges.add(edgeKey);
                    condensationGraph.addEdge(fromScc, toScc, edge.weight);
                }
            }
        }

        return condensationGraph;
    }

    /**
     * Get the condensation graph.
     * @return condensation graph (built if not already built)
     */
    public Graph getCondensationGraph() {
        if (condensationGraph == null) {
            buildCondensationGraph();
        }
        return condensationGraph;
    }

    /**
     * Get SCC information as a formatted string.
     * @return formatted string with SCCs and their sizes
     */
    public String getSCCInfo() {
        if (sccs == null || sccs.isEmpty()) {
            findSCCs();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Strongly Connected Components:\n");
        sb.append("Total SCCs: ").append(sccs.size()).append("\n");
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            sb.append(String.format("SCC %d: %s (size: %d)\n", i, scc, scc.size()));
        }
        return sb.toString();
    }
}

