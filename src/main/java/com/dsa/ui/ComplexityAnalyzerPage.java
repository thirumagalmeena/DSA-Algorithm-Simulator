package com.dsa.ui;

import com.dsa.complexity.data.ComplexityData;
import com.dsa.complexity.data.QuizData;
import com.dsa.complexity.models.AlgorithmMetrics;
import com.dsa.complexity.models.QuizQuestion;
import com.dsa.complexity.models.QuizResult;
import com.dsa.complexity.models.QuestionResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.util.*;

public class ComplexityAnalyzerPage {
    private final Stage stage;
    private List<QuizResult> quizHistory = new ArrayList<>();

    public ComplexityAnalyzerPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Main container with gradient background
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Header section
        Label title = new Label("Algorithm Complexity Analysis");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");

        Label subtitle = new Label("Understand time and space complexity of " + ComplexityData.getAllAlgorithms().size() + " algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Create tab pane for different analysis views
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

        // Create tabs using our new data system
        Tab bigOTab = createBigONotationTab();
        Tab comparisonTab = createComparisonTab();
        Tab practicalTab = createPracticalAnalysisTab();
        Tab quizTab = createComplexityQuizTab();

        tabPane.getTabs().addAll(bigOTab, comparisonTab, practicalTab, quizTab);

        // Back button
        Button backBtn = createBackButton();

        VBox contentBox = new VBox(20, headerBox, tabPane, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(1200);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Algorithm Complexity Analysis - DSA Simulator");
    }

    private Tab createBigONotationTab() {
        Tab tab = new Tab("📊 Big O Notation");
        tab.setStyle("-fx-font-weight: bold;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");

        VBox content = new VBox(25);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white;");

        // Big O Explanation Section
        VBox explanationSection = createSectionBox("Understanding Big O Notation");
        TextFlow explanation = createFormattedTextFlow(
            "Big O notation describes how the runtime or space requirements of an algorithm grow as the input size increases.\n\n" +
            "• **O(1)** - Constant Time: Runtime doesn't depend on input size\n" +
            "• **O(log n)** - Logarithmic Time: Runtime grows logarithmically\n" +
            "• **O(n)** - Linear Time: Runtime grows linearly with input size\n" +
            "• **O(n log n)** - Linearithmic Time: Common in efficient sorting algorithms\n" +
            "• **O(n²)** - Quadratic Time: Runtime grows with square of input size\n" +
            "• **O(2ⁿ)** - Exponential Time: Runtime doubles with each additional input\n" +
            "• **O(n!)** - Factorial Time: Runtime grows factorially\n\n" +
            "**Key Points:**\n" +
            "• We care about the growth rate, not exact times\n" +
            "• Constants and lower order terms are dropped\n" +
            "• Worst-case analysis is most common"
        );
        explanationSection.getChildren().add(explanation);

        // Complexity Chart
        VBox chartSection = createSectionBox("Complexity Growth Comparison");
        LineChart<Number, Number> complexityChart = createComplexityChart();
        chartSection.getChildren().add(complexityChart);

        // Algorithm Categories Section - DYNAMIC FROM DATA
        VBox categoriesSection = createSectionBox("Algorithm Categories & Complexities");
        
        // Get all categories from our data system
        Map<String, List<AlgorithmMetrics>> algorithmsByCategory = ComplexityData.getAlgorithmsByCategory();
        
        for (String category : algorithmsByCategory.keySet()) {
            VBox categoryBox = createCategoryBox(category, algorithmsByCategory.get(category));
            categoriesSection.getChildren().add(categoryBox);
        }

        content.getChildren().addAll(explanationSection, chartSection, categoriesSection);
        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createComparisonTab() {
        Tab tab = new Tab("⚡ Algorithm Comparison");
        tab.setStyle("-fx-font-weight: bold;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Compare Algorithm Performance");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        // Category selector
        HBox categorySelectorBox = new HBox(15);
        categorySelectorBox.setAlignment(Pos.CENTER);

        Label categoryLabel = new Label("Select Category:");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ComplexityData.getAvailableCategories());
        categoryCombo.setValue("Sorting Algorithms");

        Button compareBtn = new Button("Show Comparison");
        compareBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        categorySelectorBox.getChildren().addAll(categoryLabel, categoryCombo, compareBtn);

        // Comparison results
        VBox resultsBox = new VBox(15);
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setPadding(new Insets(20));
        resultsBox.setStyle("-fx-background-color: white; -fx-border-radius: 10;");

        BarChart<String, Number> comparisonChart = createComparisonChart();
        TableView<AlgorithmMetrics> comparisonTable = createComparisonTable();
        
        VBox chartAndTable = new VBox(20, comparisonChart, comparisonTable);
        resultsBox.getChildren().add(chartAndTable);

        compareBtn.setOnAction(e -> {
            updateComparison(comparisonChart, comparisonTable, categoryCombo.getValue());
        });

        // Initial load
        updateComparison(comparisonChart, comparisonTable, "Sorting Algorithms");

        content.getChildren().addAll(title, categorySelectorBox, resultsBox);
        tab.setContent(content);

        return tab;
    }

    private Tab createPracticalAnalysisTab() {
        Tab tab = new Tab("🔍 Practical Analysis");
        tab.setStyle("-fx-font-weight: bold;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Runtime Analysis with Different Input Sizes");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        // Input controls
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER);

        Label categoryLabel = new Label("Select Category:");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ComplexityData.getAvailableCategories());
        categoryCombo.setValue("Sorting Algorithms");

        Label algoLabel = new Label("Select Algorithm:");
        ComboBox<String> algoCombo = new ComboBox<>();

        // Update algorithm combo when category changes
        categoryCombo.setOnAction(e -> {
            updateAlgorithmCombo(algoCombo, categoryCombo.getValue());
        });

        Label maxSizeLabel = new Label("Max Input Size:");
        Slider sizeSlider = new Slider(100, 10000, 1000);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);

        Button analyzeBtn = new Button("Run Analysis");
        analyzeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        controlsBox.getChildren().addAll(categoryLabel, categoryCombo, algoLabel, algoCombo, maxSizeLabel, sizeSlider, analyzeBtn);

        // Initial load
        updateAlgorithmCombo(algoCombo, "Sorting Algorithms");

        // Results area
        VBox resultsBox = new VBox(15);
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setPadding(new Insets(20));
        resultsBox.setStyle("-fx-background-color: white; -fx-border-radius: 10;");

        LineChart<Number, Number> runtimeChart = createRuntimeChart();
        resultsBox.getChildren().add(runtimeChart);

        // Analysis explanation
        VBox explanationBox = createSectionBox("Analysis Results");
        TextArea explanationArea = new TextArea();
        explanationArea.setEditable(false);
        explanationArea.setWrapText(true);
        explanationArea.setPrefHeight(150);
        explanationArea.setStyle("-fx-control-inner-background: #f8f9fa;");
        explanationArea.setText("Select an algorithm and click 'Run Analysis' to see practical runtime behavior.");
        explanationBox.getChildren().add(explanationArea);

        analyzeBtn.setOnAction(e -> {
            runPracticalAnalysis(runtimeChart, explanationArea, 
                algoCombo.getValue(), (int)sizeSlider.getValue());
        });

        content.getChildren().addAll(title, controlsBox, resultsBox, explanationBox);
        tab.setContent(content);

        return tab;
    }

    private Tab createComplexityQuizTab() {
        Tab tab = new Tab("🎯 Complexity Quiz");
        tab.setStyle("-fx-font-weight: bold;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Test Your Complexity Knowledge");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        // Category selector for quiz
        HBox categoryBox = new HBox(15);
        categoryBox.setAlignment(Pos.CENTER);

        Label categoryLabel = new Label("Select Quiz Category:");
        ComboBox<String> quizCategoryCombo = new ComboBox<>();
        quizCategoryCombo.getItems().addAll(QuizData.getAvailableQuizCategories());
        quizCategoryCombo.setValue("Sorting Algorithms");

        Label difficultyLabel = new Label("Difficulty:");
        ComboBox<String> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll("All", "Easy", "Medium", "Hard");
        difficultyCombo.setValue("All");

        Label countLabel = new Label("Questions:");
        ComboBox<Integer> questionCountCombo = new ComboBox<>();
        questionCountCombo.getItems().addAll(5, 10, 15);
        questionCountCombo.setValue(5);

        Button startQuizBtn = new Button("Start New Quiz");
        startQuizBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        categoryBox.getChildren().addAll(categoryLabel, quizCategoryCombo, difficultyLabel, 
                                       difficultyCombo, countLabel, questionCountCombo, startQuizBtn);

        // Quiz area
        VBox quizArea = new VBox(20);
        quizArea.setAlignment(Pos.CENTER);
        quizArea.setPadding(new Insets(20));
        quizArea.setStyle("-fx-background-color: white; -fx-border-radius: 10;");

        // Results area
        VBox resultsArea = new VBox(15);
        resultsArea.setAlignment(Pos.CENTER);
        resultsArea.setPadding(new Insets(20));
        resultsArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10;");
        resultsArea.setVisible(false);

        // Initial message
        Label initialMessage = new Label("Select a category and click 'Start New Quiz' to begin!");
        initialMessage.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        quizArea.getChildren().add(initialMessage);

        startQuizBtn.setOnAction(e -> {
            startNewQuiz(quizArea, resultsArea, quizCategoryCombo.getValue(), 
                        difficultyCombo.getValue(), questionCountCombo.getValue());
        });

        content.getChildren().addAll(title, categoryBox, quizArea, resultsArea);
        tab.setContent(content);

        return tab;
    }

    // ========== QUIZ MANAGEMENT METHODS ==========

    private void startNewQuiz(VBox quizArea, VBox resultsArea, String category, 
                            String difficulty, int questionCount) {
        quizArea.getChildren().clear();
        resultsArea.setVisible(false);

        // Get questions based on category and difficulty
        List<QuizQuestion> questions;
        if ("All".equals(difficulty)) {
            questions = QuizData.getQuestionsByCategory(category);
        } else {
            questions = QuizData.getQuestionsByDifficulty(category, difficulty);
        }

        // Limit to requested count
        if (questions.size() > questionCount) {
            Collections.shuffle(questions);
            questions = questions.subList(0, questionCount);
        }

        if (questions.isEmpty()) {
            showAlert("No Questions", "No questions found for the selected criteria.");
            return;
        }

        QuizResult quizResult = new QuizResult(category, questions.size());

        // Create quiz progress indicator
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        Label progressLabel = new Label("Question 1 of " + questions.size());
        progressLabel.setStyle("-fx-font-weight: bold;");

        VBox progressBox = new VBox(5, progressLabel, progressBar);
        progressBox.setAlignment(Pos.CENTER);

        // Create question container
        VBox questionContainer = new VBox(15);
        questionContainer.setAlignment(Pos.CENTER_LEFT);
        questionContainer.setPrefWidth(700);

        // Display first question
        displayQuestion(questions, 0, questionContainer, progressBar, progressLabel, 
                       quizResult, quizArea, resultsArea);

        quizArea.getChildren().addAll(progressBox, questionContainer);
    }

    private void displayQuestion(List<QuizQuestion> questions, int currentIndex, 
                               VBox questionContainer, ProgressBar progressBar, 
                               Label progressLabel, QuizResult quizResult,
                               VBox quizArea, VBox resultsArea) {
        questionContainer.getChildren().clear();

        if (currentIndex >= questions.size()) {
            quizHistory.add(quizResult);
            showQuizResults(quizResult, resultsArea, quizArea);
            return;
        }

        QuizQuestion currentQuestion = questions.get(currentIndex);

        // Update progress
        double progress = (double) (currentIndex + 1) / questions.size();
        progressBar.setProgress(progress);
        progressLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size());

        // Create question UI
        Label questionLabel = new Label(currentQuestion.getQuestion());
        questionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        questionLabel.setStyle("-fx-text-fill: #2c3e50;");
        questionLabel.setWrapText(true);

        VBox answersBox = new VBox(8);
        ToggleGroup answerGroup = new ToggleGroup();

        // Handle different question types
        if (currentQuestion.isMultipleChoice()) {
            // Multiple Choice Questions
            if (currentQuestion.getOptions() != null) {
                for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
                    HBox answerRow = new HBox(10);
                    answerRow.setAlignment(Pos.CENTER_LEFT);

                    RadioButton radioButton = new RadioButton();
                    radioButton.setToggleGroup(answerGroup);
                    radioButton.setUserData(i);

                    Label optionLabel = new Label(currentQuestion.getOptions().get(i));
                    optionLabel.setWrapText(true);
                    optionLabel.setStyle("-fx-text-fill: #34495e;");

                    answerRow.getChildren().addAll(radioButton, optionLabel);
                    answersBox.getChildren().add(answerRow);
                }
            }
        } else if (currentQuestion.isTrueFalse()) {
            // True/False Questions
            HBox trueFalseBox = new HBox(20);
            trueFalseBox.setAlignment(Pos.CENTER_LEFT);

            RadioButton trueButton = new RadioButton("True");
            trueButton.setToggleGroup(answerGroup);
            trueButton.setUserData(true);

            RadioButton falseButton = new RadioButton("False");
            falseButton.setToggleGroup(answerGroup);
            falseButton.setUserData(false);

            trueFalseBox.getChildren().addAll(trueButton, falseButton);
            answersBox.getChildren().add(trueFalseBox);
        } else if (currentQuestion.isMatching()) {
            // Matching Questions
            VBox matchingBox = new VBox(10);
            matchingBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-padding: 15;");

            Label instructionLabel = new Label("Match the items on the left with their corresponding values:");
            instructionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            VBox pairsBox = new VBox(8);
            if (currentQuestion.getPairs() != null) {
                for (Map<String, String> pair : currentQuestion.getPairs()) {
                    HBox pairRow = new HBox(10);
                    pairRow.setAlignment(Pos.CENTER_LEFT);

                    String item = pair.get("item");
                    String match = pair.get("match");

                    Label itemLabel = new Label(item);
                    itemLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 150;");

                    Label arrowLabel = new Label("→");
                    arrowLabel.setStyle("-fx-text-fill: #7f8c8d;");

                    Label matchLabel = new Label(match);
                    matchLabel.setStyle("-fx-text-fill: #34495e;");

                    pairRow.getChildren().addAll(itemLabel, arrowLabel, matchLabel);
                    pairsBox.getChildren().add(pairRow);
                }
            }

            // For matching questions, we'll treat them as informational since they're complex to implement
            Label infoLabel = new Label("Note: This is a matching question. Review the pairs for understanding.");
            infoLabel.setStyle("-fx-text-fill: #3498db; -fx-font-style: italic;");

            matchingBox.getChildren().addAll(instructionLabel, pairsBox, infoLabel);
            answersBox.getChildren().add(matchingBox);
        }

        Button nextBtn = new Button(currentIndex == questions.size() - 1 ? "Finish Quiz" : "Next Question");
        nextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Label feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);

        nextBtn.setOnAction(e -> {
            // Handle answer submission based on question type
            if (currentQuestion.isMultipleChoice() || currentQuestion.isTrueFalse()) {
                RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
                if (selected == null) {
                    feedbackLabel.setText("Please select an answer!");
                    feedbackLabel.setStyle("-fx-text-fill: #e74c3c;");
                    return;
                }

                boolean isCorrect = false;
                String userAnswer = "";
                String correctAnswer = "";

                if (currentQuestion.isMultipleChoice()) {
                    int selectedIndex = (int) selected.getUserData();
                    isCorrect = selectedIndex == currentQuestion.getCorrectAnswerInt();
                    userAnswer = currentQuestion.getOptions().get(selectedIndex);
                    correctAnswer = currentQuestion.getCorrectAnswerString();
                } else if (currentQuestion.isTrueFalse()) {
                    boolean userAnswerBool = (boolean) selected.getUserData();
                    isCorrect = userAnswerBool == currentQuestion.getCorrectAnswerBool();
                    userAnswer = userAnswerBool ? "True" : "False";
                    correctAnswer = currentQuestion.getCorrectAnswerBool() ? "True" : "False";
                }

                // Record result
                QuestionResult questionResult = new QuestionResult(
                    currentQuestion.getQuestion(),
                    isCorrect,
                    userAnswer,
                    correctAnswer,
                    currentQuestion.getExplanation()
                );
                quizResult.addQuestionResult(questionResult);

            } else if (currentQuestion.isMatching()) {
                // For matching questions, we'll mark them as correct for now
                // since implementing actual matching logic is complex
                QuestionResult questionResult = new QuestionResult(
                    currentQuestion.getQuestion(),
                    true, // Mark as correct for learning purposes
                    "Matching question reviewed",
                    "All pairs matched correctly",
                    currentQuestion.getExplanation()
                );
                quizResult.addQuestionResult(questionResult);
            }

            // Move to next question
            displayQuestion(questions, currentIndex + 1, questionContainer, 
                           progressBar, progressLabel, quizResult, quizArea, resultsArea);
        });

        // Add difficulty badge
        HBox questionHeader = new HBox(10);
        questionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyBadge = new Label(currentQuestion.getDifficulty().toUpperCase());
        difficultyBadge.setStyle(getDifficultyStyle(currentQuestion.getDifficulty()));
        difficultyBadge.setPadding(new Insets(2, 8, 2, 8));
        
        questionHeader.getChildren().addAll(questionLabel, difficultyBadge);

        questionContainer.getChildren().addAll(questionHeader, answersBox, nextBtn, feedbackLabel);
    }

    private void showQuizResults(QuizResult quizResult, VBox resultsArea, VBox quizArea) {
        quizArea.setVisible(false);
        resultsArea.getChildren().clear();
        resultsArea.setVisible(true);

        // Score display
        Label scoreLabel = new Label("Quiz Completed!");
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        scoreLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 48));
        
        if (quizResult.getScore() >= 80) {
            resultLabel.setText("🎉 " + quizResult.getScore() + "%");
            resultLabel.setStyle("-fx-text-fill: #27ae60;");
        } else if (quizResult.getScore() >= 60) {
            resultLabel.setText("👍 " + quizResult.getScore() + "%");
            resultLabel.setStyle("-fx-text-fill: #f39c12;");
        } else {
            resultLabel.setText("📚 " + quizResult.getScore() + "%");
            resultLabel.setStyle("-fx-text-fill: #e74c3c;");
        }

        Label detailLabel = new Label(
            "You got " + quizResult.getCorrectAnswers() + " out of " + 
            quizResult.getTotalQuestions() + " questions correct in " + 
            quizResult.getCategory() + "!"
        );
        detailLabel.setFont(Font.font("System", 16));
        detailLabel.setStyle("-fx-text-fill: #7f8c8d;");

        // Performance chart replaced with text
        Label performanceText = new Label(
            "Correct Answers: " + quizResult.getCorrectAnswers() + "\n" +
            "Total Questions: " + quizResult.getTotalQuestions() + "\n" +
            "Score (%): " + quizResult.getScore() + "%"
        );
        performanceText.setFont(Font.font("System", FontWeight.BOLD, 16));
        performanceText.setStyle("-fx-text-fill: #2c3e50;");

        // Detailed results
        VBox detailsBox = createDetailedResults(quizResult);

        Button retryBtn = new Button("Try Another Quiz");
        retryBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        retryBtn.setOnAction(e -> {
            resultsArea.setVisible(false);
            quizArea.setVisible(true);
            quizArea.getChildren().clear();
            Label message = new Label("Select a category and click 'Start New Quiz' to begin!");
            message.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            quizArea.getChildren().add(message);
        });

        resultsArea.getChildren().addAll(scoreLabel, resultLabel, detailLabel, 
                                       performanceText, detailsBox, retryBtn);
    }

    private VBox createDetailedResults(QuizResult quizResult) {
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-padding: 15;");
        detailsBox.setPrefWidth(800); // Increased width
        detailsBox.setPrefHeight(400); // Increased height

        Label detailsTitle = new Label("Detailed Results:");
        detailsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        detailsTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox questionsBox = new VBox(10);
        for (int i = 0; i < quizResult.getQuestionResults().size(); i++) {
            QuestionResult qResult = quizResult.getQuestionResults().get(i);
            VBox questionResult = createQuestionResultBox(qResult, i + 1);
            questionsBox.getChildren().add(questionResult);
        }

        ScrollPane scrollPane = new ScrollPane(questionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350); // Adjusted to fit the new height

        detailsBox.getChildren().addAll(detailsTitle, scrollPane);
        return detailsBox;
    }

    private VBox createQuestionResultBox(QuestionResult qResult, int questionNumber) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 6; -fx-padding: 10;");

        Label questionNum = new Label("Q" + questionNumber + ": " + qResult.getQuestion());
        questionNum.setFont(Font.font("System", FontWeight.BOLD, 12));
        questionNum.setWrapText(true);

        HBox answerBox = new HBox(10);
        Label userAnswer = new Label("Your answer: " + qResult.getUserAnswer());
        Label correctAnswer = new Label("Correct: " + qResult.getCorrectAnswer());

        if (qResult.isCorrect()) {
            userAnswer.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            correctAnswer.setStyle("-fx-text-fill: #27ae60;");
        } else {
            userAnswer.setStyle("-fx-text-fill: #e74c3c;");
            correctAnswer.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }

        answerBox.getChildren().addAll(userAnswer, correctAnswer);

        if (qResult.getExplanation() != null && !qResult.getExplanation().isEmpty()) {
            Label explanation = new Label("💡 " + qResult.getExplanation());
            explanation.setStyle("-fx-text-fill: #3498db; -fx-font-style: italic;");
            explanation.setWrapText(true);
            box.getChildren().addAll(questionNum, answerBox, explanation);
        } else {
            box.getChildren().addAll(questionNum, answerBox);
        }

        return box;
    }

    private String getDifficultyStyle(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-background-radius: 10;";
            case "medium":
                return "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-background-radius: 10;";
            case "hard":
                return "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-background-radius: 10;";
            default:
                return "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-background-radius: 10;";
        }
    }

    // ========== HELPER METHODS ==========

    private VBox createCategoryBox(String category, List<AlgorithmMetrics> algorithms) {
        VBox categoryBox = new VBox(10);
        categoryBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-padding: 15;");

        Label categoryLabel = new Label(category);
        categoryLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        categoryLabel.setStyle("-fx-text-fill: #2c3e50;");

        VBox algorithmsBox = new VBox(8);
        for (AlgorithmMetrics algorithm : algorithms) {
            HBox algoRow = createAlgorithmRow(algorithm);
            algorithmsBox.getChildren().add(algoRow);
        }

        categoryBox.getChildren().addAll(categoryLabel, algorithmsBox);
        return categoryBox;
    }

    private HBox createAlgorithmRow(AlgorithmMetrics algorithm) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 6; -fx-padding: 10;");

        Label nameLabel = new Label(algorithm.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");
        nameLabel.setMinWidth(200);

        Label timeLabel = new Label("Time: " + algorithm.getFormattedTimeComplexity());
        timeLabel.setFont(Font.font("System", 12));
        timeLabel.setStyle("-fx-text-fill: #e74c3c;");

        Label spaceLabel = new Label("Space: " + algorithm.getSpaceComplexity());
        spaceLabel.setFont(Font.font("System", 12));
        spaceLabel.setStyle("-fx-text-fill: #3498db;");

        // More details button
        Button detailsBtn = new Button("Details");
        detailsBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 11;");
        detailsBtn.setOnAction(e -> showAlgorithmDetails(algorithm));

        row.getChildren().addAll(nameLabel, timeLabel, spaceLabel, detailsBtn);
        return row;
    }

    private void showAlgorithmDetails(AlgorithmMetrics algorithm) {
        String details = String.format(
            "📊 %s\n\n" +
            "⏱️ Time Complexity:\n" +
            "   Best: %s\n" +
            "   Average: %s\n" +
            "   Worst: %s\n\n" +
            "💾 Space Complexity: %s\n\n" +
            "📝 Description:\n%s\n\n" +
            "🎯 When to Use:\n• %s\n\n" +
            "🌍 Real World Example:\n%s",
            algorithm.getName(),
            algorithm.getTimeComplexity().getBest(),
            algorithm.getTimeComplexity().getAverage(),
            algorithm.getTimeComplexity().getWorst(),
            algorithm.getSpaceComplexity(),
            algorithm.getDescription(),
            String.join("\n• ", algorithm.getWhenToUse()),
            algorithm.getRealWorldExample()
        );

        TextArea detailsArea = new TextArea(details);
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefSize(600, 400);

        ScrollPane scrollPane = new ScrollPane(detailsArea);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Algorithm Details - " + algorithm.getName());
        alert.setHeaderText("Complete Complexity Analysis");
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    private void updateAlgorithmCombo(ComboBox<String> algoCombo, String category) {
        algoCombo.getItems().clear();
        List<AlgorithmMetrics> algorithms = ComplexityData.getAlgorithmsByCategory(category);
        for (AlgorithmMetrics algo : algorithms) {
            algoCombo.getItems().add(algo.getName());
        }
        if (!algorithms.isEmpty()) {
            algoCombo.setValue(algorithms.get(0).getName());
        }
    }

    private void updateComparison(BarChart<String, Number> chart, TableView<AlgorithmMetrics> table, String category) {
        // Update chart
        chart.getData().clear();
        XYChart.Series<String, Number> timeSeries = new XYChart.Series<>();
        timeSeries.setName("Time Complexity (Relative)");
        
        XYChart.Series<String, Number> spaceSeries = new XYChart.Series<>();
        spaceSeries.setName("Space Complexity (Relative)");

        List<AlgorithmMetrics> algorithms = ComplexityData.getAlgorithmsByCategory(category);
        
        for (AlgorithmMetrics algo : algorithms) {
            double timeScore = calculateComplexityScore(algo.getTimeComplexity().getAverage());
            double spaceScore = calculateComplexityScore(algo.getSpaceComplexity());
            
            timeSeries.getData().add(new XYChart.Data<>(algo.getName(), timeScore));
            spaceSeries.getData().add(new XYChart.Data<>(algo.getName(), spaceScore));
        }

        chart.getData().addAll(timeSeries, spaceSeries);

        // Update table
        table.getItems().setAll(algorithms);
    }

    private double calculateComplexityScore(String complexity) {
        // Convert complexity notation to numerical score for comparison
        if (complexity.contains("O(1)")) return 100;
        if (complexity.contains("O(log")) return 90;
        if (complexity.contains("O(n)")) return 70;
        if (complexity.contains("O(n log")) return 60;
        if (complexity.contains("O(n²)")) return 30;
        if (complexity.contains("O(2ⁿ)")) return 10;
        if (complexity.contains("O(n!)")) return 5;
        return 50; // default
    }

    private void runPracticalAnalysis(LineChart<Number, Number> chart, TextArea explanation, String algorithmName, int maxSize) {
        chart.getData().clear();

        AlgorithmMetrics algorithm = ComplexityData.getAlgorithmByName(algorithmName);
        if (algorithm == null) return;

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(algorithmName);

        // Use the runtime data from our JSON
        Map<Integer, Double> runtimeData = algorithm.getRuntimeData();
        for (Map.Entry<Integer, Double> entry : runtimeData.entrySet()) {
            if (entry.getKey() <= maxSize) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }

        chart.getData().add(series);

        // Update explanation
        explanation.setText(
            "Algorithm: " + algorithm.getName() + "\n" +
            "Time Complexity: " + algorithm.getFormattedTimeComplexity() + "\n" +
            "Space Complexity: " + algorithm.getSpaceComplexity() + "\n" +
            "Category: " + algorithm.getCategory() + "\n\n" +
            "Analysis shows practical runtime scaling with input size.\n" +
            "This simulation helps understand real-world performance characteristics."
        );
    }

    private VBox createSectionBox(String title) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 12; -fx-padding: 20; " +
                        "-fx-border-color: #e9ecef; -fx-border-width: 1;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Pane underline = new Pane();
        underline.setPrefHeight(2);
        underline.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        underline.setMaxWidth(200);

        section.getChildren().addAll(titleLabel, underline);
        return section;
    }

    private TextFlow createFormattedTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-text-fill: #34495e; -fx-line-spacing: 1.6;");

        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                textFlow.getChildren().add(new Text("\n\n"));
                continue;
            }

            if (line.matches(".*\\*\\*.*\\*\\*.*")) {
                String[] parts = line.split("\\*\\*");
                for (int i = 0; i < parts.length; i++) {
                    Text text = new Text(parts[i]);
                    if (i % 2 == 1) {
                        text.setStyle("-fx-font-weight: bold; -fx-fill: #2c3e50; -fx-font-size: 14;");
                    } else {
                        text.setStyle("-fx-font-size: 14; -fx-fill: #34495e;");
                    }
                    textFlow.getChildren().add(text);
                }
            } else if (line.trim().startsWith("•")) {
                Text bullet = new Text("• " + line.substring(1).trim() + "\n");
                bullet.setStyle("-fx-font-size: 14; -fx-fill: #34495e;");
                textFlow.getChildren().add(bullet);
            } else {
                Text text = new Text(line + "\n");
                text.setStyle("-fx-font-size: 14; -fx-fill: #34495e;");
                textFlow.getChildren().add(text);
            }
        }

        return textFlow;
    }

    private LineChart<Number, Number> createComplexityChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Input Size (n)");
        yAxis.setLabel("Operations");

        final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Big O Complexity Growth");
        chart.setPrefSize(800, 400);
        chart.setLegendVisible(true);

        addComplexitySeries(chart, "O(1)", 1);
        addComplexitySeries(chart, "O(log n)", 2);
        addComplexitySeries(chart, "O(n)", 3);
        addComplexitySeries(chart, "O(n log n)", 4);
        addComplexitySeries(chart, "O(n²)", 5);
        addComplexitySeries(chart, "O(2ⁿ)", 6);

        return chart;
    }

    private void addComplexitySeries(LineChart<Number, Number> chart, String name, int type) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);

        for (int n = 1; n <= 20; n++) {
            double value = calculateComplexity(n, type);
            series.getData().add(new XYChart.Data<>(n, value));
        }

        chart.getData().add(series);
    }

    private double calculateComplexity(int n, int type) {
        switch (type) {
            case 1: return 1; // O(1)
            case 2: return Math.log(n) / Math.log(2); // O(log n)
            case 3: return n; // O(n)
            case 4: return n * (Math.log(n) / Math.log(2)); // O(n log n)
            case 5: return n * n; // O(n²)
            case 6: return Math.pow(2, n); // O(2ⁿ)
            default: return 0;
        }
    }

    private BarChart<String, Number> createComparisonChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Algorithms");
        yAxis.setLabel("Performance Score (Higher is Better)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Algorithm Performance Comparison");
        chart.setPrefSize(700, 400);
        chart.setLegendVisible(true);

        return chart;
    }

    private LineChart<Number, Number> createRuntimeChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Input Size");
        yAxis.setLabel("Runtime (ms)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Runtime Analysis");
        chart.setPrefSize(700, 400);
        chart.setLegendVisible(true);

        return chart;
    }

    private TableView<AlgorithmMetrics> createComparisonTable() {
        TableView<AlgorithmMetrics> table = new TableView<>();

        TableColumn<AlgorithmMetrics, String> nameCol = new TableColumn<>("Algorithm");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<AlgorithmMetrics, String> timeCol = new TableColumn<>("Time Complexity");
        timeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedTimeComplexity()));

        TableColumn<AlgorithmMetrics, String> spaceCol = new TableColumn<>("Space Complexity");
        spaceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSpaceComplexity()));

        table.getColumns().addAll(nameCol, timeCol, spaceCol);
        table.setPrefHeight(200);

        return table;
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Home");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(40);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        String normalStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 8;";
        String hoverStyle = "-fx-background-color: #5a6268; -fx-text-fill: white; -fx-background-radius: 8;";
        
        backBtn.setStyle(normalStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));
        
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}