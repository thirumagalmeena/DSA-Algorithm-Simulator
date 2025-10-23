package com.dsa.complexity.data;

import com.dsa.complexity.models.AlgorithmMetrics;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;

public class ComplexityData {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Map<String, List<AlgorithmMetrics>> algorithmsByCategory = new HashMap<>();
    private static Map<String, AlgorithmMetrics> algorithmsByName = new HashMap<>();

    static {
        loadAllData();
    }

    // Main data structure to hold category information
    public static class CategoryData {
        private String category;
        private List<AlgorithmMetrics> algorithms;

        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public List<AlgorithmMetrics> getAlgorithms() { return algorithms; }
        public void setAlgorithms(List<AlgorithmMetrics> algorithms) { this.algorithms = algorithms; }
    }

    // Load all JSON data files
    private static void loadAllData() {
        try {
            String[] categories = {"sorting", "searching", "graph", "greedy", "dp"};
            
            for (String category : categories) {
                String resourcePath = "/complexity/" + category + ".json";
                InputStream inputStream = ComplexityData.class.getResourceAsStream(resourcePath);
                
                if (inputStream != null) {
                    CategoryData categoryData = mapper.readValue(inputStream, CategoryData.class);
                    
                    // Set category for each algorithm
                    for (AlgorithmMetrics algorithm : categoryData.getAlgorithms()) {
                        algorithm.setCategory(categoryData.getCategory());
                    }
                    
                    algorithmsByCategory.put(categoryData.getCategory(), categoryData.getAlgorithms());
                    
                    // Also index by algorithm name for quick lookup
                    for (AlgorithmMetrics algorithm : categoryData.getAlgorithms()) {
                        algorithmsByName.put(algorithm.getName().toLowerCase(), algorithm);
                    }
                    
                    inputStream.close();
                    System.out.println("✅ Loaded " + categoryData.getAlgorithms().size() + 
                                     " algorithms from " + category);
                } else {
                    System.err.println("❌ Could not load resource: " + resourcePath);
                }
            }
            
            System.out.println("🎉 Total algorithms loaded: " + algorithmsByName.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error loading complexity data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Public methods to access data

    /**
     * Get all algorithms organized by category
     */
    public static Map<String, List<AlgorithmMetrics>> getAlgorithmsByCategory() {
        return new HashMap<>(algorithmsByCategory);
    }

    /**
     * Get all algorithms as a flat list
     */
    public static List<AlgorithmMetrics> getAllAlgorithms() {
        List<AlgorithmMetrics> allAlgorithms = new ArrayList<>();
        for (List<AlgorithmMetrics> categoryAlgorithms : algorithmsByCategory.values()) {
            allAlgorithms.addAll(categoryAlgorithms);
        }
        return allAlgorithms;
    }

    /**
     * Get algorithms by specific category
     */
    public static List<AlgorithmMetrics> getAlgorithmsByCategory(String category) {
        return algorithmsByCategory.getOrDefault(category, new ArrayList<>());
    }

    /**
     * Get specific algorithm by name (case-insensitive)
     */
    public static AlgorithmMetrics getAlgorithmByName(String name) {
        return algorithmsByName.get(name.toLowerCase());
    }

    /**
     * Get all available categories
     */
    public static List<String> getAvailableCategories() {
        return new ArrayList<>(algorithmsByCategory.keySet());
    }

    /**
     * Search algorithms by keyword in name or description
     */
    public static List<AlgorithmMetrics> searchAlgorithms(String keyword) {
        List<AlgorithmMetrics> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase();
        
        for (AlgorithmMetrics algorithm : getAllAlgorithms()) {
            if (algorithm.getName().toLowerCase().contains(searchTerm) ||
                algorithm.getDescription().toLowerCase().contains(searchTerm)) {
                results.add(algorithm);
            }
        }
        
        return results;
    }

    /**
     * Get algorithms with specific time complexity
     */
    public static List<AlgorithmMetrics> getAlgorithmsByTimeComplexity(String complexity) {
        List<AlgorithmMetrics> results = new ArrayList<>();
        
        for (AlgorithmMetrics algorithm : getAllAlgorithms()) {
            if (algorithm.getTimeComplexity() != null) {
                String avgComplexity = algorithm.getTimeComplexity().getAverage();
                if (avgComplexity != null && avgComplexity.equalsIgnoreCase(complexity)) {
                    results.add(algorithm);
                }
            }
        }
        
        return results;
    }

    /**
     * Get runtime data for charting
     */
    public static Map<String, Map<Integer, Double>> getRuntimeDataForChart(List<String> algorithmNames) {
        Map<String, Map<Integer, Double>> chartData = new HashMap<>();
        
        for (String algorithmName : algorithmNames) {
            AlgorithmMetrics algorithm = getAlgorithmByName(algorithmName);
            if (algorithm != null && algorithm.getRuntimeData() != null) {
                chartData.put(algorithmName, algorithm.getRuntimeData());
            }
        }
        
        return chartData;
    }

    /**
     * Get comparison data for multiple algorithms
     */
    public static Map<String, Object> getComparisonData(List<String> algorithmNames) {
        Map<String, Object> comparison = new HashMap<>();
        List<Map<String, String>> algorithmData = new ArrayList<>();
        
        for (String algorithmName : algorithmNames) {
            AlgorithmMetrics algorithm = getAlgorithmByName(algorithmName);
            if (algorithm != null) {
                Map<String, String> algoInfo = new HashMap<>();
                algoInfo.put("name", algorithm.getName());
                algoInfo.put("timeComplexity", algorithm.getFormattedTimeComplexity());
                algoInfo.put("spaceComplexity", algorithm.getSpaceComplexity());
                algoInfo.put("category", algorithm.getCategory());
                algorithmData.add(algoInfo);
            }
        }
        
        comparison.put("algorithms", algorithmData);
        return comparison;
    }

    // Test method to verify data loading
    public static void main(String[] args) {
        // This will automatically load data when class is initialized
        System.out.println("🧪 Testing Complexity Data Loader...");
        
        // Test getting all categories
        List<String> categories = getAvailableCategories();
        System.out.println("📂 Available categories: " + categories);
        
        // Test getting algorithms by category
        for (String category : categories) {
            List<AlgorithmMetrics> algorithms = getAlgorithmsByCategory(category);
            System.out.println("📊 " + category + ": " + algorithms.size() + " algorithms");
            for (AlgorithmMetrics algo : algorithms) {
                System.out.println("   • " + algo.getName() + " - " + algo.getFormattedTimeComplexity());
            }
        }
        
        // Test searching
        List<AlgorithmMetrics> searchResults = searchAlgorithms("sort");
        System.out.println("🔍 Search 'sort' found: " + searchResults.size() + " algorithms");
        
        // Test getting specific algorithm
        AlgorithmMetrics quickSort = getAlgorithmByName("Quick Sort");
        if (quickSort != null) {
            System.out.println("🎯 Quick Sort Details:");
            System.out.println("   Time: " + quickSort.getFormattedTimeComplexity());
            System.out.println("   Space: " + quickSort.getSpaceComplexity());
            System.out.println("   Description: " + quickSort.getDescription().substring(0, 50) + "...");
        }
    }
}