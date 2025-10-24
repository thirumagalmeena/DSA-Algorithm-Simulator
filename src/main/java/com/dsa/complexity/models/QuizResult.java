// com/dsa/complexity/models/QuizResult.java
package com.dsa.complexity.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizResult {
    private String category;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private LocalDateTime timestamp;
    private List<QuestionResult> questionResults;

    public QuizResult(String category, int totalQuestions) {
        this.category = category;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = 0;
        this.score = 0;
        this.timestamp = LocalDateTime.now();
        this.questionResults = new ArrayList<>();
    }

    public void addQuestionResult(QuestionResult result) {
        questionResults.add(result);
        if (result.isCorrect()) {
            correctAnswers++;
        }
        calculateScore();
    }

    private void calculateScore() {
        this.score = (int) ((double) correctAnswers / totalQuestions * 100);
    }

    // Getters
    public String getCategory() { return category; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<QuestionResult> getQuestionResults() { return questionResults; }

    // Setters (optional, but good practice)
    public void setCategory(String category) { this.category = category; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setScore(int score) { this.score = score; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setQuestionResults(List<QuestionResult> questionResults) { this.questionResults = questionResults; }

    // Helper methods
    public double getPercentage() {
        return totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
    }

    public String getPerformanceRating() {
        if (score >= 90) return "Excellent";
        if (score >= 80) return "Very Good";
        if (score >= 70) return "Good";
        if (score >= 60) return "Average";
        return "Needs Improvement";
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "category='" + category + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", score=" + score +
                ", timestamp=" + timestamp +
                ", questionResults=" + questionResults +
                '}';
    }
}