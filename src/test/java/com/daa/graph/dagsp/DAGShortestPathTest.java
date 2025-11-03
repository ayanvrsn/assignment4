package com.daa.graph.dagsp;

import com.daa.graph.util.Graph;
import com.daa.graph.util.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testShortestPathSimple() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);

        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        int[] distances = dagsp.shortestPathsFromSource(0);

        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(8, distances[2]);
    }

    @Test
    void testShortestPathMultiplePaths() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 4);

        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        int[] distances = dagsp.shortestPathsFromSource(0);

        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(7, distances[3]); // min(5+2, 3+4) = 7
    }

    @Test
    void testLongestPath() {
        Graph graph = new Graph(3);
        graph.setNodeDuration(0, 1);
        graph.setNodeDuration(1, 2);
        graph.setNodeDuration(2, 3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        int[] longestDistances = dagsp.longestPaths();

        // Longest path should be sum of all durations: 1 + 2 + 3 = 6
        assertEquals(6, longestDistances[2]);
    }

    @Test
    void testCriticalPath() {
        Graph graph = new Graph(3);
        graph.setNodeDuration(0, 5);
        graph.setNodeDuration(1, 3);
        graph.setNodeDuration(2, 4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);

        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.CriticalPathResult result = dagsp.findCriticalPath();

        assertNotNull(result);
        assertTrue(result.length >= 4); // At least one path length
        assertFalse(result.path.isEmpty());
    }

    @Test
    void testReconstructPath() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        dagsp.shortestPathsFromSource(0);
        
        java.util.List<Integer> path = dagsp.reconstructPath(2);
        assertNotNull(path);
        assertTrue(path.contains(0));
        assertTrue(path.contains(1));
        assertTrue(path.contains(2));
    }
}

