// com/dsa/complexity/models/QuestionResult.java
package com.dsa.complexity.models;

public class QuestionResult {
    private String question;
    private boolean correct;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;

    public QuestionResult(String question, boolean correct, String userAnswer, 
                         String correctAnswer, String explanation) {
        this.question = question;
        this.correct = correct;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    // Getters
    public String getQuestion() { return question; }
    public boolean isCorrect() { return correct; }
    public String getUserAnswer() { return userAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    // Setters (optional, but good practice)
    public void setQuestion(String question) { this.question = question; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    @Override
    public String toString() {
        return "QuestionResult{" +
                "question='" + question + '\'' +
                ", correct=" + correct +
                ", userAnswer='" + userAnswer + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}