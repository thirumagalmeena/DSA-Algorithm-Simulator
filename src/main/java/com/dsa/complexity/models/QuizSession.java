// QuizSession.java
package com.dsa.complexity.models;

import java.util.*;

public class QuizSession {
    private String category;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex;
    private int score;
    private int totalQuestions;
    private long startTime;
    private Map<String, Boolean> userAnswers;
    private String difficulty;

    public QuizSession(String category, List<QuizQuestion> questions, String difficulty) {
        this.category = category;
        this.questions = questions;
        this.difficulty = difficulty;
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.totalQuestions = questions.size();
        this.startTime = System.currentTimeMillis();
        this.userAnswers = new HashMap<>();
    }

    // Getters
    public String getCategory() { return category; }
    public List<QuizQuestion> getQuestions() { return questions; }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public long getStartTime() { return startTime; }
    public Map<String, Boolean> getUserAnswers() { return userAnswers; }
    public String getDifficulty() { return difficulty; }

    public QuizQuestion getCurrentQuestion() {
        return currentQuestionIndex < totalQuestions ? questions.get(currentQuestionIndex) : null;
    }

    public boolean hasNextQuestion() {
        return currentQuestionIndex < totalQuestions - 1;
    }

    public void nextQuestion() {
        if (hasNextQuestion()) {
            currentQuestionIndex++;
        }
    }

    public boolean isComplete() {
        return currentQuestionIndex >= totalQuestions;
    }

    public void submitAnswer(boolean isCorrect) {
        String questionId = getCurrentQuestion().getId();
        userAnswers.put(questionId, isCorrect);
        if (isCorrect) {
            score++;
        }
    }

    public double getPercentage() {
        return totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    public String getTimeElapsedFormatted() {
        long seconds = getTimeElapsed() / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}