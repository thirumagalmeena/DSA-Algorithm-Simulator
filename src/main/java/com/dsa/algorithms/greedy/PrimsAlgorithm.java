package com.dsa.algorithms.greedy;

import java.util.*;

public class PrimsAlgorithm {

    /** Step representation for visualization */
    public static class Step {
        public int vertex;             // vertex added to MST
        public int parent;             // its parent in MST
        public int cost;               // edge weight
        public int[] currentKey;       // snapshot of key array
        public boolean[] inMST;        // snapshot of visited nodes
        public String description;     // step description

        public Step(int vertex, int parent, int cost, int[] key, boolean[] inMST, String description) {
            this.vertex = vertex;
            this.parent = parent;
            this.cost = cost;
            this.currentKey = key.clone();
            this.inMST = inMST.clone();
            this.description = description;
        }
    }

    private final List<Step> steps = new ArrayList<>();

    public int[] findMST(int[][] graph) {
        steps.clear();
        int n = graph.length;

        int[] parent = new int[n];
        int[] key = new int[n];
        boolean[] inMST = new boolean[n];

        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        key[0] = 0;  // start from vertex 0

        // Record initial state
        steps.add(new Step(-1, -1, 0, key, inMST, "Initial state: Start from vertex 0"));

        for (int count = 0; count < n; count++) {
            int u = minKey(key, inMST);
            
            if (u == -1) break; // No more vertices to process
            
            inMST[u] = true;

            String description;
            if (parent[u] == -1) {
                description = "Start: Added vertex " + u + " to MST (starting vertex)";
            } else {
                description = "Added vertex " + u + " to MST via edge (" + parent[u] + "," + u + ") with weight " + key[u];
            }
            
            // Record step after adding vertex to MST
            steps.add(new Step(u, parent[u], key[u], key, inMST, description));

            // Update key values for adjacent vertices
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && !inMST[v] && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                    
                    // Record key update step
                    steps.add(new Step(-1, u, graph[u][v], key, inMST, 
                        "Updated key for vertex " + v + " to " + graph[u][v] + " via edge (" + u + "," + v + ")"));
                }
            }
        }

        return parent;
    }

    /** Helper to find the vertex with minimum key not yet in MST */
    private int minKey(int[] key, boolean[] inMST) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < key.length; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    /** Returns recorded steps for visualization */
    public List<Step> getSteps() {
        return steps;
    }

    /** Utility to calculate total MST cost */
    public int getMSTCost(int[][] graph, int[] parent) {
        int cost = 0;
        for (int i = 1; i < graph.length; i++) {
            if (parent[i] != -1) {
                cost += graph[i][parent[i]];
            }
        }
        return cost;
    }
}