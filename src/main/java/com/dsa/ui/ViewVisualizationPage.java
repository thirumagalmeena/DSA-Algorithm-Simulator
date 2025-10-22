package com.dsa.ui;

import com.mongodb.client.MongoCollection;
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
import java.util.ArrayList;
import java.util.List;

public class ViewVisualizationPage {

    private final Stage stage;
    private MongoCollection<Document> collection;

    public ViewVisualizationPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Header section
        Label title = new Label("Algorithm Visualizations");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");

        Label subtitle = new Label("Run interactive simulations of algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // Status label
        Label statusLabel = new Label("Loading visualizations...");
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        statusLabel.setStyle("-fx-text-fill: #e0e0e0;");

        // ListView for visualizations
        ListView<VisualizationData> listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setMaxWidth(650);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                         "-fx-border-radius: 10; -fx-background-radius: 10;");

        ObservableList<VisualizationData> visualizations = FXCollections.observableArrayList();

        // Load visualizations
        Thread loadDataThread = new Thread(() -> {
            List<VisualizationData> vizList = loadAllVisualizations();
            
            javafx.application.Platform.runLater(() -> {
                visualizations.setAll(vizList);
                progressIndicator.setVisible(false);
                statusLabel.setText("Found " + vizList.size() + " visualizations available");
            });
        });
        
        loadDataThread.setDaemon(true);
        loadDataThread.start();

        // Custom cell factory for visualization cards
        listView.setCellFactory(param -> new ListCell<VisualizationData>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Label typeLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label availabilityLabel = new Label();
            private final Button runButton = new Button("Run Visualization");
            
            {
                content.setSpacing(8);
                content.setPadding(new Insets(15));
                
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                categoryLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                categoryLabel.setStyle("-fx-text-fill: #7f8c8d;");
                
                typeLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
                typeLabel.setStyle("-fx-text-fill: #e74c3c;");
                
                descriptionLabel.setFont(Font.font("System", 12));
                descriptionLabel.setStyle("-fx-text-fill: #34495e;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(600);
                
                availabilityLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
                
                runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));
                runButton.setOnMouseExited(e -> runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
                
                runButton.setOnAction(e -> {
                    VisualizationData viz = getItem();
                    if (viz != null) {
                        launchVisualization(viz);
                    }
                });
                
                content.getChildren().addAll(nameLabel, categoryLabel, typeLabel, descriptionLabel, availabilityLabel, runButton);
                container.getChildren().add(content);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(VisualizationData visualization, boolean empty) {
                super.updateItem(visualization, empty);
                
                if (empty || visualization == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(visualization.getName());
                    categoryLabel.setText(visualization.getCategory());
                    typeLabel.setText("Type: " + visualization.getType());
                    descriptionLabel.setText(visualization.getDescription());
                    
                    // Check if visualization class exists and update status
                    boolean classExists = checkClassExists(visualization.getClassName());
                    if (classExists) {
                        availabilityLabel.setText("✅ Ready to launch");
                        availabilityLabel.setStyle("-fx-text-fill: #27ae60;");
                        runButton.setDisable(false);
                    } else {
                        availabilityLabel.setText("❌ Visualization not available");
                        availabilityLabel.setStyle("-fx-text-fill: #e74c3c;");
                        runButton.setDisable(true);
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
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        });

        listView.setItems(visualizations);

        // Back button
        Button backBtn = createBackButton();

        VBox contentBox = new VBox(15, headerBox, statusLabel, progressIndicator, listView, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(700);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Algorithm Visualizations - DSA Simulator");
    }

    private List<VisualizationData> loadAllVisualizations() {
        List<VisualizationData> visualizations = new ArrayList<>();
        
        // Uncomment for debugging:
        // System.out.println("🔍 Loading all available visualizations...");
        
        // ==================== SORTING ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "bubble-sort", "Bubble Sort", "Sorting", 
            "Visualize how bubble sort works by repeatedly swapping adjacent elements",
            "Sorting", "com.dsa.simulator.sorting.BubbleSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "quick-sort", "Quick Sort", "Sorting", 
            "Visualize the divide and conquer approach of quick sort",
            "Sorting", "com.dsa.simulator.sorting.QuickSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "merge-sort", "Merge Sort", "Sorting", 
            "Visualize the merging process in merge sort algorithm",
            "Sorting", "com.dsa.simulator.sorting.MergeSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "insertion-sort", "Insertion Sort", "Sorting",
            "Visualize insertion sort building the final sorted array one item at a time",
            "Sorting", "com.dsa.simulator.sorting.InsertionSortVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "selection-sort", "Selection Sort", "Sorting",
            "Visualize selection sort repeatedly finding the minimum element",
            "Sorting", "com.dsa.simulator.sorting.SelectionSortVisualizer"
        );

        // ==================== SEARCHING ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "binary-search", "Binary Search", "Searching",
            "Visualize the divide and conquer approach of binary search on sorted arrays",
            "Searching", "com.dsa.simulator.searching.BinarySearchVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "linear-search", "Linear Search", "Searching",
            "Visualize linear search checking each element sequentially",
            "Searching", "com.dsa.simulator.searching.LinearSearchVisualizer"
        );

        // ==================== GRAPH TRAVERSAL ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "dijkstra", "Dijkstra's Algorithm", "Graph Algorithms",
            "Visualize Dijkstra's algorithm finding the shortest path in weighted graphs",
            "Graph", "com.dsa.simulator.graphTraversal.DijkstraVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "topological-sort", "Topological Sort", "Graph Algorithms",
            "Visualize topological ordering of directed acyclic graphs (DAGs)",
            "Graph", "com.dsa.simulator.graphTraversal.TopologicalOrderingVisualizer"
        );

        // ==================== GREEDY ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "job-scheduling", "Job Scheduling", "Greedy Algorithms",
            "Visualize greedy job scheduling with deadlines and profits",
            "Greedy", "com.dsa.simulator.greedy.JobSchedulingVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "kruskal", "Kruskal's Algorithm", "Greedy Algorithms",
            "Visualize Kruskal's algorithm for finding Minimum Spanning Tree",
            "Greedy", "com.dsa.simulator.greedy.KruskalVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "prims", "Prim's Algorithm", "Greedy Algorithms",
            "Visualize Prim's algorithm for finding Minimum Spanning Tree",
            "Greedy", "com.dsa.simulator.greedy.PrimsVisualizer"
        );

        // ==================== DYNAMIC PROGRAMMING ====================
        addVisualizationIfExists(visualizations,
            "coin-change", "Coin Change", "Dynamic Programming",
            "Visualize the coin change problem solving with dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "coin-change-demo", "Coin Change Demo", "Dynamic Programming",
            "Interactive demo of the coin change problem",
            "DP", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizerDemo"
        );

        addVisualizationIfExists(visualizations,
            "fibonacci", "Fibonacci Sequence", "Dynamic Programming",
            "Visualize Fibonacci sequence calculation using dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.FibonacciVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "knapsack", "0/1 Knapsack", "Dynamic Programming",
            "Visualize the 0/1 knapsack problem solving with dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.Knapsack01Visualizer"
        );

        addVisualizationIfExists(visualizations,
            "lcs", "Longest Common Subsequence", "Dynamic Programming",
            "Visualize finding the longest common subsequence between two strings",
            "DP", "com.dsa.simulator.dynamicProgramming.LongestCommonSubsequenceVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "pascal-triangle", "Pascal's Triangle", "Dynamic Programming",
            "Visualize Pascal's triangle construction and patterns",
            "DP", "com.dsa.simulator.dynamicProgramming.PascalTriangleVisualizer"
        );

        // Uncomment for debugging:
        /*
        System.out.println("📋 Loaded " + visualizations.size() + " visualizations:");
        for (VisualizationData viz : visualizations) {
            boolean exists = checkClassExists(viz.getClassName());
            System.out.println("   " + (exists ? "✅" : "❌") + " " + viz.getName() + " -> " + viz.getClassName());
        }
        */

        return visualizations;
    }

    private void addVisualizationIfExists(List<VisualizationData> visualizations, 
                                        String id, String name, String category, 
                                        String description, String type, String className) {
        if (checkClassExists(className)) {
            visualizations.add(new VisualizationData(id, name, category, description, type, className));
            // Uncomment for debugging:
            // System.out.println("✅ Added: " + name + " (" + className + ")");
        } else {
            // Uncomment for debugging:
            // System.out.println("❌ Skipped: " + name + " - Class not found: " + className);
        }
    }

    private boolean checkClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            // Uncomment for debugging:
            // System.out.println("❌ Class not found: " + className);
            return false;
        } catch (Exception e) {
            // Uncomment for debugging:
            // System.out.println("❌ Error checking class " + className + ": " + e.getMessage());
            return false;
        }
    }

    private int getWorkingVisualizationsCount(List<VisualizationData> visualizations) {
        int count = 0;
        for (VisualizationData viz : visualizations) {
            if (checkClassExists(viz.getClassName())) {
                count++;
            }
        }
        return count;
    }

    private void launchVisualization(VisualizationData visualization) {
        try {
            // Uncomment for debugging:
            // System.out.println("🚀 Attempting to launch visualization: " + visualization.getClassName());
            
            // Check if class exists first
            if (!checkClassExists(visualization.getClassName())) {
                showError("Class not found", 
                    "Visualization class not found: " + visualization.getClassName() + 
                    "\n\nPlease check:\n" +
                    "1. The class exists in your project\n" +
                    "2. The package path is correct\n" +
                    "3. The class extends Application\n" +
                    "4. The class is compiled");
                return;
            }
            
            // Use reflection to launch the visualization
            Class<?> vizClass = Class.forName(visualization.getClassName());
            Object instance = vizClass.getDeclaredConstructor().newInstance();
            
            if (instance instanceof javafx.application.Application) {
                javafx.application.Application vizApp = (javafx.application.Application) instance;
                
                // Create a new stage for the visualization
                Stage vizStage = new Stage();
                vizStage.setTitle(visualization.getName() + " - DSA Visualizer");
                
                // Start the visualization application
                // Uncomment for debugging:
                // System.out.println("✅ Starting visualization: " + visualization.getClassName());
                vizApp.start(vizStage);
                
            } else {
                showError("Invalid Visualization Class", 
                    "The class " + visualization.getClassName() + " does not extend Application.\n" +
                    "Please make sure your visualization class extends javafx.application.Application");
            }
            
        } catch (Exception e) {
            // Uncomment for debugging:
            // System.err.println("❌ Failed to launch visualization: " + e.getMessage());
            // e.printStackTrace();
            
            showError("Visualization Launch Failed", 
                "Failed to launch " + visualization.getName() + "\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "1. The class has a proper start() method\n" +
                "2. No missing dependencies\n" +
                "3. JavaFX is properly configured");
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    // Data class to hold visualization information
    public static class VisualizationData {
        private final String id;
        private final String name;
        private final String category;
        private final String description;
        private final String type;
        private final String className;

        public VisualizationData(String id, String name, String category, String description, String type, String className) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.type = type;
            this.className = className;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public String getClassName() { return className; }
    }
}

/*
package com.dsa.ui;

import com.mongodb.client.MongoCollection;
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
import java.util.ArrayList;
import java.util.List;

public class ViewVisualizationPage {

    private final Stage stage;
    private MongoCollection<Document> collection;

    public ViewVisualizationPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Header section
        Label title = new Label("Algorithm Visualizations");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");

        Label subtitle = new Label("Run interactive simulations of algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // Debug info label
        Label debugLabel = new Label("Loading visualizations...");
        debugLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        debugLabel.setStyle("-fx-text-fill: #ffeb3b;");

        // ListView for visualizations
        ListView<VisualizationData> listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setMaxWidth(650);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                         "-fx-border-radius: 10; -fx-background-radius: 10;");

        ObservableList<VisualizationData> visualizations = FXCollections.observableArrayList();

        // Load visualizations
        Thread loadDataThread = new Thread(() -> {
            List<VisualizationData> vizList = loadAllVisualizations();
            
            javafx.application.Platform.runLater(() -> {
                visualizations.setAll(vizList);
                progressIndicator.setVisible(false);
                debugLabel.setText("Loaded " + vizList.size() + " visualizations. " + 
                                 getWorkingVisualizationsCount(vizList) + " are working.");
            });
        });
        
        loadDataThread.setDaemon(true);
        loadDataThread.start();

        // Custom cell factory for visualization cards
        listView.setCellFactory(param -> new ListCell<VisualizationData>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Label typeLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label statusLabel = new Label();
            private final Button runButton = new Button("Run Visualization");
            
            {
                content.setSpacing(8);
                content.setPadding(new Insets(15));
                
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                categoryLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                categoryLabel.setStyle("-fx-text-fill: #7f8c8d;");
                
                typeLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
                typeLabel.setStyle("-fx-text-fill: #e74c3c;");
                
                descriptionLabel.setFont(Font.font("System", 12));
                descriptionLabel.setStyle("-fx-text-fill: #34495e;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(600);
                
                statusLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
                
                runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));
                runButton.setOnMouseExited(e -> runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
                
                runButton.setOnAction(e -> {
                    VisualizationData viz = getItem();
                    if (viz != null) {
                        launchVisualization(viz);
                    }
                });
                
                content.getChildren().addAll(nameLabel, categoryLabel, typeLabel, descriptionLabel, statusLabel, runButton);
                container.getChildren().add(content);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(VisualizationData visualization, boolean empty) {
                super.updateItem(visualization, empty);
                
                if (empty || visualization == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(visualization.getName());
                    categoryLabel.setText(visualization.getCategory());
                    typeLabel.setText("Type: " + visualization.getType());
                    descriptionLabel.setText(visualization.getDescription());
                    
                    // Check if visualization class exists and update status
                    boolean classExists = checkClassExists(visualization.getClassName());
                    if (classExists) {
                        statusLabel.setText("✅ Class found: " + visualization.getClassName());
                        statusLabel.setStyle("-fx-text-fill: #27ae60;");
                        runButton.setDisable(false);
                    } else {
                        statusLabel.setText("❌ Class not found: " + visualization.getClassName());
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        runButton.setDisable(true);
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
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        });

        listView.setItems(visualizations);

        // Back button
        Button backBtn = createBackButton();
        
        // Results count
        Label countLabel = new Label("Loading visualizations...");
        countLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        countLabel.setStyle("-fx-text-fill: #e0e0e0;");

        visualizations.addListener((javafx.collections.ListChangeListener.Change<? extends VisualizationData> c) -> {
            countLabel.setText("Found " + visualizations.size() + " visualizations");
        });

        VBox contentBox = new VBox(15, headerBox, debugLabel, countLabel, progressIndicator, listView, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(700);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Algorithm Visualizations - DSA Simulator");
    }

    private List<VisualizationData> loadAllVisualizations() {
        List<VisualizationData> visualizations = new ArrayList<>();
        
        System.out.println("🔍 Loading all available visualizations...");
        
        // ==================== SORTING ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "bubble-sort", "Bubble Sort", "Sorting", 
            "Visualize how bubble sort works by repeatedly swapping adjacent elements",
            "Sorting", "com.dsa.simulator.sorting.BubbleSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "quick-sort", "Quick Sort", "Sorting", 
            "Visualize the divide and conquer approach of quick sort",
            "Sorting", "com.dsa.simulator.sorting.QuickSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "merge-sort", "Merge Sort", "Sorting", 
            "Visualize the merging process in merge sort algorithm",
            "Sorting", "com.dsa.simulator.sorting.MergeSortVisualizer"
        );
        
        addVisualizationIfExists(visualizations,
            "insertion-sort", "Insertion Sort", "Sorting",
            "Visualize insertion sort building the final sorted array one item at a time",
            "Sorting", "com.dsa.simulator.sorting.InsertionSortVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "selection-sort", "Selection Sort", "Sorting",
            "Visualize selection sort repeatedly finding the minimum element",
            "Sorting", "com.dsa.simulator.sorting.SelectionSortVisualizer"
        );

        // ==================== SEARCHING ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "binary-search", "Binary Search", "Searching",
            "Visualize the divide and conquer approach of binary search on sorted arrays",
            "Searching", "com.dsa.simulator.searching.BinarySearchVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "linear-search", "Linear Search", "Searching",
            "Visualize linear search checking each element sequentially",
            "Searching", "com.dsa.simulator.searching.LinearSearchVisualizer"
        );

        // ==================== GRAPH TRAVERSAL ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "dijkstra", "Dijkstra's Algorithm", "Graph Algorithms",
            "Visualize Dijkstra's algorithm finding the shortest path in weighted graphs",
            "Graph", "com.dsa.simulator.graphTraversal.DijkstraVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "topological-sort", "Topological Sort", "Graph Algorithms",
            "Visualize topological ordering of directed acyclic graphs (DAGs)",
            "Graph", "com.dsa.simulator.graphTraversal.TopologicalOrderingVisualizer"
        );

        // ==================== GREEDY ALGORITHMS ====================
        addVisualizationIfExists(visualizations,
            "job-scheduling", "Job Scheduling", "Greedy Algorithms",
            "Visualize greedy job scheduling with deadlines and profits",
            "Greedy", "com.dsa.simulator.greedy.JobSchedulingVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "kruskal", "Kruskal's Algorithm", "Greedy Algorithms",
            "Visualize Kruskal's algorithm for finding Minimum Spanning Tree",
            "Greedy", "com.dsa.simulator.greedy.KruskalVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "prims", "Prim's Algorithm", "Greedy Algorithms",
            "Visualize Prim's algorithm for finding Minimum Spanning Tree",
            "Greedy", "com.dsa.simulator.greedy.PrimsVisualizer"
        );

        // ==================== DYNAMIC PROGRAMMING ====================
        addVisualizationIfExists(visualizations,
            "coin-change", "Coin Change", "Dynamic Programming",
            "Visualize the coin change problem solving with dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "coin-change-demo", "Coin Change Demo", "Dynamic Programming",
            "Interactive demo of the coin change problem",
            "DP", "com.dsa.simulator.dynamicProgramming.CoinChangeVisualizerDemo"
        );

        addVisualizationIfExists(visualizations,
            "fibonacci", "Fibonacci Sequence", "Dynamic Programming",
            "Visualize Fibonacci sequence calculation using dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.FibonacciVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "knapsack", "0/1 Knapsack", "Dynamic Programming",
            "Visualize the 0/1 knapsack problem solving with dynamic programming",
            "DP", "com.dsa.simulator.dynamicProgramming.Knapsack01Visualizer"
        );

        addVisualizationIfExists(visualizations,
            "lcs", "Longest Common Subsequence", "Dynamic Programming",
            "Visualize finding the longest common subsequence between two strings",
            "DP", "com.dsa.simulator.dynamicProgramming.LongestCommonSubsequenceVisualizer"
        );

        addVisualizationIfExists(visualizations,
            "pascal-triangle", "Pascal's Triangle", "Dynamic Programming",
            "Visualize Pascal's triangle construction and patterns",
            "DP", "com.dsa.simulator.dynamicProgramming.PascalTriangleVisualizer"
        );

        // Debug: Print all loaded visualizations
        System.out.println("📋 Loaded " + visualizations.size() + " visualizations:");
        for (VisualizationData viz : visualizations) {
            boolean exists = checkClassExists(viz.getClassName());
            System.out.println("   " + (exists ? "✅" : "❌") + " " + viz.getName() + " -> " + viz.getClassName());
        }

        return visualizations;
    }

    private void addVisualizationIfExists(List<VisualizationData> visualizations, 
                                        String id, String name, String category, 
                                        String description, String type, String className) {
        if (checkClassExists(className)) {
            visualizations.add(new VisualizationData(id, name, category, description, type, className));
            System.out.println("✅ Added: " + name + " (" + className + ")");
        } else {
            System.out.println("❌ Skipped: " + name + " - Class not found: " + className);
        }
    }

    private boolean checkClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Class not found: " + className);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error checking class " + className + ": " + e.getMessage());
            return false;
        }
    }

    private int getWorkingVisualizationsCount(List<VisualizationData> visualizations) {
        int count = 0;
        for (VisualizationData viz : visualizations) {
            if (checkClassExists(viz.getClassName())) {
                count++;
            }
        }
        return count;
    }

    private void launchVisualization(VisualizationData visualization) {
        try {
            System.out.println("🚀 Attempting to launch visualization: " + visualization.getClassName());
            
            // Check if class exists first
            if (!checkClassExists(visualization.getClassName())) {
                showError("Class not found", 
                    "Visualization class not found: " + visualization.getClassName() + 
                    "\n\nPlease check:\n" +
                    "1. The class exists in your project\n" +
                    "2. The package path is correct\n" +
                    "3. The class extends Application\n" +
                    "4. The class is compiled");
                return;
            }
            
            // Use reflection to launch the visualization
            Class<?> vizClass = Class.forName(visualization.getClassName());
            Object instance = vizClass.getDeclaredConstructor().newInstance();
            
            if (instance instanceof javafx.application.Application) {
                javafx.application.Application vizApp = (javafx.application.Application) instance;
                
                // Create a new stage for the visualization
                Stage vizStage = new Stage();
                vizStage.setTitle(visualization.getName() + " - DSA Visualizer");
                
                // Start the visualization application
                System.out.println("✅ Starting visualization: " + visualization.getClassName());
                vizApp.start(vizStage);
                
            } else {
                showError("Invalid Visualization Class", 
                    "The class " + visualization.getClassName() + " does not extend Application.\n" +
                    "Please make sure your visualization class extends javafx.application.Application");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Failed to launch visualization: " + e.getMessage());
            e.printStackTrace();
            
            showError("Visualization Launch Failed", 
                "Failed to launch " + visualization.getName() + "\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "1. The class has a proper start() method\n" +
                "2. No missing dependencies\n" +
                "3. JavaFX is properly configured");
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    // Data class to hold visualization information
    public static class VisualizationData {
        private final String id;
        private final String name;
        private final String category;
        private final String description;
        private final String type;
        private final String className;

        public VisualizationData(String id, String name, String category, String description, String type, String className) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.type = type;
            this.className = className;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public String getClassName() { return className; }
    }
}
*/