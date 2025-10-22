package com.dsa.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HomePage extends Application {

    @Override
    public void start(Stage stage) {
        // Main container with gradient background
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Title label with improved styling
        Label title = new Label("DSA Learning Kit Simulator");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");
        
        Label subtitle = new Label("Master Data Structures & Algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        // Create feature buttons
        Button viewAlgorithmsBtn = createStyledButton("View Algorithms", "#4CAF50");
        Button visualizeAlgoBtn = createStyledButton("Algorithm Visualizations", "#2196F3");
        Button practiceBtn = createStyledButton("Practice Problems", "#FF9800");
        Button exitBtn = createStyledButton("Exit", "#f44336");

        // Button actions
        viewAlgorithmsBtn.setOnAction(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });
        
        visualizeAlgoBtn.setOnAction(e -> {
            ViewVisualizationPage vizPage = new ViewVisualizationPage(stage);
            vizPage.show();
        });
        
        practiceBtn.setOnAction(e -> System.out.println("Practice Problems clicked"));
        exitBtn.setOnAction(e -> stage.close());

        // Feature description panel
        VBox featurePanel = createFeaturePanel();
        
        // Layout organization
        VBox headerBox = new VBox(10, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);
        
        VBox buttonBox = new VBox(15, viewAlgorithmsBtn, visualizeAlgoBtn, practiceBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        root.getChildren().addAll(headerBox, buttonBox, featurePanel);

        Scene scene = new Scene(root, 600, 650);
        stage.setScene(scene);
        stage.setTitle("DSA Simulator - Home");
        stage.setMinWidth(600);
        stage.setMinHeight(650);
        stage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        
        // Hover effect styles
        String normalStyle = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 2, 2);",
            color
        );
        
        String hoverStyle = String.format(
            "-fx-background-color: derive(%s, -20%%); -fx-text-fill: white; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 3, 3);",
            color
        );
        
        button.setStyle(normalStyle);
        
        // Hover effects
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        
        return button;
    }

    private VBox createFeaturePanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setMaxWidth(400);
        panel.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 12; " +
                      "-fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 12; -fx-border-width: 1;");

        Label featuresTitle = new Label("Features Overview");
        featuresTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        featuresTitle.setStyle("-fx-text-fill: white;");

        VBox featureList = new VBox(8);
        featureList.setAlignment(Pos.CENTER_LEFT);
        
        String[] features = {
            "• Browse comprehensive algorithm explanations",
            "• Interactive algorithm visualizations", 
            "• Practice with curated problem sets",
            "• Step-by-step learning path",
            "• Real-time algorithm execution",
            "• Multiple visualization types"
        };
        
        for (String feature : features) {
            Label featureLabel = new Label(feature);
            featureLabel.setFont(Font.font("System", 13));
            featureLabel.setStyle("-fx-text-fill: #e0e0e0;");
            featureList.getChildren().add(featureLabel);
        }

        panel.getChildren().addAll(featuresTitle, featureList);
        return panel;
    }

    public static void main(String[] args) {
        launch();
    }
}