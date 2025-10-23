package com.dsa.complexity;

import com.dsa.complexity.data.ComplexityData;
import com.dsa.complexity.models.AlgorithmMetrics;

public class ComplexityDataTester {
    public static void main(String[] args) {
        System.out.println("🚀 Testing Complexity Data System...");
        
        // This will trigger static initialization and load all data
        System.out.println("Total algorithms loaded: " + ComplexityData.getAllAlgorithms().size());
        
        // Test various data access methods
        testCategoryData();
        testIndividualAlgorithms();
        testSearchFunctionality();
    }
    
    private static void testCategoryData() {
        System.out.println("\n📂 Testing Category Data:");
        for (String category : ComplexityData.getAvailableCategories()) {
            var algorithms = ComplexityData.getAlgorithmsByCategory(category);
            System.out.println("  " + category + ": " + algorithms.size() + " algorithms");
        }
    }
    
    private static void testIndividualAlgorithms() {
        System.out.println("\n🎯 Testing Individual Algorithms:");
        
        String[] testAlgorithms = {"Quick Sort", "Binary Search", "Dijkstra's Algorithm", "0/1 Knapsack"};
        
        for (String algoName : testAlgorithms) {
            AlgorithmMetrics algo = ComplexityData.getAlgorithmByName(algoName);
            if (algo != null) {
                System.out.println("  ✅ " + algo.getName());
                System.out.println("     Time: " + algo.getFormattedTimeComplexity());
                System.out.println("     Space: " + algo.getSpaceComplexity());
                System.out.println("     Category: " + algo.getCategory());
            } else {
                System.out.println("  ❌ " + algoName + " not found");
            }
        }
    }
    
    private static void testSearchFunctionality() {
        System.out.println("\n🔍 Testing Search Functionality:");
        
        String[] searchTerms = {"sort", "search", "graph", "dynamic"};
        
        for (String term : searchTerms) {
            var results = ComplexityData.searchAlgorithms(term);
            System.out.println("  Search '" + term + "': " + results.size() + " results");
        }
    }
}