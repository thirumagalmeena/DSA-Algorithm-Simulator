package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ViewAlgorithmsPage {

    private final Stage stage;
    private MongoCollection<Document> collection;
    private ObservableList<AlgorithmData> allAlgorithms;
    private FilteredList<AlgorithmData> filteredAlgorithms;
    private SortedList<AlgorithmData> sortedAlgorithms;
    
    // UI Components
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> difficultyFilter;
    private ComboBox<String> tagsFilter;
    private ComboBox<String> sortOrderComboBox;
    private ListView<AlgorithmData> listView;
    private Label countLabel;
    private TabPane filterTabPane;

    public ViewAlgorithmsPage(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://23pd03_db_user:ldn2saUWgoBBINVw@cluster0.gwvt6zu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            MongoDatabase database = mongoClient.getDatabase("algodb");
            collection = database.getCollection("algorithms");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
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

        Label subtitle = new Label("Browse, search, and filter algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Search and Sort controls
        HBox topControlsBox = createTopControls();

        // Filter tabs
        filterTabPane = createFilterTabs();

        // Loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // ListView for algorithms
        listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setMaxWidth(700);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                         "-fx-border-radius: 10; -fx-background-radius: 10;");

        // Initialize data structures
        allAlgorithms = FXCollections.observableArrayList();
        filteredAlgorithms = new FilteredList<>(allAlgorithms);
        sortedAlgorithms = new SortedList<>(filteredAlgorithms);

        // Set custom cell factory
        listView.setCellFactory(param -> createAlgorithmCell());

        // Bind sorted list to ListView
        listView.setItems(sortedAlgorithms);

        // Load algorithms from MongoDB
        Thread loadDataThread = new Thread(() -> {
            List<AlgorithmData> algoList = loadAlgorithmsFromMongoDB();
            
            javafx.application.Platform.runLater(() -> {
                allAlgorithms.setAll(algoList);
                progressIndicator.setVisible(false);
                updateFilterOptions();
                updateCountLabel();
            });
        });
        
        loadDataThread.setDaemon(true);
        loadDataThread.start();

        // Results count
        countLabel = new Label("Loading algorithms...");
        countLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        countLabel.setStyle("-fx-text-fill: #e0e0e0;");

        // Back button
        Button backBtn = createBackButton();
        
        VBox contentBox = new VBox(15, headerBox, topControlsBox, filterTabPane, countLabel, progressIndicator, listView, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(750);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 900, 850);
        stage.setScene(scene);
        stage.setTitle("Algorithm Library - DSA Simulator");
    }

    private HBox createTopControls() {
        HBox topControls = new HBox(15);
        topControls.setAlignment(Pos.CENTER);
        topControls.setPadding(new Insets(10));
        topControls.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search algorithms by name, description...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6;");

        // Sort order
        sortOrderComboBox = new ComboBox<>();
        sortOrderComboBox.setPromptText("Sort Order");
        sortOrderComboBox.setPrefWidth(120);
        sortOrderComboBox.setStyle("-fx-background-radius: 6; -fx-border-radius: 6;");
        sortOrderComboBox.getItems().addAll("A-Z", "Z-A", "Difficulty Low-High", "Difficulty High-Low");

        // Clear filters button
        Button clearFiltersBtn = new Button("Clear All");
        clearFiltersBtn.setPrefWidth(100);
        clearFiltersBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6;");

        // Set up event handlers
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        sortOrderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            applySorting();
        });

        clearFiltersBtn.setOnAction(e -> clearAllFilters());

        topControls.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Sort:"), sortOrderComboBox,
            clearFiltersBtn
        );

        return topControls;
    }

    private TabPane createFilterTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");
        tabPane.setMaxWidth(700);

        // Category Tab
        Tab categoryTab = new Tab("Category");
        categoryTab.setContent(createCategoryFilterContent());
        categoryTab.setStyle("-fx-background-color: rgba(255,255,255,0.9);");

        // Difficulty Tab
        Tab difficultyTab = new Tab("Difficulty");
        difficultyTab.setContent(createDifficultyFilterContent());
        difficultyTab.setStyle("-fx-background-color: rgba(255,255,255,0.9);");

        // Tags Tab
        Tab tagsTab = new Tab("Tags");
        tagsTab.setContent(createTagsFilterContent());
        tagsTab.setStyle("-fx-background-color: rgba(255,255,255,0.9);");

        tabPane.getTabs().addAll(categoryTab, difficultyTab, tagsTab);

        return tabPane;
    }

    private VBox createCategoryFilterContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10;");

        Label title = new Label("Filter by Category");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: #2c3e50;");

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Select Category");
        categoryFilter.setPrefWidth(250);
        categoryFilter.setStyle("-fx-background-radius: 6; -fx-border-radius: 6;");

        Button clearCategoryBtn = new Button("Clear Category");
        clearCategoryBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 6;");

        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        clearCategoryBtn.setOnAction(e -> {
            categoryFilter.setValue(null);
        });

        content.getChildren().addAll(title, categoryFilter, clearCategoryBtn);
        return content;
    }

    private VBox createDifficultyFilterContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10;");

        Label title = new Label("Filter by Difficulty");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: #2c3e50;");

        difficultyFilter = new ComboBox<>();
        difficultyFilter.setPromptText("Select Difficulty");
        difficultyFilter.setPrefWidth(250);
        difficultyFilter.setStyle("-fx-background-radius: 6; -fx-border-radius: 6;");
        difficultyFilter.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");

        Button clearDifficultyBtn = new Button("Clear Difficulty");
        clearDifficultyBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 6;");

        difficultyFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        clearDifficultyBtn.setOnAction(e -> {
            difficultyFilter.setValue(null);
        });

        content.getChildren().addAll(title, difficultyFilter, clearDifficultyBtn);
        return content;
    }

    private VBox createTagsFilterContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10;");

        Label title = new Label("Filter by Tags");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: #2c3e50;");

        tagsFilter = new ComboBox<>();
        tagsFilter.setPromptText("Select Tag");
        tagsFilter.setPrefWidth(250);
        tagsFilter.setStyle("-fx-background-radius: 6; -fx-border-radius: 6;");

        Button clearTagsBtn = new Button("Clear Tag");
        clearTagsBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 6;");

        tagsFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        clearTagsBtn.setOnAction(e -> {
            tagsFilter.setValue(null);
        });

        content.getChildren().addAll(title, tagsFilter, clearTagsBtn);
        return content;
    }

    private void applyFilters() {
        filteredAlgorithms.setPredicate(algorithm -> {
            // Search filter
            String searchText = searchField.getText();
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!algorithm.getName().toLowerCase().contains(lowerCaseFilter) &&
                    !algorithm.getDescription().toLowerCase().contains(lowerCaseFilter) &&
                    !algorithm.getCategory().toLowerCase().contains(lowerCaseFilter) &&
                    !algorithm.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerCaseFilter))) {
                    return false;
                }
            }

            // Category filter
            String selectedCategory = categoryFilter.getValue();
            if (selectedCategory != null && !algorithm.getCategory().equals(selectedCategory)) {
                return false;
            }

            // Difficulty filter
            String selectedDifficulty = difficultyFilter.getValue();
            if (selectedDifficulty != null && !algorithm.getDifficulty().equals(selectedDifficulty)) {
                return false;
            }

            // Tags filter
            String selectedTag = tagsFilter.getValue();
            if (selectedTag != null && !algorithm.getTags().contains(selectedTag)) {
                return false;
            }

            return true;
        });
        
        updateCountLabel();
    }

    private void applySorting() {
        String sortOrder = sortOrderComboBox.getValue();
        
        if (sortOrder == null) {
            // Default sort by name A-Z
            sortedAlgorithms.setComparator(Comparator.comparing(AlgorithmData::getName, String.CASE_INSENSITIVE_ORDER));
            return;
        }

        sortedAlgorithms.setComparator((algo1, algo2) -> {
            int result = 0;
            
            switch (sortOrder) {
                case "A-Z":
                    result = algo1.getName().compareToIgnoreCase(algo2.getName());
                    break;
                case "Z-A":
                    result = algo2.getName().compareToIgnoreCase(algo1.getName());
                    break;
                case "Difficulty Low-High":
                    result = getDifficultyWeight(algo1.getDifficulty()) - getDifficultyWeight(algo2.getDifficulty());
                    break;
                case "Difficulty High-Low":
                    result = getDifficultyWeight(algo2.getDifficulty()) - getDifficultyWeight(algo1.getDifficulty());
                    break;
            }
            
            return result;
        });
    }

    private int getDifficultyWeight(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "beginner": return 1;
            case "intermediate": return 2;
            case "advanced": return 3;
            case "expert": return 4;
            default: return 0;
        }
    }

    private void clearAllFilters() {
        searchField.clear();
        categoryFilter.setValue(null);
        difficultyFilter.setValue(null);
        tagsFilter.setValue(null);
        sortOrderComboBox.setValue(null);
        
        // Reset sorting to default (by name A-Z)
        sortedAlgorithms.setComparator(Comparator.comparing(AlgorithmData::getName, String.CASE_INSENSITIVE_ORDER));
        
        updateCountLabel();
    }

    private void updateFilterOptions() {
        // Update category filter options
        List<String> categories = allAlgorithms.stream()
            .map(AlgorithmData::getCategory)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        categoryFilter.getItems().clear();
        categoryFilter.getItems().addAll(categories);

        // Update tags filter options
        Set<String> allTags = allAlgorithms.stream()
            .flatMap(algo -> algo.getTags().stream())
            .distinct()
            .sorted()
            .collect(Collectors.toSet());
        
        tagsFilter.getItems().clear();
        tagsFilter.getItems().addAll(allTags);
    }

    private void updateCountLabel() {
        int total = allAlgorithms.size();
        int filtered = filteredAlgorithms.size();
        
        if (total == filtered) {
            countLabel.setText("Showing all " + total + " algorithms");
        } else {
            countLabel.setText("Showing " + filtered + " of " + total + " algorithms");
        }
    }

    private ListCell<AlgorithmData> createAlgorithmCell() {
        return new ListCell<AlgorithmData>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Label difficultyLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final HBox tagsBox = new HBox();
            private final HBox infoBox = new HBox();
            
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
                descriptionLabel.setMaxWidth(650);
                
                tagsBox.setSpacing(5);
                infoBox.setSpacing(15);
                infoBox.getChildren().addAll(categoryLabel, difficultyLabel);
                
                content.getChildren().addAll(nameLabel, infoBox, descriptionLabel, tagsBox);
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
        };
    }

    private List<AlgorithmData> loadAlgorithmsFromMongoDB() {
        List<AlgorithmData> algorithms = new ArrayList<>();
        
        if (collection == null) {
            return algorithms;
        }

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
        
        return algorithms;
    }

    private void showAlgorithmDetails(AlgorithmData algorithm) {
        AlgorithmDetailPage detailPage = new AlgorithmDetailPage(stage, algorithm, collection);
        detailPage.show();
    }

    private String getDifficultyColor(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "beginner": return "#28a745";
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

        @Override
        public String toString() {
            return name + " (" + category + ")";
        }
    }
}