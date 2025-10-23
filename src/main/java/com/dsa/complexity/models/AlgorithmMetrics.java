package com.dsa.complexity.models;

import java.util.Map;

public class AlgorithmMetrics {
    private String name;
    private TimeComplexity timeComplexity;
    private String spaceComplexity;
    private String description;
    private String[] keyPoints;
    private String[] whenToUse;
    private String realWorldExample;
    private Map<Integer, Double> runtimeData;
    private String category;

    // Static inner class for time complexity
    public static class TimeComplexity {
        private String best;
        private String average;
        private String worst;

        // Constructors
        public TimeComplexity() {}

        public TimeComplexity(String best, String average, String worst) {
            this.best = best;
            this.average = average;
            this.worst = worst;
        }

        // Getters and Setters
        public String getBest() { return best; }
        public void setBest(String best) { this.best = best; }

        public String getAverage() { return average; }
        public void setAverage(String average) { this.average = average; }

        public String getWorst() { return worst; }
        public void setWorst(String worst) { this.worst = worst; }

        @Override
        public String toString() {
            if (best != null && average != null && worst != null) {
                return String.format("Best: %s, Average: %s, Worst: %s", best, average, worst);
            } else {
                return "N/A";
            }
        }
    }

    // Constructors
    public AlgorithmMetrics() {}

    public AlgorithmMetrics(String name, TimeComplexity timeComplexity, String spaceComplexity, 
                           String description, String[] keyPoints, String[] whenToUse, 
                           String realWorldExample, Map<Integer, Double> runtimeData, String category) {
        this.name = name;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.description = description;
        this.keyPoints = keyPoints;
        this.whenToUse = whenToUse;
        this.realWorldExample = realWorldExample;
        this.runtimeData = runtimeData;
        this.category = category;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TimeComplexity getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(TimeComplexity timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String[] getKeyPoints() { return keyPoints; }
    public void setKeyPoints(String[] keyPoints) { this.keyPoints = keyPoints; }

    public String[] getWhenToUse() { return whenToUse; }
    public void setWhenToUse(String[] whenToUse) { this.whenToUse = whenToUse; }

    public String getRealWorldExample() { return realWorldExample; }
    public void setRealWorldExample(String realWorldExample) { this.realWorldExample = realWorldExample; }

    public Map<Integer, Double> getRuntimeData() { return runtimeData; }
    public void setRuntimeData(Map<Integer, Double> runtimeData) { this.runtimeData = runtimeData; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Helper methods
    public String getFormattedTimeComplexity() {
        if (timeComplexity != null) {
            return timeComplexity.toString();
        }
        return "N/A";
    }

    public double getRuntimeForSize(int size) {
        return runtimeData != null ? runtimeData.getOrDefault(size, 0.0) : 0.0;
    }
}