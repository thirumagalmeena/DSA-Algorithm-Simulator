package com.dsa.ui;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AlgorithmDetailPage {

    private final Stage stage;
    private final ViewAlgorithmsPage.AlgorithmData algorithm;
    private final MongoCollection<Document> collection;

    public AlgorithmDetailPage(Stage stage, ViewAlgorithmsPage.AlgorithmData algorithm, MongoCollection<Document> collection) {
        this.stage = stage;
        this.algorithm = algorithm;
        this.collection = collection;
    }

    public void show() {
        // Main container with beautiful gradient
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Header section with improved styling
        VBox headerBox = createHeaderBox();
        
        // Load full algorithm data from MongoDB
        Document fullAlgorithm = collection.find(Filters.eq("algorithm.id", algorithm.getId())).first();

        // Create elegant tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0.3, 0, 5);");

        if (fullAlgorithm != null) {
            // Create all tabs with enhanced styling
            Tab overviewTab = createOverviewTab(fullAlgorithm);
            Tab learningTab = createLearningTab(fullAlgorithm);
            Tab codeTab = createCodeTab(fullAlgorithm);
            Tab propertiesTab = createPropertiesTab(fullAlgorithm);
            Tab visualizationTab = createVisualizationTab(fullAlgorithm); // New visualization tab

            tabPane.getTabs().addAll(overviewTab, learningTab, codeTab, propertiesTab, visualizationTab);
        } else {
            Tab errorTab = createErrorTab();
            tabPane.getTabs().add(errorTab);
        }

        // Enhanced back button
        Button backBtn = createBackButton();

        VBox contentBox = new VBox(25, headerBox, tabPane, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(1100);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 1100, 850);
        stage.setScene(scene);
        stage.setTitle(algorithm.getName() + " - DSA Simulator");
    }

    private VBox createHeaderBox() {
        Label title = new Label(algorithm.getName());
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 36));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.5, 3, 3);");

        Label category = new Label(algorithm.getCategory());
        category.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        category.setStyle("-fx-text-fill: #e8f4f8;");

        // Add decorative element
        HBox tagsBox = new HBox(10);
        tagsBox.setAlignment(Pos.CENTER);
        for (String tag : algorithm.getTags()) {
            Label tagLabel = createTagLabel(tag);
            tagsBox.getChildren().add(tagLabel);
        }

        VBox headerBox = new VBox(8, title, category, tagsBox);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        
        return headerBox;
    }

    private Tab createOverviewTab(Document fullAlgorithm) {
        Tab tab = new Tab("📖 Overview");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = createContentBox();

        Document algorithmDoc = fullAlgorithm.get("algorithm", Document.class);
        Document metadata = fullAlgorithm.get("metadata", Document.class);

        // Description section
        VBox descSection = createSectionBox("Description");
        TextFlow descFlow = createFormattedTextFlow(algorithmDoc.getString("description"));
        descSection.getChildren().add(descFlow);

        // Metadata section
        VBox metaSection = createSectionBox("Algorithm Details");
        VBox metaBox = createMetadataBox(metadata);
        metaSection.getChildren().add(metaBox);

        content.getChildren().addAll(descSection, metaSection);
        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createLearningTab(Document fullAlgorithm) {
        Tab tab = new Tab("🎓 Learning Content");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = createContentBox();

        List<Document> sections = fullAlgorithm.get("sections", List.class);

        if (sections != null) {
            for (Document section : sections) {
                String sectionLabel = section.getString("label");
                String sectionContent = section.getString("content");

                VBox sectionBox = createSectionBox(sectionLabel.trim());
                
                // Handle mermaid diagrams
                String mermaidCode = extractMermaidCode(sectionContent);
                if (mermaidCode != null) {
                    Node mermaidWebView = createMermaidWebView(mermaidCode);
                    VBox diagramBox = createDiagramBox(mermaidWebView);
                    sectionBox.getChildren().add(diagramBox);
                    
                    // Remove mermaid code from text content
                    sectionContent = sectionContent.replaceAll("```mermaid\\s*.*?\\s*```", "").trim();
                }

                // Add formatted text content
                TextFlow contentFlow = createFormattedTextFlow(sectionContent);
                sectionBox.getChildren().add(contentFlow);
                content.getChildren().add(sectionBox);
            }
        }

        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createCodeTab(Document fullAlgorithm) {
        Tab tab = new Tab("💻 Code Examples");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = createContentBox();

        Document codeExamples = fullAlgorithm.get("code_examples", Document.class);

        if (codeExamples != null) {
            String[] languages = {"python", "java", "javascript", "cpp", "c"};
            String[] languageNames = {"🐍 Python", "☕ Java", "🌐 JavaScript", "⚡ C++", "🔧 C"};
            String[] languageColors = {"#28a745", "#dc3545", "#f7df1e", "#00599c", "#555555"};

            for (int i = 0; i < languages.length; i++) {
                String language = languages[i];
                String languageName = languageNames[i];
                String languageColor = languageColors[i];
                
                if (codeExamples.containsKey(language)) {
                    String code = codeExamples.getString(language);
                    VBox codeBox = createCodeBox(languageName, code, languageColor);
                    content.getChildren().add(codeBox);
                }
            }
        } else {
            Label noCode = new Label("No code examples available.");
            noCode.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 14;");
            content.getChildren().add(noCode);
        }

        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createPropertiesTab(Document fullAlgorithm) {
        Tab tab = new Tab("📊 Properties");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = createContentBox();

        List<Document> sections = fullAlgorithm.get("sections", List.class);
        String propertiesContent = "";

        if (sections != null) {
            for (Document section : sections) {
                if ("properties".equals(section.getString("id"))) {
                    propertiesContent = section.getString("content");
                    break;
                }
            }
        }

        if (!propertiesContent.isEmpty()) {
            VBox propertiesBox = createSectionBox("Algorithm Properties");
            TextFlow propertiesFlow = createFormattedTextFlow(propertiesContent);
            propertiesBox.getChildren().add(propertiesFlow);
            content.getChildren().add(propertiesBox);
        } else {
            Label noProperties = new Label("No properties information available.");
            noProperties.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 14;");
            content.getChildren().add(noProperties);
        }

        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createVisualizationTab(Document fullAlgorithm) {
        Tab tab = new Tab("🎮 Visualization");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = createContentBox();

        // Check if visualization is available
        boolean hasVisualization = checkVisualizationAvailability(fullAlgorithm);
        
        if (hasVisualization) {
            VBox vizSection = createSectionBox("Interactive Visualization");
            
            Label title = new Label("Run " + algorithm.getName() + " Visualization");
            title.setFont(Font.font("System", FontWeight.BOLD, 20));
            title.setStyle("-fx-text-fill: #2c3e50;");
            
            Label description = new Label("Experience an interactive simulation to see how " + 
                algorithm.getName() + " works step by step with real-time animation and visual feedback.");
            description.setFont(Font.font("System", 14));
            description.setStyle("-fx-text-fill: #7f8c8d;");
            description.setWrapText(true);
            description.setMaxWidth(700);

            // Features list
            VBox featuresBox = new VBox(10);
            featuresBox.setPadding(new Insets(15, 0, 15, 0));
            
            String[] features = {
                "• Real-time algorithm execution",
                "• Step-by-step animation",
                "• Interactive controls (play, pause, reset)",
                "• Visual comparison and swap highlighting",
                "• Performance metrics tracking",
                "• Custom input values"
            };
            
            for (String feature : features) {
                Label featureLabel = new Label(feature);
                featureLabel.setFont(Font.font("System", 13));
                featureLabel.setStyle("-fx-text-fill: #495057;");
                featuresBox.getChildren().add(featureLabel);
            }

            Button runVizButton = createVisualizationButton();
            
            VBox centerBox = new VBox(20, title, description, featuresBox, runVizButton);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setPadding(new Insets(20));
            
            vizSection.getChildren().add(centerBox);
            content.getChildren().add(vizSection);
        } else {
            VBox noVizSection = createSectionBox("Visualization");
            
            Label noVizLabel = new Label("Visualization not available for " + algorithm.getName());
            noVizLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noVizLabel.setStyle("-fx-text-fill: #e74c3c;");
            
            Label suggestionLabel = new Label("Check back later or explore other algorithms with available visualizations.");
            suggestionLabel.setFont(Font.font("System", 14));
            suggestionLabel.setStyle("-fx-text-fill: #7f8c8d;");
            suggestionLabel.setWrapText(true);
            suggestionLabel.setMaxWidth(500);
            
            Button browseVizButton = new Button("Browse Available Visualizations");
            browseVizButton.setPrefWidth(250);
            browseVizButton.setPrefHeight(40);
            browseVizButton.setFont(Font.font("System", FontWeight.BOLD, 14));
            browseVizButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            browseVizButton.setOnMouseEntered(e -> browseVizButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
            browseVizButton.setOnMouseExited(e -> browseVizButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));
            
            browseVizButton.setOnAction(e -> {
                ViewVisualizationPage vizPage = new ViewVisualizationPage(stage);
                vizPage.show();
            });
            
            VBox centerBox = new VBox(20, noVizLabel, suggestionLabel, browseVizButton);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setPadding(new Insets(30));
            
            noVizSection.getChildren().add(centerBox);
            content.getChildren().add(noVizSection);
        }

        scrollPane.setContent(content);
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createErrorTab() {
        Tab tab = new Tab("❌ Error");
        tab.setStyle("-fx-font-weight: bold;");

        VBox errorBox = new VBox(20);
        errorBox.setPadding(new Insets(30));
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-background-color: #f8d7da; -fx-border-radius: 10;");

        Label errorIcon = new Label("⚠️");
        errorIcon.setStyle("-fx-font-size: 48;");

        Label errorLabel = new Label("Algorithm details not found in database.");
        errorLabel.setStyle("-fx-text-fill: #721c24; -fx-font-size: 16; -fx-font-weight: bold;");

        errorBox.getChildren().addAll(errorIcon, errorLabel);
        tab.setContent(errorBox);

        return tab;
    }

    // Visualization-related methods
    private boolean checkVisualizationAvailability(Document fullAlgorithm) {
        Document visualization = fullAlgorithm.get("visualization", Document.class);
        if (visualization != null && visualization.getBoolean("available", false)) {
            return true;
        }
        
        // Fallback: Check if visualization class exists
        return getVisualizationClassName() != null;
    }

    private Button createVisualizationButton() {
        Button runVizButton = new Button("🚀 Launch Visualization");
        runVizButton.setPrefWidth(250);
        runVizButton.setPrefHeight(50);
        runVizButton.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        String normalStyle = "-fx-background-color: linear-gradient(to right, #27ae60, #2ecc71); " +
                           "-fx-text-fill: white; -fx-background-radius: 10; " +
                           "-fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 3, 3);";
        
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #229954, #27ae60); " +
                          "-fx-text-fill: white; -fx-background-radius: 10; " +
                          "-fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0.6, 4, 4);";
        
        runVizButton.setStyle(normalStyle);
        runVizButton.setOnMouseEntered(e -> runVizButton.setStyle(hoverStyle));
        runVizButton.setOnMouseExited(e -> runVizButton.setStyle(normalStyle));
        
        runVizButton.setOnAction(e -> launchAlgorithmVisualization());
        
        return runVizButton;
    }

    private void launchAlgorithmVisualization() {
        try {
            String className = getVisualizationClassName();
            if (className != null) {
                Class<?> vizClass = Class.forName(className);
                javafx.application.Application vizApp = (javafx.application.Application) vizClass.getDeclaredConstructor().newInstance();
                
                Stage vizStage = new Stage();
                vizStage.setTitle(algorithm.getName() + " - DSA Visualizer");
                vizApp.start(vizStage);
            } else {
                showVisualizationError("No visualization class found for " + algorithm.getName());
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to launch visualization: " + e.getMessage());
            e.printStackTrace();
            showVisualizationError("Failed to launch visualization: " + e.getMessage());
        }
    }

    private String getVisualizationClassName() {
        System.out.println("🔍 Looking for visualization for: " + algorithm.getName() + " (ID: " + algorithm.getId() + ")");
        
        String algorithmName = algorithm.getName().toLowerCase();
        String algorithmId = algorithm.getId().toLowerCase();
        
        // ==================== SORTING ALGORITHMS ====================
        if (algorithmName.contains("bubble") || algorithmId.contains("bubble")) {
            return checkAndReturnClass("com.dsa.simulator.sorting.BubbleSortVisualizer");
        } else if (algorithmName.contains("quick") || algorithmId.contains("quick")) {
            return checkAndReturnClass("com.dsa.simulator.sorting.QuickSortVisualizer");
        } else if (algorithmName.contains("merge") || algorithmId.contains("merge")) {
            return checkAndReturnClass("com.dsa.simulator.sorting.MergeSortVisualizer");
        } else if (algorithmName.contains("insertion") || algorithmId.contains("insertion")) {
            return checkAndReturnClass("com.dsa.simulator.sorting.InsertionSortVisualizer");
        } else if (algorithmName.contains("selection") || algorithmId.contains("selection")) {
            return checkAndReturnClass("com.dsa.simulator.sorting.SelectionSortVisualizer");
        }
        
        // ==================== SEARCHING ALGORITHMS ====================
        else if (algorithmName.contains("binary") || algorithmId.contains("binary")) {
            return checkAndReturnClass("com.dsa.simulator.searching.BinarySearchVisualizer");
        } else if (algorithmName.contains("linear") || algorithmId.contains("linear")) {
            return checkAndReturnClass("com.dsa.simulator.searching.LinearSearchVisualizer");
        }
        
        // ==================== GRAPH ALGORITHMS ====================
        else if (algorithmName.contains("dijkstra") || algorithmId.contains("dijkstra")) {
            return checkAndReturnClass("com.dsa.simulator.graphTraversal.DijkstraVisualizer");
        } else if (algorithmName.contains("topological") || algorithmId.contains("topological")) {
            return checkAndReturnClass("com.dsa.simulator.graphTraversal.TopologicalOrderingVisualizer");
        }
        
        // ==================== GREEDY ALGORITHMS ====================
        else if (algorithmName.contains("job") && algorithmName.contains("scheduling") || 
                 algorithmId.contains("job") && algorithmId.contains("scheduling")) {
            return checkAndReturnClass("com.dsa.simulator.greedy.JobSchedulingVisualizer");
        } else if (algorithmName.contains("kruskal") || algorithmId.contains("kruskal")) {
            return checkAndReturnClass("com.dsa.simulator.greedy.KruskalVisualizer");
        } else if (algorithmName.contains("prim") || algorithmId.contains("prim")) {
            return checkAndReturnClass("com.dsa.simulator.greedy.PrimsVisualizer");
        }
        
        // ==================== DYNAMIC PROGRAMMING ====================
        else if ((algorithmName.contains("coin") && algorithmName.contains("change")) || 
                 (algorithmId.contains("coin") && algorithmId.contains("change"))) {
            return checkAndReturnClass("com.dsa.simulator.dynamicProgramming.CoinChangeVisualizer");
        } else if (algorithmName.contains("fibonacci") || algorithmId.contains("fibonacci")) {
            return checkAndReturnClass("com.dsa.simulator.dynamicProgramming.FibonacciVisualizer");
        } else if (algorithmName.contains("knapsack") || algorithmId.contains("knapsack")) {
            return checkAndReturnClass("com.dsa.simulator.dynamicProgramming.Knapsack01Visualizer");
        } else if ((algorithmName.contains("longest") && algorithmName.contains("common") && algorithmName.contains("subsequence")) || 
                   (algorithmId.contains("lcs"))) {
            return checkAndReturnClass("com.dsa.simulator.dynamicProgramming.LongestCommonSubsequenceVisualizer");
        } else if (algorithmName.contains("pascal") || algorithmId.contains("pascal")) {
            return checkAndReturnClass("com.dsa.simulator.dynamicProgramming.PascalTriangleVisualizer");
        }
        
        System.out.println("   → No visualization mapping found for: " + algorithm.getName());
        return null;
    }

    private String checkAndReturnClass(String className) {
        try {
            Class.forName(className);
            System.out.println("✅ Found visualization class: " + className);
            return className;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Visualization class not found: " + className);
            return null;
        }
    }

    private void showVisualizationError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Visualization Error");
        alert.setHeaderText("Failed to launch visualization");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // UI Component Factory Methods
    private ScrollPane createStyledScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: white; -fx-background-color: white;");
        scrollPane.setPadding(new Insets(5));
        return scrollPane;
    }

    private VBox createContentBox() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white;");
        return content;
    }

    private VBox createSectionBox(String title) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 12; -fx-padding: 20; " +
                        "-fx-border-color: #e9ecef; -fx-border-width: 1;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 0 0 5 0;");
        
        // Add decorative underline
        Pane underline = new Pane();
        underline.setPrefHeight(2);
        underline.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-background-radius: 1;");
        underline.setMaxWidth(200);

        section.getChildren().addAll(titleLabel, underline);
        return section;
    }

    private TextFlow createFormattedTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-text-fill: #34495e; -fx-line-spacing: 1.6;");

        if (content == null || content.trim().isEmpty()) {
            Text emptyText = new Text("No content available.");
            emptyText.setStyle("-fx-font-style: italic; -fx-fill: #6c757d; -fx-font-size: 14;");
            textFlow.getChildren().add(emptyText);
            return textFlow;
        }

        // Handle markdown-style formatting
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                textFlow.getChildren().add(new Text("\n\n"));
                continue;
            }

            // Handle bold text: **bold**
            if (line.matches(".*\\*\\*.*\\*\\*.*")) {
                String[] parts = line.split("\\*\\*");
                for (int i = 0; i < parts.length; i++) {
                    Text text = new Text(parts[i]);
                    if (i % 2 == 1) { // Odd indices are bold parts
                        text.setStyle("-fx-font-weight: bold; -fx-fill: #2c3e50; -fx-font-size: 14;");
                    } else {
                        text.setStyle("-fx-font-size: 14; -fx-fill: #34495e;");
                    }
                    textFlow.getChildren().add(text);
                }
            } 
            // Handle headers: ### Header
            else if (line.trim().startsWith("###")) {
                Text text = new Text(line.replace("###", "").trim() + "\n");
                text.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-fill: #2c3e50;");
                textFlow.getChildren().add(text);
            }
            // Handle bullet points
            else if (line.trim().startsWith("-") || line.trim().startsWith("•")) {
                Text bullet = new Text("• " + line.substring(1).trim() + "\n");
                bullet.setStyle("-fx-font-size: 14; -fx-fill: #34495e; -fx-font-weight: normal;");
                textFlow.getChildren().add(bullet);
            }
            // Handle numbered lists
            else if (line.trim().matches("^\\d+\\..*")) {
                Text numbered = new Text(line.trim() + "\n");
                numbered.setStyle("-fx-font-size: 14; -fx-fill: #34495e; -fx-font-weight: normal;");
                textFlow.getChildren().add(numbered);
            }
            else {
                Text text = new Text(line + "\n");
                text.setStyle("-fx-font-size: 14; -fx-fill: #34495e;");
                textFlow.getChildren().add(text);
            }
            
            textFlow.getChildren().add(new Text("\n"));
        }

        return textFlow;
    }

    private VBox createMetadataBox(Document metadata) {
        VBox metaBox = new VBox(12);
        metaBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-padding: 15;");

        if (metadata != null) {
            String difficulty = metadata.getString("difficulty");
            String lastUpdated = metadata.getString("last_updated");
            List<?> tags = metadata.get("tags", List.class);

            metaBox.getChildren().add(createDetailRow("🎯 Difficulty:", difficulty, getDifficultyColor(difficulty)));
            
            if (lastUpdated != null) {
                metaBox.getChildren().add(createDetailRow("📅 Last Updated:", lastUpdated, "#6c757d"));
            }
            
            if (tags != null && !tags.isEmpty()) {
                HBox tagsRow = new HBox(10);
                tagsRow.setAlignment(Pos.CENTER_LEFT);
                
                Label tagsLabel = new Label("🏷️ Tags:");
                tagsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                tagsLabel.setStyle("-fx-text-fill: #495057;");
                
                HBox tagsContainer = new HBox(8);
                for (Object tag : tags) {
                    Label tagLabel = createTagLabel(tag.toString());
                    tagsContainer.getChildren().add(tagLabel);
                }
                
                tagsRow.getChildren().addAll(tagsLabel, tagsContainer);
                metaBox.getChildren().add(tagsRow);
            }
        } else {
            Label noMetadata = new Label("No metadata available.");
            noMetadata.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
            metaBox.getChildren().add(noMetadata);
        }

        return metaBox;
    }

    private VBox createDiagramBox(Node diagram) {
        VBox diagramBox = new VBox(10);
        diagramBox.setAlignment(Pos.CENTER);
        diagramBox.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-padding: 15;");
        
        Label diagramLabel = new Label("📊 Flowchart Diagram");
        diagramLabel.setStyle("-fx-text-fill: #495057; -fx-font-weight: bold; -fx-font-size: 14;");
        
        diagramBox.getChildren().addAll(diagramLabel, diagram);
        return diagramBox;
    }

    private VBox createCodeBox(String languageName, String code, String color) {
        VBox codeBox = new VBox(10);
        codeBox.setStyle("-fx-background-color: #2d3748; -fx-border-radius: 10; -fx-padding: 15;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label langLabel = new Label(languageName);
        langLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        langLabel.setStyle("-fx-text-fill: " + color + ";");

        header.getChildren().add(langLabel);

        TextArea codeArea = new TextArea(code);
        codeArea.setEditable(false);
        codeArea.setWrapText(false);
        codeArea.setStyle("-fx-control-inner-background: #2d3748; -fx-text-fill: #e2e8f0; " +
                        "-fx-font-family: 'Consolas', 'Monaco', 'Monospace'; -fx-font-size: 13; " +
                        "-fx-border-color: #4a5568; -fx-border-radius: 6;");
        codeArea.setPrefHeight(200);

        codeBox.getChildren().addAll(header, codeArea);
        return codeBox;
    }

    private HBox createDetailRow(String label, String value, String color) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Label keyLabel = new Label(label);
        keyLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        keyLabel.setStyle("-fx-text-fill: #495057;");
        keyLabel.setMinWidth(120);

        Label valueLabel = new Label(value != null ? value : "N/A");
        valueLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        row.getChildren().addAll(keyLabel, valueLabel);
        return row;
    }

    private Label createTagLabel(String tag) {
        Label tagLabel = new Label(tag);
        tagLabel.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                         "-fx-text-fill: white; -fx-padding: 4 12 4 12; " +
                         "-fx-background-radius: 15; -fx-border-radius: 15; " +
                         "-fx-font-size: 11; -fx-font-weight: bold;");
        return tagLabel;
    }

    // Mermaid rendering methods
    private String extractMermaidCode(String content) {
        if (content == null) return null;
        Pattern pattern = Pattern.compile("```mermaid\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private Node createMermaidWebView(String mermaidCode) {
        WebView webView = new WebView();
        webView.setPrefSize(700, 450);
        webView.setMinHeight(350);

        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/mermaid@9.3.0/dist/mermaid.min.js"></script>
                <style>
                    body { 
                        margin: 0; 
                        padding: 20px; 
                        background: white;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                    }
                    .mermaid { 
                        text-align: center;
                        font-family: Arial, sans-serif;
                    }
                    .error {
                        color: red;
                        text-align: center;
                        padding: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="mermaid">
                    %s
                </div>
                <script>
                    try {
                        mermaid.initialize({ 
                            startOnLoad: true, 
                            theme: 'default',
                            flowchart: { 
                                useMaxWidth: true,
                                htmlLabels: true,
                                curve: 'basis'
                            }
                        });
                    } catch (error) {
                        document.body.innerHTML = '<div class="error">Failed to render flowchart: ' + error.message + '</div>';
                    }
                </script>
            </body>
            </html>
            """.formatted(mermaidCode != null ? mermaidCode : "graph TD\nA[Error] --> B[No diagram data]");

        webView.getEngine().loadContent(html);
        return webView;
    }

    private String getDifficultyColor(String difficulty) {
        if (difficulty == null) return "#6c757d";
        switch (difficulty.toLowerCase()) {
            case "beginner": return "#28a745";
            case "easy": return "#28a745";
            case "intermediate": return "#ffc107";
            case "advanced": return "#fd7e14";
            case "expert": return "#dc3545";
            default: return "#6c757d";
        }
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Algorithms");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(40);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        String normalStyle = "-fx-background-color: linear-gradient(to right, #6c757d, #5a6268); " +
                           "-fx-text-fill: white; -fx-background-radius: 8; " +
                           "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 2, 2);";
        
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #5a6268, #495057); " +
                          "-fx-text-fill: white; -fx-background-radius: 8; " +
                          "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 3, 3);";
        
        backBtn.setStyle(normalStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));
        
        backBtn.setOnAction(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });
        
        return backBtn;
    }
}