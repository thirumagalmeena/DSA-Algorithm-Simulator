// QuizData.java
package com.dsa.complexity.data;

import com.dsa.complexity.models.QuizQuestion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;

public class QuizData {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Map<String, List<QuizQuestion>> questionsByCategory = new HashMap<>();
    private static Map<String, QuizQuestion> questionsById = new HashMap<>();

    static {
        loadAllQuizData();
    }

    public static class QuizCategory {
        private String category;
        private List<QuizQuestion> questions;

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<QuizQuestion> getQuestions() { return questions; }
        public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
    }

    private static void loadAllQuizData() {
        try {
            String[] categories = {"sorting", "searching", "graph", "greedy", "dp"};
            
            for (String category : categories) {
                String resourcePath = "/complexity/quizzes/" + category + "_quiz.json";
                InputStream inputStream = QuizData.class.getResourceAsStream(resourcePath);
                
                if (inputStream != null) {
                    QuizCategory quizCategory = mapper.readValue(inputStream, QuizCategory.class);
                    
                    // Set category for each question
                    for (QuizQuestion question : quizCategory.getQuestions()) {
                        question.setCategory(quizCategory.getCategory());
                    }
                    
                    questionsByCategory.put(quizCategory.getCategory(), quizCategory.getQuestions());
                    
                    // Index by question ID
                    for (QuizQuestion question : quizCategory.getQuestions()) {
                        questionsById.put(question.getId(), question);
                    }
                    
                    inputStream.close();
                    System.out.println("✅ Loaded " + quizCategory.getQuestions().size() + 
                                     " quiz questions from " + category);
                } else {
                    System.err.println("❌ Could not load quiz resource: " + resourcePath);
                }
            }
            
            System.out.println("🎉 Total quiz questions loaded: " + questionsById.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error loading quiz data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Public access methods
    public static List<QuizQuestion> getQuestionsByCategory(String category) {
        return new ArrayList<>(questionsByCategory.getOrDefault(category, new ArrayList<>()));
    }

    public static List<QuizQuestion> getQuestionsByDifficulty(String category, String difficulty) {
        List<QuizQuestion> filtered = new ArrayList<>();
        for (QuizQuestion question : getQuestionsByCategory(category)) {
            if (question.getDifficulty().equalsIgnoreCase(difficulty)) {
                filtered.add(question);
            }
        }
        return filtered;
    }

    public static List<String> getAvailableQuizCategories() {
        return new ArrayList<>(questionsByCategory.keySet());
    }

    public static int getTotalQuestionCount() {
        return questionsById.size();
    }

    public static int getQuestionCountByCategory(String category) {
        return questionsByCategory.getOrDefault(category, new ArrayList<>()).size();
    }

    public static QuizQuestion getQuestionById(String id) {
        return questionsById.get(id);
    }

    public static List<QuizQuestion> getMixedQuestions(int count) {
        List<QuizQuestion> allQuestions = new ArrayList<>();
        for (List<QuizQuestion> categoryQuestions : questionsByCategory.values()) {
            allQuestions.addAll(categoryQuestions);
        }
        
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }
}