package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.util.*;

public class ProgressTracker {
    private MongoCollection<Document> progressCollection;
    private MongoCollection<Document> algorithmsCollection;
    private MongoCollection<Document> practiceCollection;
    private String userId; // You can make this dynamic based on user login

    public ProgressTracker() {
        initializeDatabase();
        this.userId = "user_001"; // Default user ID - replace with actual user system
    }

    private void initializeDatabase() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://23pd03_db_user:ldn2saUWgoBBINVw@cluster0.gwvt6zu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            MongoDatabase database = mongoClient.getDatabase("algodb");
            progressCollection = database.getCollection("user_progress");
            algorithmsCollection = database.getCollection("algorithms");
            practiceCollection = database.getCollection("practice");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    // Update quiz progress
    public void updateQuizProgress(String algorithmName, String questionId, boolean isCorrect, int score) {
        Document progressDoc = progressCollection.find(Filters.and(
            Filters.eq("user_id", userId),
            Filters.eq("algorithm_name", algorithmName)
        )).first();

        if (progressDoc == null) {
            // Create new progress document
            Document newProgress = new Document()
                .append("user_id", userId)
                .append("algorithm_name", algorithmName)
                .append("quiz_completed", isCorrect ? 1 : 0)
                .append("quiz_total", 1)
                .append("best_quiz_score", score)
                .append("last_attempt", new Date())
                .append("completed_questions", Arrays.asList(questionId))
                .append("total_attempts", 1);
            progressCollection.insertOne(newProgress);
        } else {
            // Update existing progress
            List<String> completedQuestions = progressDoc.getList("completed_questions", String.class);
            if (completedQuestions == null) completedQuestions = new ArrayList<>();
            
            if (!completedQuestions.contains(questionId) && isCorrect) {
                completedQuestions.add(questionId);
            }

            int quizCompleted = progressDoc.getInteger("quiz_completed", 0);
            int quizTotal = progressDoc.getInteger("quiz_total", 0);
            int bestScore = progressDoc.getInteger("best_quiz_score", 0);
            int totalAttempts = progressDoc.getInteger("total_attempts", 0);

            progressCollection.updateOne(
                Filters.and(
                    Filters.eq("user_id", userId),
                    Filters.eq("algorithm_name", algorithmName)
                ),
                Updates.combine(
                    Updates.set("quiz_completed", isCorrect ? quizCompleted + 1 : quizCompleted),
                    Updates.set("quiz_total", quizTotal + 1),
                    Updates.set("best_quiz_score", Math.max(bestScore, score)),
                    Updates.set("last_attempt", new Date()),
                    Updates.set("completed_questions", completedQuestions),
                    Updates.set("total_attempts", totalAttempts + 1)
                )
            );
        }
    }

    // Update problem completion status
    public void updateProblemCompletion(String algorithmName, String problemTitle, boolean completed) {
        Document progressDoc = progressCollection.find(Filters.and(
            Filters.eq("user_id", userId),
            Filters.eq("algorithm_name", algorithmName)
        )).first();

        if (progressDoc == null) {
            Document newProgress = new Document()
                .append("user_id", userId)
                .append("algorithm_name", algorithmName)
                .append("problems_completed", completed ? 1 : 0)
                .append("problems_total", 1)
                .append("completed_problems", completed ? Arrays.asList(problemTitle) : new ArrayList<>())
                .append("last_activity", new Date());
            progressCollection.insertOne(newProgress);
        } else {
            List<String> completedProblems = progressDoc.getList("completed_problems", String.class);
            if (completedProblems == null) completedProblems = new ArrayList<>();
            
            if (completed && !completedProblems.contains(problemTitle)) {
                completedProblems.add(problemTitle);
            } else if (!completed) {
                completedProblems.remove(problemTitle);
            }

            int problemsCompleted = completedProblems.size();
            int problemsTotal = progressDoc.getInteger("problems_total", 0);
            
            // Get total problems count from practice collection
            Document practiceDoc = practiceCollection.find(Filters.eq("topic", algorithmName)).first();
            if (practiceDoc != null) {
                List<Document> problems = practiceDoc.getList("hackerrank_problems", Document.class);
                problemsTotal = problems != null ? problems.size() : 0;
            }

            progressCollection.updateOne(
                Filters.and(
                    Filters.eq("user_id", userId),
                    Filters.eq("algorithm_name", algorithmName)
                ),
                Updates.combine(
                    Updates.set("problems_completed", problemsCompleted),
                    Updates.set("problems_total", problemsTotal),
                    Updates.set("completed_problems", completedProblems),
                    Updates.set("last_activity", new Date())
                )
            );
        }
    }

    // Get user's overall progress
    public Map<String, Object> getUserProgress() {
        Map<String, Object> progress = new HashMap<>();
        List<Document> userProgress = progressCollection.find(Filters.eq("user_id", userId)).into(new ArrayList<>());
        
        int totalAlgorithms = (int) algorithmsCollection.countDocuments();
        int attemptedAlgorithms = userProgress.size();
        
        int totalQuizCompleted = 0;
        int totalQuizTotal = 0;
        int totalProblemsCompleted = 0;
        int totalProblemsTotal = 0;
        
        List<Map<String, Object>> algorithmProgress = new ArrayList<>();
        
        for (Document doc : userProgress) {
            String algoName = doc.getString("algorithm_name");
            int quizCompleted = doc.getInteger("quiz_completed", 0);
            int quizTotal = doc.getInteger("quiz_total", 0);
            int problemsCompleted = doc.getInteger("problems_completed", 0);
            int problemsTotal = doc.getInteger("problems_total", 0);
            Date lastAttempt = doc.getDate("last_attempt");
            
            totalQuizCompleted += quizCompleted;
            totalQuizTotal += quizTotal;
            totalProblemsCompleted += problemsCompleted;
            totalProblemsTotal += problemsTotal;
            
            Map<String, Object> algoProgress = new HashMap<>();
            algoProgress.put("algorithm_name", algoName);
            algoProgress.put("quiz_progress", quizTotal > 0 ? (double) quizCompleted / quizTotal * 100 : 0);
            algoProgress.put("problems_progress", problemsTotal > 0 ? (double) problemsCompleted / problemsTotal * 100 : 0);
            algoProgress.put("last_attempt", lastAttempt);
            algoProgress.put("best_score", doc.getInteger("best_quiz_score", 0));
            
            algorithmProgress.add(algoProgress);
        }
        
        progress.put("total_algorithms", totalAlgorithms);
        progress.put("attempted_algorithms", attemptedAlgorithms);
        progress.put("overall_quiz_progress", totalQuizTotal > 0 ? (double) totalQuizCompleted / totalQuizTotal * 100 : 0);
        progress.put("overall_problems_progress", totalProblemsTotal > 0 ? (double) totalProblemsCompleted / totalProblemsTotal * 100 : 0);
        progress.put("algorithm_details", algorithmProgress);
        
        return progress;
    }

    // Get suggestions for next algorithm to learn
    public List<Map<String, Object>> getLearningSuggestions() {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // Get user's completed algorithms
        List<Document> userProgress = progressCollection.find(Filters.eq("user_id", userId)).into(new ArrayList<>());
        Set<String> attemptedAlgorithms = new HashSet<>();
        for (Document doc : userProgress) {
            attemptedAlgorithms.add(doc.getString("algorithm_name"));
        }
        
        // Find algorithms from same categories that user hasn't attempted
        List<Document> allAlgorithms = algorithmsCollection.find().into(new ArrayList<>());
        Map<String, List<Document>> algorithmsByCategory = new HashMap<>();
        
        for (Document algoDoc : allAlgorithms) {
            Document algorithm = algoDoc.get("algorithm", Document.class);
            if (algorithm != null) {
                String category = algorithm.getString("category");
                String algoName = algorithm.getString("name");
                
                if (!attemptedAlgorithms.contains(algoName)) {
                    algorithmsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(algoDoc);
                }
            }
        }
        
        // For each category where user has progress, suggest next algorithm
        for (Document progressDoc : userProgress) {
            String attemptedAlgo = progressDoc.getString("algorithm_name");
            Document attemptedAlgoDoc = algorithmsCollection.find(Filters.eq("algorithm.name", attemptedAlgo)).first();
            
            if (attemptedAlgoDoc != null) {
                Document algorithm = attemptedAlgoDoc.get("algorithm", Document.class);
                String category = algorithm.getString("category");
                
                if (algorithmsByCategory.containsKey(category)) {
                    List<Document> categoryAlgorithms = algorithmsByCategory.get(category);
                    for (Document suggestionDoc : categoryAlgorithms) {
                        Document suggestionAlgo = suggestionDoc.get("algorithm", Document.class);
                        Map<String, Object> suggestion = new HashMap<>();
                        suggestion.put("algorithm_name", suggestionAlgo.getString("name"));
                        suggestion.put("category", category);
                        suggestion.put("difficulty", suggestionAlgo.getString("difficulty"));
                        suggestion.put("description", suggestionAlgo.getString("description"));
                        suggestion.put("reason", "Similar to " + attemptedAlgo + " (same category)");
                        suggestions.add(suggestion);
                    }
                }
            }
        }
        
        // If no category-based suggestions, suggest by difficulty
        if (suggestions.isEmpty()) {
            for (Document algoDoc : allAlgorithms) {
                Document algorithm = algoDoc.get("algorithm", Document.class);
                if (algorithm != null) {
                    String algoName = algorithm.getString("name");
                    if (!attemptedAlgorithms.contains(algoName)) {
                        Map<String, Object> suggestion = new HashMap<>();
                        suggestion.put("algorithm_name", algoName);
                        suggestion.put("category", algorithm.getString("category"));
                        suggestion.put("difficulty", algorithm.getString("difficulty"));
                        suggestion.put("description", algorithm.getString("description"));
                        suggestion.put("reason", "Beginner-friendly algorithm");
                        suggestions.add(suggestion);
                    }
                }
            }
        }
        
        return suggestions.subList(0, Math.min(suggestions.size(), 3)); // Return top 3 suggestions
    }
}