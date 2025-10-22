package com.dsa.ui;

public class ComprehensiveVisualizationTester {
    public static void main(String[] args) {
        System.out.println("🧪 COMPREHENSIVE VISUALIZATION TESTER 🧪");
        
        // Sorting Algorithms
        testCategory("SORTING ALGORITHMS");
        testVisualizer("BubbleSortVisualizer", "com.dsa.simulator.sorting.BubbleSortVisualizer");
        testVisualizer("QuickSortVisualizer", "com.dsa.simulator.sorting.QuickSortVisualizer");
        testVisualizer("MergeSortVisualizer", "com.dsa.simulator.sorting.MergeSortVisualizer");
        testVisualizer("InsertionSortVisualizer", "com.dsa.simulator.sorting.InsertionSortVisualizer");
        testVisualizer("SelectionSortVisualizer", "com.dsa.simulator.sorting.SelectionSortVisualizer");
        
        // Searching Algorithms
        testCategory("SEARCHING ALGORITHMS");
        testVisualizer("BinarySearchVisualizer", "com.dsa.simulator.searching.BinarySearchVisualizer");
        testVisualizer("LinearSearchVisualizer", "com.dsa.simulator.searching.LinearSearchVisualizer");
        
        // Graph Algorithms
        testCategory("GRAPH ALGORITHMS");
        testVisualizer("DijkstraVisualizer", "com.dsa.simulator.graphTraversal.DijkstraVisualizer");
        testVisualizer("TopologicalOrderingVisualizer", "com.dsa.simulator.graphTraversal.TopologicalOrderingVisualizer");
        
        // Greedy Algorithms
        testCategory("GREEDY ALGORITHMS");
        testVisualizer("JobSchedulingVisualizer", "com.dsa.simulator.greedy.JobSchedulingVisualizer");
        testVisualizer("KruskalVisualizer", "com.dsa.simulator.greedy.KruskalVisualizer");
        testVisualizer("PrimsVisualizer", "com.dsa.simulator.greedy.PrimsVisualizer");
        
        // Dynamic Programming
        testCategory("DYNAMIC PROGRAMMING");
        testVisualizer("CoinChangeVisualizer", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizer");
        testVisualizer("CoinChangeVisualizerDemo", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizerDemo");
        testVisualizer("FibonacciVisualizer", "com.dsa.simulator.dynamicProgramming.FibonacciVisualizer");
        testVisualizer("Knapsack01Visualizer", "com.dsa.simulator.dynamicProgramming.Knapsack01Visualizer");
        testVisualizer("LongestCommonSubsequenceVisualizer", "com.dsa.simulator.dynamicProgramming.LongestCommonSubsequenceVisualizer");
        testVisualizer("PascalTriangleVisualizer", "com.dsa.simulator.dynamicProgramming.PascalTriangleVisualizer");
        
        System.out.println("\n🎯 TESTING COMPLETE");
    }
    
    private static void testCategory(String category) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📂 " + category);
        System.out.println("=".repeat(50));
    }
    
    private static void testVisualizer(String name, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            System.out.print("✅ " + name + " - Class loaded");
            
            // Check if it extends Application
            if (javafx.application.Application.class.isAssignableFrom(clazz)) {
                System.out.println(" ✓ Extends Application");
            } else {
                System.out.println(" ❌ Does NOT extend Application - WILL NOT WORK!");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ " + name + " - Class not found: " + className);
        } catch (Exception e) {
            System.out.println("❌ " + name + " - Error: " + e.getMessage());
        }
    }
}