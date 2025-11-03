package com.daa.graph.util;


public class Metrics {
    private int dfsVisits = 0;
    private int edgeTraversals = 0;
    private int kahnPops = 0;
    private int kahnPushes = 0;
    private int relaxations = 0;
    private long startTime = 0;
    private long endTime = 0;

   
    public void startTimer() {
        startTime = System.nanoTime();
    }

    
    public void stopTimer() {
        endTime = System.nanoTime();
    }

    /**
     * Get elapsed time in milliseconds.
     * @return elapsed time in ms
     */
    public double getElapsedTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Get elapsed time in nanoseconds.
     * @return elapsed time in ns
     */
    public long getElapsedTimeNs() {
        return endTime - startTime;
    }

    public void incrementDfsVisits() {
        dfsVisits++;
    }

    public void incrementEdgeTraversals() {
        edgeTraversals++;
    }

    public void incrementKahnPops() {
        kahnPops++;
    }

    public void incrementKahnPushes() {
        kahnPushes++;
    }

    public void incrementRelaxations() {
        relaxations++;
    }

    public int getDfsVisits() {
        return dfsVisits;
    }

    public int getEdgeTraversals() {
        return edgeTraversals;
    }

    public int getKahnPops() {
        return kahnPops;
    }

    public int getKahnPushes() {
        return kahnPushes;
    }

    public int getRelaxations() {
        return relaxations;
    }

    
    public void reset() {
        dfsVisits = 0;
        edgeTraversals = 0;
        kahnPops = 0;
        kahnPushes = 0;
        relaxations = 0;
        startTime = 0;
        endTime = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Metrics{DFS Visits: %d, Edge Traversals: %d, Kahn Pops: %d, Kahn Pushes: %d, Relaxations: %d, Time: %.3f ms}",
            dfsVisits, edgeTraversals, kahnPops, kahnPushes, relaxations, getElapsedTimeMs()
        );
    }
}

