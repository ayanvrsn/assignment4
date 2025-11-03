package com.daa.graph.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads a task graph from a JSON file.
 * Expected format:
 * {
 *   "tasks": [
 *     {"id": 0, "name": "task1", "duration": 5, "dependencies": [1, 2]},
 *     ...
 *   ]
 * }
 */
public class TaskGraphLoader {
    private static final Gson gson = new Gson();
    private static final int DEFAULT_EDGE_WEIGHT = 1;

    /**
     * Load a graph from a JSON file.
     * @param filePath path to the JSON file
     * @return Graph object
     * @throws IOException if file cannot be read
     */
    public static Graph loadGraph(String filePath) throws IOException {
        JsonObject root = gson.fromJson(new FileReader(filePath), JsonObject.class);
        JsonArray tasks = root.getAsJsonArray("tasks");

        // First pass: count vertices
        int maxId = -1;
        Map<Integer, String> taskNames = new HashMap<>();
        
        for (JsonElement taskElem : tasks) {
            JsonObject task = taskElem.getAsJsonObject();
            int id = task.get("id").getAsInt();
            maxId = Math.max(maxId, id);
            if (task.has("name")) {
                taskNames.put(id, task.get("name").getAsString());
            }
        }

        int numVertices = maxId + 1;
        Graph graph = new Graph(numVertices);

        // Second pass: set durations and add edges
        for (JsonElement taskElem : tasks) {
            JsonObject task = taskElem.getAsJsonObject();
            int id = task.get("id").getAsInt();
            
            // Set node duration if provided
            if (task.has("duration")) {
                int duration = task.get("duration").getAsInt();
                graph.setNodeDuration(id, duration);
            }

            // Add dependency edges
            if (task.has("dependencies")) {
                JsonArray deps = task.getAsJsonArray("dependencies");
                for (JsonElement depElem : deps) {
                    int depId = depElem.getAsInt();
                    // Edge weight is the duration of the source node
                    int weight = task.has("duration") ? task.get("duration").getAsInt() : DEFAULT_EDGE_WEIGHT;
                    graph.addEdge(depId, id, weight);
                }
            }
        }

        return graph;
    }
}

