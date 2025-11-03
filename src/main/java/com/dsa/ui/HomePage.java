package com.dsa.ui;

import com.dsa.ui.ViewVisualizationPage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HomePage extends Application {

    @Override
    public void start(Stage stage) {
        // Main container with elegant gradient background
        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2, #f093fb);");

        // Navigation Bar
        HBox navBar = createNavigationBar(stage);
        
        // Hero Section
        VBox heroSection = createHeroSection(stage);
        
        // Features Section
        VBox featuresSection = createFeaturesSection(stage);

        mainContent.getChildren().addAll(navBar, heroSection, featuresSection);

        // Create ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene = new Scene(scrollPane, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("DSA Learning Simulator");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();
    }

    private HBox createNavigationBar(Stage stage) {
        HBox navBar = new HBox(30);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(20, 50, 20, 50));
        navBar.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 2);");

        // Navigation buttons
        Button learnAlgorithmsBtn = createNavButton("Learn Algorithms");
        Button visualizeBtn = createNavButton("Visualize");
        Button complexityAnalysisBtn = createNavButton("Complexity Analysis", "#9C27B0");
        Button practiceBtn = createNavButton("Practice");
        Button progressBtn = createNavButton("View Progress");

        // Button actions
        learnAlgorithmsBtn.setOnAction(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });
        
        visualizeBtn.setOnAction(e -> {
            ViewVisualizationPage vizPage = new ViewVisualizationPage(stage);
            vizPage.show();
        });
        
        complexityAnalysisBtn.setOnAction(e -> {
            ComplexityAnalyzerPage analyzer = new ComplexityAnalyzerPage(stage);
            analyzer.show();
        });
              
        practiceBtn.setOnAction(e -> {
            PracticeProblemsPage practicePage = new PracticeProblemsPage(stage);
            practicePage.show();
        });
        
        progressBtn.setOnAction(e -> {
            ProgressDashboard progressDashboard = new ProgressDashboard(stage);
            progressDashboard.show();
        });

        navBar.getChildren().addAll(learnAlgorithmsBtn, visualizeBtn, complexityAnalysisBtn, practiceBtn, progressBtn);
        return navBar;
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c3e50; -fx-border-color: transparent; " +
                       "-fx-cursor: hand; -fx-padding: 8 16 8 16;");
        
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: rgba(102, 126, 234, 0.1); -fx-text-fill: #667eea; -fx-border-color: transparent; " +
                           "-fx-cursor: hand; -fx-padding: 8 16 8 16; -fx-background-radius: 5;");
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c3e50; -fx-border-color: transparent; " +
                           "-fx-cursor: hand; -fx-padding: 8 16 8 16;");
        });
        
        return button;
    }

    private Button createNavButton(String text, String accentColor) {
        Button button = createNavButton(text);
        button.setStyle(button.getStyle() + "-fx-text-fill: " + accentColor + ";");
        return button;
    }

    private VBox createHeroSection(Stage stage) {
        VBox heroSection = new VBox(30);
        heroSection.setAlignment(Pos.CENTER);
        heroSection.setPadding(new Insets(100, 50, 100, 50));
        heroSection.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Main title
        Label mainTitle = new Label("DSA Mastery");
        mainTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 52));
        mainTitle.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 2, 2);");

        // Subtitle
        Label subtitle = new Label("Master Data Structures & Algorithms With Confidence");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.95); -fx-text-alignment: center;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(700);

        // Description
        Label description = new Label("Comprehensive algorithm explanations, interactive visualizations, " +
                                    "and curated practice problems – designed to build your DSA skills from foundation to mastery.");
        description.setFont(Font.font("System", FontWeight.NORMAL, 16));
        description.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-text-alignment: center;");
        description.setWrapText(true);
        description.setMaxWidth(800);
        description.setAlignment(Pos.CENTER);

        // Action buttons container
        HBox actionButtons = new HBox(25);
        actionButtons.setAlignment(Pos.CENTER);

        // Start Learning button
        Button startLearningBtn = createActionButton("Start Learning", "#4CAF50", "#45a049");
        startLearningBtn.setOnAction(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });

        // View Progress button
        Button viewProgressBtn = createActionButton("View Progress", "#2196F3", "#1976D2");
        viewProgressBtn.setOnAction(e -> {
            ProgressDashboard progressDashboard = new ProgressDashboard(stage);
            progressDashboard.show();
        });

        actionButtons.getChildren().addAll(startLearningBtn, viewProgressBtn);

        heroSection.getChildren().addAll(mainTitle, subtitle, description, actionButtons);
        return heroSection;
    }

    private Button createActionButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setPrefSize(200, 55);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        String normalStyle = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 28; " +
            "-fx-border-radius: 28; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.4, 2, 4); " +
            "-fx-cursor: hand; -fx-border-width: 0;",
            color
        );
        
        String hoverStyle = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 28; " +
            "-fx-border-radius: 28; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0.6, 4, 6); " +
            "-fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-border-width: 0;",
            hoverColor
        );
        
        button.setStyle(normalStyle);
        
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        
        return button;
    }

    private VBox createFeaturesSection(Stage stage) {
        VBox featuresSection = new VBox(50);
        featuresSection.setAlignment(Pos.CENTER);
        featuresSection.setPadding(new Insets(80, 50, 100, 50));
        featuresSection.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Section title
        Label featuresTitle = new Label("Start Building Your DSA Foundation");
        featuresTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        featuresTitle.setStyle("-fx-text-fill: #2c3e50; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0.3, 1, 1);");

        // Features grid
        GridPane featuresGrid = new GridPane();
        featuresGrid.setAlignment(Pos.CENTER);
        featuresGrid.setHgap(35);
        featuresGrid.setVgap(35);
        featuresGrid.setPadding(new Insets(50, 0, 0, 0));

        // Feature 1: Learn Algorithms
        VBox feature1 = createFeatureCard(
            "Learn Algorithms", 
            "Comprehensive explanations with step-by-step breakdowns, code examples, and real-world applications for each algorithm.",
            "#4CAF50",
            "#E8F5E8",
            stage
        );
        feature1.setOnMouseClicked(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });

        // Feature 2: Visualize
        VBox feature2 = createFeatureCard(
            "Visualize Algorithms", 
            "Interactive visualizations that show how algorithms work step-by-step, making complex concepts easy to understand.",
            "#2196F3",
            "#E3F2FD",
            stage
        );
        feature2.setOnMouseClicked(e -> {
            ViewVisualizationPage vizPage = new ViewVisualizationPage(stage);
            vizPage.show();
        });

        // Feature 3: Practice
        VBox feature3 = createFeatureCard(
            "Practice Problems", 
            "Curated problem sets with quizzes and coding challenges to test and strengthen your understanding.",
            "#FF9800",
            "#FFF3E0",
            stage
        );
        feature3.setOnMouseClicked(e -> {
            PracticeProblemsPage practicePage = new PracticeProblemsPage(stage);
            practicePage.show();
        });

        // Feature 4: Track Progress
        VBox feature4 = createFeatureCard(
            "Track Progress", 
            "Monitor your learning journey with detailed progress tracking, performance analytics, and personalized recommendations.",
            "#9C27B0",
            "#F3E5F5",
            stage
        );
        feature4.setOnMouseClicked(e -> {
            ProgressDashboard progressDashboard = new ProgressDashboard(stage);
            progressDashboard.show();
        });

        // Add features to grid
        featuresGrid.add(feature1, 0, 0);
        featuresGrid.add(feature2, 1, 0);
        featuresGrid.add(feature3, 0, 1);
        featuresGrid.add(feature4, 1, 1);

        // Call to action section
        VBox ctaSection = createCTASection(stage);

        featuresSection.getChildren().addAll(featuresTitle, featuresGrid, ctaSection);
        return featuresSection;
    }

    private VBox createFeatureCard(String title, String description, String accentColor, String bgColor, Stage stage) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(35, 25, 35, 25));
        card.setPrefSize(300, 250);
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 20; " +
            "-fx-border-color: %s; -fx-border-radius: 20; -fx-border-width: 3; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0.3, 0, 3);",
            bgColor, accentColor
        ));
        
        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(String.format(
                "-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-border-color: %s; -fx-border-radius: 20; -fx-border-width: 4; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.4, 0, 8); " +
                "-fx-cursor: hand; -fx-scale-x: 1.03; -fx-scale-y: 1.03;",
                accentColor
            ));
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 20; " +
                "-fx-border-color: %s; -fx-border-radius: 20; -fx-border-width: 3; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0.3, 0, 3);",
                bgColor, accentColor
            ));
        });


        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 14));
        descLabel.setStyle("-fx-text-fill: #5a6268;");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        descLabel.setMaxWidth(250);
        descLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(titleLabel, descLabel);
        return card;
    }

    private VBox createCTASection(Stage stage) {
        VBox ctaSection = new VBox(30);
        ctaSection.setAlignment(Pos.CENTER);
        ctaSection.setPadding(new Insets(60, 50, 60, 50));
        ctaSection.setStyle("-fx-background-color: linear-gradient(135deg, #667eea, #764ba2); -fx-background-radius: 25;");
        ctaSection.setMaxWidth(800);

        Label ctaTitle = new Label("Ready to Start Your DSA Journey?");
        ctaTitle.setFont(Font.font("System", FontWeight.BOLD, 32));
        ctaTitle.setStyle("-fx-text-fill:rgba(6, 6, 6, 0.9);");

        Label ctaSubtitle = new Label("Join thousands of learners who have mastered Data Structures and Algorithms with our comprehensive platform");
        ctaSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));
        ctaSubtitle.setStyle("-fx-text-fill: rgba(9, 106, 147, 0.9);");
        ctaSubtitle.setWrapText(true);
        ctaSubtitle.setMaxWidth(600);
        ctaSubtitle.setAlignment(Pos.CENTER);

        Button ctaButton = new Button("Get Started Now");
        ctaButton.setPrefSize(220, 60);
        ctaButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        ctaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 30; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 4, 6); -fx-cursor: hand;");
        
        ctaButton.setOnMouseEntered(e -> {
            ctaButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-background-radius: 30; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0.6, 6, 8); -fx-cursor: hand; " +
                              "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });
        
        ctaButton.setOnMouseExited(e -> {
            ctaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 30; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 4, 6); -fx-cursor: hand;");
        });

        ctaButton.setOnAction(e -> {
            ViewAlgorithmsPage viewPage = new ViewAlgorithmsPage(stage);
            viewPage.show();
        });

        ctaSection.getChildren().addAll(ctaTitle, ctaSubtitle, ctaButton);
        return ctaSection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}