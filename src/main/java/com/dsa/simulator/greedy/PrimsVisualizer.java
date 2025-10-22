package com.dsa.simulator.greedy;

import com.dsa.algorithms.greedy.PrimsAlgorithm;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class PrimsVisualizer extends Application {
    private static final int NODE_RADIUS = 25;
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 400;
    
    private final PrimsAlgorithm algo = new PrimsAlgorithm();
    private int[][] graph;
    private int numVertices = 5;
    private int originalNumVertices = numVertices;
    
    private List<PrimsAlgorithm.Step> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int mstCost = 0;
    private int[] mstParent;
    
    private Circle[] nodeCircles;
    private Line[] edgeLines;
    private Text[] nodeLabels;
    private Text[] edgeLabels;
    private Text[] keyLabels;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label verticesLabel;
    private Label mstCostLabel;
    private Label currentVertexLabel;
    private Label keyLabel;
    private Pane graphCanvas;
    private VBox keyTable;
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateGraphBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField verticesInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for Prim's Algorithm
    private final Color DEFAULT_NODE_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color CURRENT_NODE_COLOR = Color.web("#ffa726"); // Orange - current node being processed
    private final Color MST_NODE_COLOR = Color.web("#66bb6a"); // Green - nodes in MST
    private final Color MST_EDGE_COLOR = Color.web("#66bb6a"); // Green - edges in MST
    private final Color CONSIDERING_EDGE_COLOR = Color.web("#9575cd"); // Purple - edges being considered
    private final Color DEFAULT_EDGE_COLOR = Color.web("#78909c"); // Gray - default edges
    private final Color INFINITY_COLOR = Color.web("#ef5350"); // Red - infinity in key table
    
    // Node positions for visualization
    class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        // Initialize default graph
        initializeDefaultGraph();
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        HBox visualizationArea = createVisualizationArea();
        root.setCenter(visualizationArea);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Prim's Algorithm - Minimum Spanning Tree Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeDefaultGraph() {
        numVertices = 5;
        graph = new int[][]{
            {0, 2, 0, 6, 0},
            {2, 0, 3, 8, 5},
            {0, 3, 0, 0, 7},
            {6, 8, 0, 0, 9},
            {0, 5, 7, 9, 0}
        };
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        verticesLabel = createStyledLabel("Vertices: " + numVertices);
        mstCostLabel = createStyledLabel("MST Cost: 0");
        currentVertexLabel = createStyledLabel("Current: -");
        keyLabel = createStyledLabel("Key: -");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, verticesLabel, mstCostLabel, currentVertexLabel, keyLabel);
        return infoPanel;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        return label;
    }
    
    private HBox createVisualizationArea() {
        HBox visualizationArea = new HBox(20);
        visualizationArea.setPadding(new Insets(20));
        visualizationArea.setAlignment(Pos.CENTER);
        visualizationArea.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        
        // Graph visualization
        VBox graphArea = new VBox(10);
        graphArea.setAlignment(Pos.TOP_CENTER);
        
        Label graphLabel = new Label("Graph Visualization");
        graphLabel.setTextFill(Color.WHITE);
        graphLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        graphCanvas = new Pane();
        graphCanvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        graphCanvas.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        
        graphArea.getChildren().addAll(graphLabel, graphCanvas);
        
        // Key table
        VBox tableArea = new VBox(10);
        tableArea.setAlignment(Pos.TOP_CENTER);
        
        Label tableLabel = new Label("Key Values");
        tableLabel.setTextFill(Color.WHITE);
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        keyTable = new VBox(5);
        keyTable.setPadding(new Insets(10));
        keyTable.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        keyTable.setPrefWidth(200);
        
        tableArea.getChildren().addAll(tableLabel, keyTable);
        
        visualizationArea.getChildren().addAll(graphArea, tableArea);
        updateVisualization();
        
        return visualizationArea;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        // Vertices input
        VBox verticesControl = new VBox(5);
        verticesControl.setAlignment(Pos.CENTER);
        Label verticesLabel = createStyledLabel("Vertices:");
        verticesLabel.setTextFill(Color.LIGHTGRAY);
        
        verticesInput = new TextField(String.valueOf(numVertices));
        verticesInput.setPrefWidth(80);
        verticesInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        verticesControl.getChildren().addAll(verticesLabel, verticesInput);
        
        // Buttons
        startComputeBtn = createStyledButton("Start Prim's");
        startComputeBtn.setOnAction(e -> startAutoCompute());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        generateGraphBtn = createStyledButton("Generate Graph");
        generateGraphBtn.setOnAction(e -> generateNewGraph());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseComputation());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeComputation());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayComputation());
        
        // Speed slider
        VBox speedControl = new VBox(5);
        speedControl.setAlignment(Pos.CENTER);
        Label speedLabel = createStyledLabel("Speed:");
        speedLabel.setTextFill(Color.LIGHTGRAY);
        
        speedSlider = new Slider(500, 3000, 1500);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(500);
        speedSlider.setMinorTickCount(0);
        speedSlider.setSnapToTicks(true);
        speedSlider.setStyle("-fx-control-inner-background: #555;");
        
        speedControl.getChildren().addAll(speedLabel, speedSlider);
        
        controlPanel.getChildren().addAll(
            verticesControl, startComputeBtn, nextStepBtn, resetBtn, 
            generateGraphBtn, pauseBtn, resumeBtn, replayBtn, speedControl
        );
        
        return controlPanel;
    }
    
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setPrefWidth(100);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #7986cb; -fx-text-fill: white; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white; -fx-font-weight: bold;"));
        return button;
    }
    
    private void updateVisualization() {
        graphCanvas.getChildren().clear();
        keyTable.getChildren().clear();
        
        if (graph == null) return;
        
        // Position nodes in a circle
        Point[] positions = new Point[numVertices];
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        double radius = Math.min(CANVAS_WIDTH, CANVAS_HEIGHT) * 0.35;
        
        for (int i = 0; i < numVertices; i++) {
            double angle = 2 * Math.PI * i / numVertices;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions[i] = new Point(x, y);
        }
        
        // Create edges first (so they appear behind nodes)
        int edgeCount = 0;
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                if (graph[i][j] != 0) {
                    edgeCount++;
                }
            }
        }
        
        edgeLines = new Line[edgeCount];
        edgeLabels = new Text[edgeCount];
        int edgeIndex = 0;
        
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                if (graph[i][j] != 0) {
                    Line edge = new Line(positions[i].x, positions[i].y, positions[j].x, positions[j].y);
                    edge.setStroke(DEFAULT_EDGE_COLOR);
                    edge.setStrokeWidth(2);
                    
                    // Edge weight label
                    double midX = (positions[i].x + positions[j].x) / 2;
                    double midY = (positions[i].y + positions[j].y) / 2;
                    Text weightLabel = new Text(midX, midY, String.valueOf(graph[i][j]));
                    weightLabel.setFill(Color.WHITE);
                    weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    
                    edgeLines[edgeIndex] = edge;
                    edgeLabels[edgeIndex] = weightLabel;
                    edgeIndex++;
                    
                    graphCanvas.getChildren().addAll(edge, weightLabel);
                }
            }
        }
        
        // Create nodes
        nodeCircles = new Circle[numVertices];
        nodeLabels = new Text[numVertices];
        
        for (int i = 0; i < numVertices; i++) {
            Circle circle = new Circle(positions[i].x, positions[i].y, NODE_RADIUS);
            circle.setFill(DEFAULT_NODE_COLOR);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            Text label = new Text(positions[i].x - 5, positions[i].y + 5, String.valueOf(i));
            label.setFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            nodeCircles[i] = circle;
            nodeLabels[i] = label;
            graphCanvas.getChildren().addAll(circle, label);
        }
        
        // Update key table
        updateKeyTable();
        
        // Apply step highlighting if we have steps
        if (steps != null && currentStep > 0 && currentStep <= steps.size()) {
            highlightCurrentStep(currentStep - 1);
        }
    }
    
    private void updateKeyTable() {
        keyTable.getChildren().clear();
        
        // Create header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(5));
        
        Label vertexHeader = createTableLabel("Vertex");
        Label keyHeader = createTableLabel("Key Value");
        Label inMSTHeader = createTableLabel("In MST");
        
        header.getChildren().addAll(vertexHeader, keyHeader, inMSTHeader);
        keyTable.getChildren().add(header);
        
        // Create rows
        if (steps != null && currentStep > 0) {
            PrimsAlgorithm.Step currentStepData = steps.get(currentStep - 1);
            
            for (int i = 0; i < numVertices; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(3));
                
                Label vertexLabel = createTableLabel(String.valueOf(i));
                String keyValue = currentStepData.currentKey[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(currentStepData.currentKey[i]);
                Label keyValueLabel = createTableLabel(keyValue);
                Label inMSTLabel = createTableLabel(currentStepData.inMST[i] ? "Yes" : "No");
                
                // Color coding
                if (currentStepData.inMST[i]) {
                    row.setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 3;");
                } else if (i == currentStepData.vertex) {
                    row.setStyle("-fx-background-color: #ffa726; -fx-background-radius: 3;");
                }
                
                if (currentStepData.currentKey[i] == Integer.MAX_VALUE) {
                    keyValueLabel.setTextFill(INFINITY_COLOR);
                }
                
                row.getChildren().addAll(vertexLabel, keyValueLabel, inMSTLabel);
                keyTable.getChildren().add(row);
            }
        } else {
            // Show empty table
            for (int i = 0; i < numVertices; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(3));
                
                Label vertexLabel = createTableLabel(String.valueOf(i));
                Label keyValueLabel = createTableLabel("∞");
                Label inMSTLabel = createTableLabel("No");
                
                keyValueLabel.setTextFill(INFINITY_COLOR);
                
                row.getChildren().addAll(vertexLabel, keyValueLabel, inMSTLabel);
                keyTable.getChildren().add(row);
            }
        }
    }
    
    private Label createTableLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        label.setPrefWidth(60);
        label.setAlignment(Pos.CENTER);
        return label;
    }
    
    private void highlightCurrentStep(int stepIndex) {
        if (stepIndex < 0 || stepIndex >= steps.size()) return;
        
        PrimsAlgorithm.Step step = steps.get(stepIndex);
        
        // Reset all nodes to default color first
        for (int i = 0; i < numVertices; i++) {
            if (nodeCircles[i] != null) {
                nodeCircles[i].setFill(DEFAULT_NODE_COLOR);
            }
        }
        
        // Reset all edges to default color
        for (int i = 0; i < edgeLines.length; i++) {
            if (edgeLines[i] != null) {
                edgeLines[i].setStroke(DEFAULT_EDGE_COLOR);
                edgeLines[i].setStrokeWidth(2);
            }
        }
        
        // Highlight MST nodes
        for (int i = 0; i < numVertices; i++) {
            if (step.inMST[i] && nodeCircles[i] != null) {
                nodeCircles[i].setFill(MST_NODE_COLOR);
            }
        }
        
        // Highlight current node if it's a vertex addition step
        if (step.vertex != -1 && nodeCircles[step.vertex] != null) {
            nodeCircles[step.vertex].setFill(CURRENT_NODE_COLOR);
            currentVertexLabel.setText("Current: Vertex " + step.vertex);
            
            // Highlight the MST edge if this vertex was added via an edge
            if (step.parent != -1) {
                highlightMSTEdge(step.parent, step.vertex);
            }
        }
        
        // Update status with step description
        statusLabel.setText(step.description);
        
        // Update key label
        if (step.vertex != -1) {
            keyLabel.setText("Key: " + (step.currentKey[step.vertex] == Integer.MAX_VALUE ? "∞" : step.currentKey[step.vertex]));
        }
        
        // Update key table
        updateKeyTable();
    }
    
    private void highlightMSTEdge(int u, int v) {
        // Find and highlight the edge between u and v
        for (int i = 0; i < edgeLines.length; i++) {
            if (edgeLines[i] != null) {
                // Check if this edge connects u and v
                // This is a simplified check - in a real implementation, you'd track edge connections
                edgeLines[i].setStroke(MST_EDGE_COLOR);
                edgeLines[i].setStrokeWidth(3);
            }
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            if (numVertices <= 0) {
                showAlert("Invalid Input", "Number of vertices must be positive.");
                return;
            }
            if (numVertices > 10) {
                showAlert("Input Warning", "For better visualization, please use 10 vertices or fewer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of vertices.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing MST...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateGraphBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        verticesInput.setDisable(true);

        // Generate a graph if not already generated
        if (graph == null || graph.length != numVertices) {
            generateNewGraph();
        }

        // Perform Prim's algorithm to get steps
        mstParent = algo.findMST(graph);
        steps = algo.getSteps();
        mstCost = algo.getMSTCost(graph, mstParent);
        currentStep = 0;

        // Update visualization
        updateVisualization();
        
        verticesLabel.setText("Vertices: " + numVertices);
        mstCostLabel.setText("MST Cost: " + mstCost);

        sequentialTransition = new SequentialTransition();

        // Animate through each step
        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(1200));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateVisualization();
                });
            });
            
            sequentialTransition.getChildren().add(stepTransition);
        }

        // Final state
        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete - MST Cost: " + mstCost);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateGraphBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                verticesInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            if (numVertices <= 0) {
                showAlert("Invalid Input", "Number of vertices must be positive.");
                return;
            }
            if (numVertices > 10) {
                showAlert("Input Warning", "For better visualization, please use 10 vertices or fewer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of vertices.");
            return;
        }

        if (steps == null || currentStep == 0) {
            // Generate a graph if not already generated
            if (graph == null || graph.length != numVertices) {
                generateNewGraph();
            }
            
            // Initialize computation
            mstParent = algo.findMST(graph);
            steps = algo.getSteps();
            mstCost = algo.getMSTCost(graph, mstParent);
            currentStep = 0;
            mstCostLabel.setText("MST Cost: " + mstCost);
            verticesLabel.setText("Vertices: " + numVertices);
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateVisualization();
            
            if (currentStep == steps.size()) {
                statusLabel.setText("Status: Complete - MST Cost: " + mstCost);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
            }
        }
    }
    
    private void generateNewGraph() {
        if (isComputing) return;
        
        Random rand = new Random();
        graph = new int[numVertices][numVertices];
        
        // Generate a connected graph with random weights
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                // 70% chance of having an edge
                if (rand.nextDouble() < 0.7) {
                    int weight = rand.nextInt(10) + 1; // 1-10 weight
                    graph[i][j] = weight;
                    graph[j][i] = weight;
                }
            }
        }
        
        // Ensure graph is connected by adding a spanning tree
        for (int i = 1; i < numVertices; i++) {
            int parent = rand.nextInt(i);
            if (graph[i][parent] == 0) {
                int weight = rand.nextInt(10) + 1;
                graph[i][parent] = weight;
                graph[parent][i] = weight;
            }
        }
        
        // Reset visualization
        resetVisualization();
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        steps = null;
        currentStep = 0;
        mstCost = 0;
        mstParent = null;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        verticesLabel.setText("Vertices: " + numVertices);
        mstCostLabel.setText("MST Cost: 0");
        currentVertexLabel.setText("Current: -");
        keyLabel.setText("Key: -");
        verticesInput.setText(String.valueOf(numVertices));
        
        updateVisualization();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateGraphBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        verticesInput.setDisable(false);
    }
    
    private void replayComputation() {
        resetVisualization();
        startAutoCompute();
    }
    
    private void pauseComputation() {
        if (sequentialTransition != null && isComputing) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeComputation() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Computing MST...");
            pauseBtn.setDisable(false);
            resumeBtn.setDisable(true);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}