package com.dsa.algorithms.greedy;

import java.util.*;

/**
 * Kruskal's Algorithm (Greedy)
 *
 * Finds the Minimum Spanning Tree (MST) of a connected, undirected, weighted graph.
 * Works on an edge list representation and uses Union-Find for cycle detection.
 * Steps are recorded after each edge consideration for visualization.
 */
public class KruskalAlgorithm {

    /** Step representation for visualization */
    public static class Step {
        public int u, v, weight;
        public boolean taken; // whether edge was included in MST
        public int[] parent;
        public int[] rank;

        public Step(int u, int v, int weight, boolean taken, int[] parent, int[] rank) {
            this.u = u;
            this.v = v;
            this.weight = weight;
            this.taken = taken;
            this.parent = parent.clone();
            this.rank = rank.clone();
        }
    }

    private final List<Step> steps = new ArrayList<>();

    /** Edge class for Kruskal */
    private static class Edge implements Comparable<Edge> {
        int u, v, weight;
        Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    /** Union-Find data structure (Disjoint Set) */
    private static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x)
                parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) return;

            if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
            else if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
            else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    /**
     * Executes Kruskal's algorithm.
     * @param graph weighted adjacency matrix
     * @return List of MST edges in format [u, v, weight]
     */
    public List<int[]> findMST(int[][] graph) {
        steps.clear();
        int n = graph.length;
        List<Edge> edges = new ArrayList<>();

        // Build edge list from adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph[i][j] != 0)
                    edges.add(new Edge(i, j, graph[i][j]));
            }
        }

        // Sort edges by weight
        Collections.sort(edges);

        UnionFind uf = new UnionFind(n);
        List<int[]> mst = new ArrayList<>();

        for (Edge edge : edges) {
            int rootU = uf.find(edge.u);
            int rootV = uf.find(edge.v);
            boolean taken = false;

            if (rootU != rootV) {
                uf.union(rootU, rootV);
                mst.add(new int[]{edge.u, edge.v, edge.weight});
                taken = true;
            }

            // Record step for visualization
            steps.add(new Step(edge.u, edge.v, edge.weight, taken, uf.parent, uf.rank));
        }

        return mst;
    }

    /** Returns total cost of MST */
    public int getMSTCost(List<int[]> mstEdges) {
        return mstEdges.stream().mapToInt(e -> e[2]).sum();
    }

    /** Returns all recorded steps */
    public List<Step> getSteps() {
        return steps;
    }
}
