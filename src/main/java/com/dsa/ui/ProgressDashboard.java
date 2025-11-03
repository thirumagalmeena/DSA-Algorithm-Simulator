package com.dsa.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class ProgressDashboard {
    private final Stage stage;
    private ProgressTracker progressTracker;

    public ProgressDashboard(Stage stage) {
        this.stage = stage;
        this.progressTracker = new ProgressTracker();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Header
        Label title = new Label("Your Learning Progress");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.5, 3, 3);");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

        // Get progress data
        Map<String, Object> progress = progressTracker.getUserProgress();
        List<Map<String, Object>> suggestions = progressTracker.getLearningSuggestions();

        // Overall Progress
        VBox overallProgress = createOverallProgressSection(progress);
        
        // Algorithm-specific Progress
        VBox algorithmProgress = createAlgorithmProgressSection(progress);
        
        // Learning Suggestions
        VBox suggestionsSection = createSuggestionsSection(suggestions);

        content.getChildren().addAll(overallProgress, algorithmProgress, suggestionsSection);
        scrollPane.setContent(content);

        // Back button
        Button backBtn = createBackButton();

        root.getChildren().addAll(title, scrollPane, backBtn);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Progress Dashboard - DSA Simulator");
    }

    private VBox createOverallProgressSection(Map<String, Object> progress) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #d6bceaff; -fx-border-radius: 10; -fx-padding: 20;");

        Label sectionTitle = new Label("📊 Overall Progress");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        int totalAlgorithms = (int) progress.get("total_algorithms");
        int attemptedAlgorithms = (int) progress.get("attempted_algorithms");
        double quizProgress = (double) progress.get("overall_quiz_progress");
        double problemsProgress = (double) progress.get("overall_problems_progress");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);

        // Algorithms attempted
        Label algoLabel = new Label("Algorithms Attempted:");
        algoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label algoValue = new Label(attemptedAlgorithms + " / " + totalAlgorithms);
        algoValue.setFont(Font.font("System", FontWeight.BOLD, 16));
        algoValue.setStyle("-fx-text-fill: #007bff;");

        // Quiz progress
        Label quizLabel = new Label("Quiz Completion:");
        quizLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label quizValue = new Label(String.format("%.1f%%", quizProgress));
        quizValue.setFont(Font.font("System", FontWeight.BOLD, 16));
        quizValue.setStyle("-fx-text-fill: #28a745;");

        // Problems progress
        Label problemsLabel = new Label("Problems Solved:");
        problemsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label problemsValue = new Label(String.format("%.1f%%", problemsProgress));
        problemsValue.setFont(Font.font("System", FontWeight.BOLD, 16));
        problemsValue.setStyle("-fx-text-fill: #dc3545;");

        statsGrid.add(algoLabel, 0, 0);
        statsGrid.add(algoValue, 1, 0);
        statsGrid.add(quizLabel, 0, 1);
        statsGrid.add(quizValue, 1, 1);
        statsGrid.add(problemsLabel, 0, 2);
        statsGrid.add(problemsValue, 1, 2);

        section.getChildren().addAll(sectionTitle, statsGrid);
        return section;
    }

    private VBox createAlgorithmProgressSection(Map<String, Object> progress) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 20;");

        Label sectionTitle = new Label("🔍 Algorithm-specific Progress");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox algorithmsContainer = new VBox(10);
        List<Map<String, Object>> algorithmDetails = (List<Map<String, Object>>) progress.get("algorithm_details");

        if (algorithmDetails.isEmpty()) {
            Label noProgress = new Label("No progress recorded yet. Start practicing algorithms!");
            noProgress.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
            algorithmsContainer.getChildren().add(noProgress);
        } else {
            for (Map<String, Object> algoProgress : algorithmDetails) {
                HBox algoCard = createAlgorithmProgressCard(algoProgress);
                algorithmsContainer.getChildren().add(algoCard);
            }
        }

        section.getChildren().addAll(sectionTitle, algorithmsContainer);
        return section;
    }

    private HBox createAlgorithmProgressCard(Map<String, Object> algoProgress) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER_LEFT);

        String algoName = (String) algoProgress.get("algorithm_name");
        double quizProgress = (double) algoProgress.get("quiz_progress");
        double problemsProgress = (double) algoProgress.get("problems_progress");
        int bestScore = (int) algoProgress.get("best_score");

        VBox infoBox = new VBox(5);
        
        Label nameLabel = new Label(algoName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label quizLabel = new Label(String.format("Quizzes: %.1f%%", quizProgress));
        quizLabel.setFont(Font.font("System", 12));
        quizLabel.setStyle("-fx-text-fill: #28a745;");

        Label problemsLabel = new Label(String.format("Problems: %.1f%%", problemsProgress));
        problemsLabel.setFont(Font.font("System", 12));
        problemsLabel.setStyle("-fx-text-fill: #dc3545;");

        Label scoreLabel = new Label("Best Score: " + bestScore + "%");
        scoreLabel.setFont(Font.font("System", 12));
        scoreLabel.setStyle("-fx-text-fill: #007bff;");

        infoBox.getChildren().addAll(nameLabel, quizLabel, problemsLabel, scoreLabel);
        card.getChildren().add(infoBox);

        return card;
    }

    private VBox createSuggestionsSection(List<Map<String, Object>> suggestions) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #e8f5e8; -fx-border-radius: 10; -fx-padding: 20;");

        Label sectionTitle = new Label("💡 Recommended Next Steps");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox suggestionsContainer = new VBox(10);

        if (suggestions.isEmpty()) {
            Label noSuggestions = new Label("Great job! You've made progress on all available algorithms.");
            noSuggestions.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            suggestionsContainer.getChildren().add(noSuggestions);
        } else {
            for (Map<String, Object> suggestion : suggestions) {
                VBox suggestionCard = createSuggestionCard(suggestion);
                suggestionsContainer.getChildren().add(suggestionCard);
            }
        }

        section.getChildren().addAll(sectionTitle, suggestionsContainer);
        return section;
    }

    private VBox createSuggestionCard(Map<String, Object> suggestion) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-padding: 15;");

        String algoName = (String) suggestion.get("algorithm_name");
        String category = (String) suggestion.get("category");
        String difficulty = (String) suggestion.get("difficulty");
        String description = (String) suggestion.get("description");
        String reason = (String) suggestion.get("reason");

        Label nameLabel = new Label(algoName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        HBox metaBox = new HBox(10);
        Label categoryLabel = new Label("Category: " + category);
        categoryLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12;");
        
        Label difficultyLabel = new Label("Difficulty: " + difficulty);
        difficultyLabel.setStyle("-fx-text-fill: " + getDifficultyColor(difficulty) + "; -fx-font-size: 12; -fx-font-weight: bold;");

        metaBox.getChildren().addAll(categoryLabel, difficultyLabel);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: #495057; -fx-font-size: 12;");
        descLabel.setWrapText(true);

        Label reasonLabel = new Label("💡 " + reason);
        reasonLabel.setStyle("-fx-text-fill: #007bff; -fx-font-size: 11; -fx-font-style: italic;");

        Button practiceBtn = new Button("Start Practicing");
        practiceBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 6;");
        practiceBtn.setOnAction(e -> {
            PracticeProblemsPage practicePage = new PracticeProblemsPage(stage);
            practicePage.show();
        });

        card.getChildren().addAll(nameLabel, metaBox, descLabel, reasonLabel, practiceBtn);
        return card;
    }

    private String getDifficultyColor(String difficulty) {
        if (difficulty == null) return "#6c757d";
        switch (difficulty.toLowerCase()) {
            case "beginner": return "#28a745";
            case "easy": return "#28a745";
            case "medium": return "#ffc107";
            case "hard": return "#dc3545";
            case "advanced": return "#dc3545";
            default: return "#6c757d";
        }
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Home");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(40);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        backBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c757d, #5a6268); " +
                       "-fx-text-fill: white; -fx-background-radius: 8; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 2, 2);");
        
        backBtn.setOnAction(e -> {
            HomePage home = new HomePage();
            try {
                home.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        return backBtn;
    }
}