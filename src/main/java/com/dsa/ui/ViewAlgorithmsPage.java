package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class ViewAlgorithmsPage {

    private final Stage stage;
    private MongoCollection<Document> collection;
    private static Dotenv dotenv;

    static {
        // Initialize dotenv
        dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    public ViewAlgorithmsPage(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // Get credentials from environment variables
            String uri = dotenv.get("MONGODB_URI");
            String databaseName = dotenv.get("DATABASE_NAME");

            // Validate environment variables
            if (uri == null || uri.isEmpty()) {
                throw new IllegalStateException("MONGODB_URI not found in environment variables");
            }
            if (databaseName == null || databaseName.isEmpty()) {
                throw new IllegalStateException("DATABASE_NAME not found in environment variables");
            }

            // Use environment variables for MongoDB connection
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            collection = database.getCollection("algorithms");
            
            System.out.println("✅ Connected to MongoDB using environment variables");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message to user
            javafx.application.Platform.runLater(() -> {
                showErrorAlert("Database Connection Error", 
                    "Failed to connect to database. Please check your environment variables.\n\n" +
                    "Error: " + e.getMessage());
            });
        }
    }

    public void show() {
        // Main container with gradient background
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Header section
        Label title = new Label("Algorithm Library");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");

        Label subtitle = new Label("Browse and learn various algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // Connection status label
        Label connectionStatus = new Label();
        connectionStatus.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        if (collection == null) {
            connectionStatus.setText("⚠️ Not connected to database");
            connectionStatus.setStyle("-fx-text-fill: #ff6b6b;");
        } else {
            connectionStatus.setText("✅ Connected to database");
            connectionStatus.setStyle("-fx-text-fill: #51cf66;");
        }

        // ListView for algorithms
        ListView<AlgorithmData> listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setMaxWidth(650);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                         "-fx-border-radius: 10; -fx-background-radius: 10;");

        ObservableList<AlgorithmData> algorithms = FXCollections.observableArrayList();

        // Load algorithms from MongoDB
        Thread loadDataThread = new Thread(() -> {
            List<AlgorithmData> algoList = loadAlgorithmsFromMongoDB();
            
            javafx.application.Platform.runLater(() -> {
                algorithms.setAll(algoList);
                progressIndicator.setVisible(false);
                
                // Update connection status based on results
                if (collection == null) {
                    connectionStatus.setText("❌ Database connection failed");
                    connectionStatus.setStyle("-fx-text-fill: #ff6b6b;");
                } else if (algoList.isEmpty()) {
                    connectionStatus.setText("ℹ️ Connected but no algorithms found");
                    connectionStatus.setStyle("-fx-text-fill: #ffd43b;");
                } else {
                    connectionStatus.setText("✅ Connected to database - " + algoList.size() + " algorithms loaded");
                    connectionStatus.setStyle("-fx-text-fill: #51cf66;");
                }
            });
        });
        
        loadDataThread.setDaemon(true);
        loadDataThread.start();

        // Custom cell factory for algorithm cards
        listView.setCellFactory(param -> new ListCell<AlgorithmData>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Label difficultyLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final HBox tagsBox = new HBox();
            
            {
                content.setSpacing(8);
                content.setPadding(new Insets(15));
                
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                categoryLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                categoryLabel.setStyle("-fx-text-fill: #7f8c8d;");
                
                difficultyLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
                
                descriptionLabel.setFont(Font.font("System", 12));
                descriptionLabel.setStyle("-fx-text-fill: #34495e;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(600);
                
                tagsBox.setSpacing(5);
                
                content.getChildren().addAll(nameLabel, categoryLabel, difficultyLabel, descriptionLabel, tagsBox);
                container.getChildren().add(content);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(AlgorithmData algorithm, boolean empty) {
                super.updateItem(algorithm, empty);
                
                if (empty || algorithm == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(algorithm.getName());
                    categoryLabel.setText(algorithm.getCategory());
                    descriptionLabel.setText(algorithm.getDescription());
                    
                    // Set difficulty with color coding
                    String difficulty = algorithm.getDifficulty();
                    String difficultyColor = getDifficultyColor(difficulty);
                    difficultyLabel.setText("Difficulty: " + difficulty);
                    difficultyLabel.setStyle("-fx-text-fill: " + difficultyColor + ";");
                    
                    // Clear and add tags
                    tagsBox.getChildren().clear();
                    for (String tag : algorithm.getTags()) {
                        Label tagLabel = createTagLabel(tag);
                        tagsBox.getChildren().add(tagLabel);
                    }
                    
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
                    
                    // Click to view details
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            showAlgorithmDetails(algorithm);
                        }
                    });
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        });

        listView.setItems(algorithms);

        // Back button
        Button backBtn = createBackButton();
        
        // Results count
        Label countLabel = new Label("Loading algorithms...");
        countLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        countLabel.setStyle("-fx-text-fill: #e0e0e0;");

        algorithms.addListener((javafx.collections.ListChangeListener.Change<? extends AlgorithmData> c) -> {
            countLabel.setText("Found " + algorithms.size() + " algorithms");
        });

        VBox contentBox = new VBox(15, headerBox, connectionStatus, countLabel, progressIndicator, listView, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(700);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Algorithm Library - DSA Simulator");
    }

    private List<AlgorithmData> loadAlgorithmsFromMongoDB() {
        List<AlgorithmData> algorithms = new ArrayList<>();
        
        if (collection == null) {
            System.err.println("❌ MongoDB collection is null - cannot load algorithms");
            return algorithms;
        }

        try {
            FindIterable<Document> algoDocuments = collection.find();
            
            for (Document doc : algoDocuments) {
                Document algorithmDoc = doc.get("algorithm", Document.class);
                Document metadata = doc.get("metadata", Document.class);
                
                String id = algorithmDoc.getString("id");
                String name = algorithmDoc.getString("name");
                String category = algorithmDoc.getString("category");
                String description = algorithmDoc.getString("description");
                
                String difficulty = "Unknown";
                List<String> tags = new ArrayList<>();
                
                if (metadata != null) {
                    difficulty = metadata.getString("difficulty");
                    if (difficulty == null) difficulty = "Unknown";
                    
                    List<?> tagsList = metadata.get("tags", List.class);
                    if (tagsList != null) {
                        for (Object tag : tagsList) {
                            tags.add(tag.toString());
                        }
                    }
                }
                
                algorithms.add(new AlgorithmData(id, name, category, description, difficulty, tags));
            }
            
            System.out.println("✅ Loaded " + algorithms.size() + " algorithms from MongoDB");
        } catch (Exception e) {
            System.err.println("❌ Error loading algorithms from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        
        return algorithms;
    }

    private void showAlgorithmDetails(AlgorithmData algorithm) {
        // Open detailed algorithm view
        AlgorithmDetailPage detailPage = new AlgorithmDetailPage(stage, algorithm, collection);
        detailPage.show();
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getDifficultyColor(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "beginner": return "#28a745";
            case "easy": return "#28a745";
            case "intermediate": return "#ffc107";
            case "advanced": return "#fd7e14";
            case "expert": return "#dc3545";
            default: return "#6c757d";
        }
    }

    private Label createTagLabel(String tag) {
        Label tagLabel = new Label(tag);
        tagLabel.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-padding: 2 6 2 6; " +
                         "-fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 10;");
        return tagLabel;
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Home");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(35);
        backBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        
        String normalStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6; " +
                           "-fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0.2, 1, 1);";
        
        String hoverStyle = "-fx-background-color: #5a6268; -fx-text-fill: white; -fx-background-radius: 6; " +
                          "-fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);";
        
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

    // Data class to hold algorithm information
    public static class AlgorithmData {
        private final String id;
        private final String name;
        private final String category;
        private final String description;
        private final String difficulty;
        private final List<String> tags;

        public AlgorithmData(String id, String name, String category, String description, String difficulty, List<String> tags) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.difficulty = difficulty;
            this.tags = tags;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getDifficulty() { return difficulty; }
        public List<String> getTags() { return tags; }
    }
}