package com.dsa.complexity;

import com.dsa.complexity.data.QuizData;
import com.dsa.complexity.models.QuizQuestion;

public class QuizDataTester {
    public static void main(String[] args) {
        System.out.println("🧪 Testing Quiz Data System...");
        
        // This will trigger static initialization and load all quiz data
        System.out.println("Total quiz questions loaded: " + QuizData.getTotalQuestionCount());
        
        // Test various data access methods
        testQuizCategories();
        testQuestionsByCategory();
        testQuestionTypes();
        testMixedQuestions();
        testDifficultyFiltering();
    }
    
    private static void testQuizCategories() {
        System.out.println("\n📂 Testing Quiz Categories:");
        for (String category : QuizData.getAvailableQuizCategories()) {
            int count = QuizData.getQuestionCountByCategory(category);
            System.out.println("  " + category + ": " + count + " questions");
        }
    }
    
    private static void testQuestionsByCategory() {
        System.out.println("\n🎯 Testing Questions by Category:");
        
        String[] testCategories = {"Sorting Algorithms", "Searching Algorithms", "Graph Algorithms"};
        
        for (String category : testCategories) {
            var questions = QuizData.getQuestionsByCategory(category);
            System.out.println("  " + category + ": " + questions.size() + " questions");
            
            // Show first 2 questions from each category
            for (int i = 0; i < Math.min(2, questions.size()); i++) {
                QuizQuestion question = questions.get(i);
                System.out.println("    Q" + (i+1) + ": " + question.getQuestion());
                System.out.println("      Type: " + question.getType());
                System.out.println("      Difficulty: " + question.getDifficulty());
                if (question.isMultipleChoice()) {
                    System.out.println("      Options: " + question.getOptions());
                    // Use the safe method instead
                    System.out.println("      Correct Answer Index: " + question.getCorrectAnswerInt());
                } else if (question.isTrueFalse()) {
                    System.out.println("      Correct Answer: " + question.getCorrectAnswerBool());
                }
                System.out.println("      Explanation: " + 
                    (question.getExplanation().length() > 50 ? 
                     question.getExplanation().substring(0, 50) + "..." : 
                     question.getExplanation()));
            }
        }
    }
    
    private static void testQuestionTypes() {
        System.out.println("\n📊 Testing Question Types:");
        
        var allCategories = QuizData.getAvailableQuizCategories();
        int mcCount = 0, tfCount = 0, matchCount = 0;
        
        for (String category : allCategories) {
            var questions = QuizData.getQuestionsByCategory(category);
            for (QuizQuestion question : questions) {
                if (question.isMultipleChoice()) mcCount++;
                else if (question.isTrueFalse()) tfCount++;
                else if (question.isMatching()) matchCount++;
            }
        }
        
        System.out.println("  Multiple Choice: " + mcCount + " questions");
        System.out.println("  True/False: " + tfCount + " questions");
        System.out.println("  Matching: " + matchCount + " questions");
        System.out.println("  Total: " + (mcCount + tfCount + matchCount) + " questions");
    }
    
    private static void testMixedQuestions() {
        System.out.println("\n🔀 Testing Mixed Questions:");
        
        var mixedQuestions = QuizData.getMixedQuestions(5);
        System.out.println("  Mixed quiz with 5 questions from all categories:");
        
        for (int i = 0; i < mixedQuestions.size(); i++) {
            QuizQuestion question = mixedQuestions.get(i);
            System.out.println("    " + (i+1) + ". [" + question.getCategory() + "] " + 
                             question.getQuestion() + " (" + question.getDifficulty() + ")");
        }
    }
    
    private static void testDifficultyFiltering() {
        System.out.println("\n🎚️ Testing Difficulty Filtering:");
        
        String category = "Sorting Algorithms";
        String[] difficulties = {"easy", "medium", "hard"};
        
        for (String difficulty : difficulties) {
            var questions = QuizData.getQuestionsByDifficulty(category, difficulty);
            System.out.println("  " + category + " - " + difficulty + ": " + questions.size() + " questions");
        }
    }
}