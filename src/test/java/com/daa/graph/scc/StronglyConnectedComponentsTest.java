package com.daa.graph.scc;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StronglyConnectedComponentsTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        List<List<Integer>> sccs = scc.findSCCs();

        // Each vertex should be its own SCC
        assertEquals(3, sccs.size());
        assertTrue(sccs.stream().allMatch(s -> s.size() == 1));
    }

    @Test
    void testSingleCycle() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        List<List<Integer>> sccs = scc.findSCCs();

        // All vertices should be in one SCC
        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
        assertTrue(sccs.get(0).contains(0));
        assertTrue(sccs.get(0).contains(1));
        assertTrue(sccs.get(0).contains(2));
    }

    @Test
    void testTwoSCCs() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        List<List<Integer>> sccs = scc.findSCCs();

        assertEquals(2, sccs.size());
        // Each cycle should be an SCC
        assertTrue(sccs.stream().anyMatch(s -> s.contains(0) && s.contains(1)));
        assertTrue(sccs.stream().anyMatch(s -> s.contains(2) && s.contains(3)));
    }

    @Test
    void testCondensationGraph() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        scc.findSCCs();
        Graph condensation = scc.buildCondensationGraph();

        // Should have 2 SCCs: {0,1} and {2}
        assertEquals(2, condensation.getNumVertices());
        
        // Check edge from SCC {0,1} to SCC {2}
        Map<Integer, Integer> vertexToScc = scc.getVertexToScc();
        int scc01 = vertexToScc.get(0);
        int scc2 = vertexToScc.get(2);
        assertTrue(condensation.hasEdge(scc01, scc2));
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(0);
        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        List<List<Integer>> sccs = scc.findSCCs();
        
        assertTrue(sccs.isEmpty());
    }

    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1);
        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, metrics);
        List<List<Integer>> sccs = scc.findSCCs();
        
        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
        assertEquals(0, sccs.get(0).get(0));
    }
}

