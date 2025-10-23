package com.dsa.ui;

import com.dsa.complexity.data.ComplexityData;
import com.dsa.complexity.models.AlgorithmMetrics;
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
        quizCategoryCombo.getItems().addAll("All Categories", "Sorting", "Searching", "Graph", "Greedy", "Dynamic Programming");
        quizCategoryCombo.setValue("All Categories");

        Button startQuizBtn = new Button("Start Quiz");
        startQuizBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        categoryBox.getChildren().addAll(categoryLabel, quizCategoryCombo, startQuizBtn);

        // Quiz area
        VBox quizArea = new VBox(20);
        quizArea.setAlignment(Pos.CENTER);
        quizArea.setPadding(new Insets(20));
        quizArea.setStyle("-fx-background-color: white; -fx-border-radius: 10;");

        // Sample quiz question - in a real implementation, this would be dynamic
        VBox sampleQuestion = createSampleQuizQuestion();
        quizArea.getChildren().add(sampleQuestion);

        startQuizBtn.setOnAction(e -> {
            // In a full implementation, this would load questions based on category
            showAlert("Quiz Started", "Starting " + quizCategoryCombo.getValue() + " quiz!\n\nThis would load dynamic questions in a full implementation.");
        });

        content.getChildren().addAll(title, categoryBox, quizArea);
        tab.setContent(content);

        return tab;
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

    private VBox createSampleQuizQuestion() {
        VBox questionBox = new VBox(10);
        questionBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-padding: 15;");

        Label questionLabel = new Label("What is the average time complexity of Quick Sort?");
        questionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        questionLabel.setStyle("-fx-text-fill: #2c3e50;");

        ToggleGroup answerGroup = new ToggleGroup();
        VBox answersBox = new VBox(8);
        
        RadioButton answer1 = new RadioButton("O(n²)");
        RadioButton answer2 = new RadioButton("O(n log n)");
        RadioButton answer3 = new RadioButton("O(n)");
        RadioButton answer4 = new RadioButton("O(1)");
        
        answer1.setToggleGroup(answerGroup);
        answer2.setToggleGroup(answerGroup);
        answer3.setToggleGroup(answerGroup);
        answer4.setToggleGroup(answerGroup);
        
        answer2.setSelected(true); // Correct answer

        answersBox.getChildren().addAll(answer1, answer2, answer3, answer4);

        Button submitBtn = new Button("Submit Answer");
        submitBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        submitBtn.setOnAction(e -> {
            if (answer2.isSelected()) {
                resultLabel.setText("✅ Correct! Quick Sort has O(n log n) average time complexity.");
                resultLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                resultLabel.setText("❌ Incorrect. The correct answer is O(n log n).");
                resultLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        questionBox.getChildren().addAll(questionLabel, answersBox, submitBtn, resultLabel);
        return questionBox;
    }

    // ========== UI COMPONENT CREATION METHODS ==========
    // (Keep all the existing createSectionBox, createFormattedTextFlow, 
    // createComplexityChart, createComparisonChart, createRuntimeChart,
    // createComparisonTable, createBackButton, showAlert methods from previous version)
    // These remain the same as in our previous implementation

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