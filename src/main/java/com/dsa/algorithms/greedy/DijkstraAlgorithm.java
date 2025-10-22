package com.dsa.algorithms.greedy;

import java.util.*;

/**
 * Dijkstra's Algorithm implementation for finding the shortest path
 * from a single source to all other vertices in a weighted graph.
 * 
 * This class also records intermediate steps for visualization.
 */
public class DijkstraAlgorithm {

    /** Step representation for visualization */
    public static class Step {
        public int currentVertex;       // vertex being processed
        public int[] distance;          // snapshot of current distance array
        public boolean[] visited;       // snapshot of visited nodes
        public String description;      // textual description of the step

        public Step(int currentVertex, int[] distance, boolean[] visited, String description) {
            this.currentVertex = currentVertex;
            this.distance = distance.clone();
            this.visited = visited.clone();
            this.description = description;
        }
    }

    private final List<Step> steps = new ArrayList<>();

    /**
     * Performs Dijkstra’s algorithm.
     * @param graph adjacency matrix representing the weighted graph
     * @param source source vertex
     * @return array of shortest distances from source to all vertices
     */
    public int[] findShortestPaths(int[][] graph, int source) {
        steps.clear();
        int V = graph.length;
        int[] distance = new int[V];
        boolean[] visited = new boolean[V];

        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;

        steps.add(new Step(source, distance, visited, "Initial state: Source vertex = " + source));

        for (int count = 0; count < V - 1; count++) {
            int u = minDistance(distance, visited);

            if (u == -1) break; // all reachable vertices processed
            visited[u] = true;

            steps.add(new Step(u, distance, visited, "Selected vertex " + u + " with minimum distance " + distance[u]));

            for (int v = 0; v < V; v++) {
                if (!visited[v] && graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE &&
                    distance[u] + graph[u][v] < distance[v]) {

                    distance[v] = distance[u] + graph[u][v];
                    steps.add(new Step(u, distance, visited, 
                        "Updated distance of vertex " + v + " to " + distance[v] + " via " + u));
                }
            }
        }

        return distance;
    }

    /** Helper to find vertex with minimum distance not yet processed */
    private int minDistance(int[] distance, boolean[] visited) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < distance.length; v++) {
            if (!visited[v] && distance[v] <= min) {
                min = distance[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    /** Returns the recorded steps for visualization */
    public List<Step> getSteps() {
        return steps;
    }
}
