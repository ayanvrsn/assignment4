package com.daa.graph.topo;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TopologicalSortTest {
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

        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.kahnTopologicalSort();

        assertEquals(3, order.size());
        assertEquals(0, order.indexOf(0));
        assertTrue(order.indexOf(1) > order.indexOf(0));
        assertTrue(order.indexOf(2) > order.indexOf(1));
    }

    @Test
    void testDAGWithMultipleSources() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 2, 1);

        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.kahnTopologicalSort();

        assertEquals(3, order.size());
        assertTrue(order.contains(0));
        assertTrue(order.contains(1));
        assertTrue(order.contains(2));
        assertTrue(order.indexOf(2) > order.indexOf(0));
        assertTrue(order.indexOf(2) > order.indexOf(1));
    }

    @Test
    void testCycleThrowsException() {
        Graph graph = new Graph(2);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);

        TopologicalSort topo = new TopologicalSort(graph, metrics);
        assertThrows(IllegalArgumentException.class, () -> topo.kahnTopologicalSort());
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(0);
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertTrue(order.isEmpty());
    }

    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1);
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }
}

