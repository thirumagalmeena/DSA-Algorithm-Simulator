package com.dsa.complexity.models;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizQuestion {
    private String id;
    private String type;
    private String difficulty;
    private String question;
    private List<String> options;
    private Integer correctAnswer;       // For multiple choice (0-indexed)
    private Boolean correctAnswerBool;   // For true/false
    private List<Map<String, String>> pairs; // For matching questions
    private String explanation;
    private String category;

    // Constructors
    public QuizQuestion() {}

    // Custom deserializer to handle both int and boolean correctAnswer
    @JsonProperty("correctAnswer")
    private void unpackCorrectAnswer(Object correctAnswer) {
        if (correctAnswer instanceof Integer) {
            this.correctAnswer = (Integer) correctAnswer;
        } else if (correctAnswer instanceof Boolean) {
            this.correctAnswerBool = (Boolean) correctAnswer;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    // FIX: Provide both getter methods for compatibility
    public Integer getCorrectAnswer() { 
        return correctAnswer; 
    }
    
    public int getCorrectAnswerInt() {
        return correctAnswer != null ? correctAnswer : -1;
    }

    public void setCorrectAnswer(Integer correctAnswer) { 
        this.correctAnswer = correctAnswer; 
    }

    public Boolean getCorrectAnswerBool() { 
        return correctAnswerBool; 
    }
    
    public void setCorrectAnswerBool(Boolean correctAnswerBool) { 
        this.correctAnswerBool = correctAnswerBool; 
    }

    public List<Map<String, String>> getPairs() { return pairs; }
    public void setPairs(List<Map<String, String>> pairs) { this.pairs = pairs; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Helper methods
    public boolean isMultipleChoice() { return "multiple_choice".equals(type); }
    public boolean isTrueFalse() { return "true_false".equals(type); }
    public boolean isMatching() { return "matching".equals(type); }

    // Method to check if user answer is correct
    public boolean isAnswerCorrect(Object userAnswer) {
        if (isMultipleChoice() && userAnswer instanceof Integer) {
            return correctAnswer != null && correctAnswer.equals(userAnswer);
        } else if (isTrueFalse() && userAnswer instanceof Boolean) {
            return correctAnswerBool != null && correctAnswerBool.equals(userAnswer);
        } else if (isMatching() && userAnswer instanceof Map) {
            // For matching questions, we'd need more complex validation
            return true; // Simplified for now
        }
        return false;
    }

    // Get correct answer as string for display
    public String getCorrectAnswerString() {
        if (isMultipleChoice() && correctAnswer != null && options != null) {
            return options.get(correctAnswer);
        } else if (isTrueFalse() && correctAnswerBool != null) {
            return correctAnswerBool ? "True" : "False";
        } else if (isMatching()) {
            return "Matching pairs"; // Simplified
        }
        return "Unknown";
    }
}