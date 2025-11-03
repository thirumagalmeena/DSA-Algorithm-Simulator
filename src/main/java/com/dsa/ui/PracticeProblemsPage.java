package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PracticeProblemsPage {

    private final Stage stage;
    private MongoCollection<Document> practiceCollection;
    private ListView<AlgorithmPractice> algorithmsListView;
    private TextField searchField;
    private ComboBox<String> sortComboBox;
    private List<AlgorithmPractice> allAlgorithms;

    public PracticeProblemsPage(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://23pd03_db_user:ldn2saUWgoBBINVw@cluster0.gwvt6zu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            MongoDatabase database = mongoClient.getDatabase("algodb");
            practiceCollection = database.getCollection("practice");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public void show() {
        // Main container with gradient background
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Header section
        VBox headerBox = createHeaderBox();

        // Search and sort controls
        HBox controlsBox = createSearchSortControls();

        // Algorithms list
        VBox listContainer = createAlgorithmsList();

        // Back button
        Button backBtn = createBackButton();

        VBox contentBox = new VBox(20, headerBox, controlsBox, listContainer, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(800);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Practice Problems - DSA Simulator");
    }

    private VBox createHeaderBox() {
        Label title = new Label("Practice Problems");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.5, 3, 3);");

        Label subtitle = new Label("Select an algorithm to practice");
        subtitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        subtitle.setStyle("-fx-text-fill: #e8f4f8;");

        VBox headerBox = new VBox(8, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        
        return headerBox;
    }

    private HBox createSearchSortControls() {
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setMaxWidth(700);

        // Search field
        VBox searchContainer = new VBox(5);
        Label searchLabel = new Label("Search Algorithms:");
        searchLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        searchLabel.setStyle("-fx-text-fill: white;");

        searchField = new TextField();
        searchField.setPromptText("Enter algorithm name...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; " +
                           "-fx-padding: 8; -fx-font-size: 14;");
        
        searchContainer.getChildren().addAll(searchLabel, searchField);

        // Sort combo box
        VBox sortContainer = new VBox(5);
        Label sortLabel = new Label("Sort By:");
        sortLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        sortLabel.setStyle("-fx-text-fill: white;");

        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll(
            "Name (A-Z)",
            "Name (Z-A)", 
            "Most Quizzes",
            "Fewest Quizzes",
            "Most Problems", 
            "Fewest Problems"
        );
        sortComboBox.setValue("Name (A-Z)");
        sortComboBox.setPrefWidth(180);
        sortComboBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        sortContainer.getChildren().addAll(sortLabel, sortComboBox);

        // Clear button
        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(80);
        clearButton.setPrefHeight(35);
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 5; " +
                           "-fx-font-weight: bold;");
        clearButton.setOnMouseEntered(e -> 
            clearButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-background-radius: 5; " +
                               "-fx-font-weight: bold;")
        );
        clearButton.setOnMouseExited(e -> 
            clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 5; " +
                               "-fx-font-weight: bold;")
        );

        clearButton.setOnAction(e -> {
            searchField.clear();
            sortComboBox.setValue("Name (A-Z)");
            refreshAlgorithmList();
        });

        // Add event listeners
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAndSortAlgorithms();
        });

        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterAndSortAlgorithms();
        });

        controlsBox.getChildren().addAll(searchContainer, sortContainer, clearButton);
        return controlsBox;
    }

    private void filterAndSortAlgorithms() {
        if (allAlgorithms == null) return;

        String searchText = searchField.getText().toLowerCase();
        String sortOption = sortComboBox.getValue();

        // Filter algorithms based on search text
        List<AlgorithmPractice> filteredAlgorithms = allAlgorithms.stream()
            .filter(algorithm -> algorithm.getName().toLowerCase().contains(searchText))
            .collect(Collectors.toList());

        // Sort algorithms based on selected option
        Comparator<AlgorithmPractice> comparator = getComparator(sortOption);
        filteredAlgorithms.sort(comparator);

        // Update the ListView
        algorithmsListView.getItems().setAll(filteredAlgorithms);

        // Show message if no results found
        if (filteredAlgorithms.isEmpty() && !searchText.isEmpty()) {
            algorithmsListView.setPlaceholder(new Label("No algorithms found matching '" + searchText + "'"));
        } else {
            algorithmsListView.setPlaceholder(new Label("No algorithms available"));
        }
    }

    private Comparator<AlgorithmPractice> getComparator(String sortOption) {
        switch (sortOption) {
            case "Name (A-Z)":
                return Comparator.comparing(AlgorithmPractice::getName);
            case "Name (Z-A)":
                return Comparator.comparing(AlgorithmPractice::getName).reversed();
            case "Most Quizzes":
                return Comparator.comparing(AlgorithmPractice::getQuizCount).reversed();
            case "Fewest Quizzes":
                return Comparator.comparing(AlgorithmPractice::getQuizCount);
            case "Most Problems":
                return Comparator.comparing(AlgorithmPractice::getProblemCount).reversed();
            case "Fewest Problems":
                return Comparator.comparing(AlgorithmPractice::getProblemCount);
            default:
                return Comparator.comparing(AlgorithmPractice::getName);
        }
    }

    private void refreshAlgorithmList() {
        allAlgorithms = loadAlgorithms();
        filterAndSortAlgorithms();
    }

    private VBox createAlgorithmsList() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(700);

        Label listTitle = new Label("Available Algorithms");
        listTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        listTitle.setStyle("-fx-text-fill: white;");

        algorithmsListView = new ListView<>();
        algorithmsListView.setPrefHeight(350);
        algorithmsListView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                                   "-fx-border-radius: 10; -fx-background-radius: 10;");

        // Load algorithms
        refreshAlgorithmList();

        // Custom cell factory
        algorithmsListView.setCellFactory(param -> new ListCell<AlgorithmPractice>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label statsLabel = new Label();
            private final HBox statsBox = new HBox();
            
            {
                content.setSpacing(10);
                content.setPadding(new Insets(15));
                
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                statsLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                statsLabel.setStyle("-fx-text-fill: #7f8c8d;");
                
                statsBox.setSpacing(20);
                statsBox.getChildren().addAll(statsLabel);
                
                content.getChildren().addAll(nameLabel, statsBox);
                container.getChildren().add(content);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(AlgorithmPractice algorithm, boolean empty) {
                super.updateItem(algorithm, empty);
                
                if (empty || algorithm == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(algorithm.getName());
                    statsLabel.setText(algorithm.getQuizCount() + " quizzes • " + 
                                      algorithm.getProblemCount() + " coding problems");
                    
                    // Card styling
                    String[] cardColors = {
                        "-fx-background-color: linear-gradient(to right, #ffffff, #f8f9fa); -fx-background-radius: 12; -fx-border-color: #e9ecef; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 2, 2);",
                        "-fx-background-color: linear-gradient(to right, #f8f9fa, #ffffff); -fx-background-radius: 12; -fx-border-color: #e9ecef; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 2, 2);"
                    };
                    
                    int colorIndex = getIndex() % 2;
                    content.setStyle(cardColors[colorIndex]);
                    
                    // Hover effect
                    setOnMouseEntered(e -> {
                        content.setStyle("-fx-background-color: linear-gradient(to right, #e3f2fd, #bbdefb); " +
                                       "-fx-background-radius: 12; -fx-border-color: #90caf9; -fx-border-radius: 12; " +
                                       "-fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.5, 3, 3);");
                    });
                    
                    setOnMouseExited(e -> {
                        content.setStyle(cardColors[colorIndex]);
                    });
                    
                    // Click to open practice page
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1) {
                            openPracticePage(algorithm);
                        }
                    });
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        });

        container.getChildren().addAll(listTitle, algorithmsListView);
        return container;
    }

    private List<AlgorithmPractice> loadAlgorithms() {
        List<AlgorithmPractice> algorithms = new ArrayList<>();
        
        if (practiceCollection == null) {
            System.err.println("MongoDB collection is null - connection failed");
            return algorithms;
        }

        FindIterable<Document> practiceDocs = practiceCollection.find();
        
        System.out.println("=== DEBUG: Loading Algorithms ===");
        int docCount = 0;
        
        for (Document doc : practiceDocs) {
            docCount++;
            System.out.println("Document " + docCount + ": " + doc.toJson());
            
            // Use the correct field names from your JSON
            String topic = doc.getString("topic");
            
            // Get quizzes and problems lists
            List<Document> quizzes = doc.getList("quiz", Document.class);
            List<Document> problems = doc.getList("hackerrank_problems", Document.class);
            
            // Debug field values
            System.out.println("  Topic: " + topic);
            System.out.println("  Quizzes: " + (quizzes != null ? quizzes.size() : "null"));
            System.out.println("  Problems: " + (problems != null ? problems.size() : "null"));
            
            int quizCount = quizzes != null ? quizzes.size() : 0;
            int problemCount = problems != null ? problems.size() : 0;
            
            if (topic != null) {
                // Use topic as both ID and name, or generate an ID
                String algorithmId = "algo_" + docCount;
                algorithms.add(new AlgorithmPractice(algorithmId, topic, quizCount, problemCount));
            }
        }
        
        System.out.println("Total algorithms found: " + docCount);
        System.out.println("=== END DEBUG ===");
        
        return algorithms;
    }

    private void openPracticePage(AlgorithmPractice algorithm) {
        PracticeProblemsDetailPage detailPage = new PracticeProblemsDetailPage(stage, algorithm.getId(), algorithm.getName());
        detailPage.show();
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Home");
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
            HomePage home = new HomePage();
            try {
                home.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        return backBtn;
    }

    // Data class for algorithm practice info
    public static class AlgorithmPractice {
        private final String id;
        private final String name;
        private final int quizCount;
        private final int problemCount;

        public AlgorithmPractice(String id, String name, int quizCount, int problemCount) {
            this.id = id;
            this.name = name;
            this.quizCount = quizCount;
            this.problemCount = problemCount;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getQuizCount() { return quizCount; }
        public int getProblemCount() { return problemCount; }

        @Override
        public String toString() {
            return name;
        }
    }
}