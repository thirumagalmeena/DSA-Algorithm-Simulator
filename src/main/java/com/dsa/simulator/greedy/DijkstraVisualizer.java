package com.dsa.simulator.greedy;

import com.dsa.algorithms.greedy.DijkstraAlgorithm;
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

public class DijkstraVisualizer extends Application {
    private static final int NODE_RADIUS = 25;
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 400;
    
    private final DijkstraAlgorithm algo = new DijkstraAlgorithm();
    private int[][] graph;
    private int numVertices = 5;
    private int sourceVertex = 0;
    
    private List<DijkstraAlgorithm.Step> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int[] shortestDistances;
    
    private Circle[] nodeCircles;
    private Line[] edgeLines;
    private Text[] nodeLabels;
    private Text[] edgeLabels;
    private Text[] distanceLabels;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label verticesLabel;
    private Label sourceLabel;
    private Label currentVertexLabel;
    private Label distanceTableLabel;
    private Pane graphCanvas;
    private VBox distanceTable;
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateGraphBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField verticesInput;
    private TextField sourceInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for Dijkstra's Algorithm
    private final Color DEFAULT_NODE_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color SOURCE_NODE_COLOR = Color.web("#ffa726"); // Orange - source node
    private final Color PROCESSED_NODE_COLOR = Color.web("#66bb6a"); // Green - processed nodes
    private final Color CURRENT_NODE_COLOR = Color.web("#ff7043"); // Deep Orange - current node being processed
    private final Color UNREACHABLE_NODE_COLOR = Color.web("#78909c"); // Gray - unreachable nodes
    private final Color RELAXED_EDGE_COLOR = Color.web("#66bb6a"); // Green - relaxed edges
    private final Color CONSIDERING_EDGE_COLOR = Color.web("#ffa726"); // Orange - edges being considered
    private final Color DEFAULT_EDGE_COLOR = Color.web("#78909c"); // Gray - default edges
    private final Color HEADER_COLOR = Color.web("#9575cd"); // Purple - headers
    
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
        primaryStage.setTitle("Dijkstra's Algorithm - Shortest Path Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeDefaultGraph() {
        numVertices = 5;
        graph = new int[][]{
            {0, 4, 0, 0, 0},
            {4, 0, 8, 0, 0},
            {0, 8, 0, 7, 4},
            {0, 0, 7, 0, 14},
            {0, 0, 4, 14, 0}
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
        sourceLabel = createStyledLabel("Source: " + sourceVertex);
        currentVertexLabel = createStyledLabel("Current Vertex: -");
        distanceTableLabel = createStyledLabel("Shortest Distances: -");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, verticesLabel, sourceLabel, currentVertexLabel, distanceTableLabel);
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
        
        // Distance table
        VBox tableArea = new VBox(10);
        tableArea.setAlignment(Pos.TOP_CENTER);
        
        Label tableLabel = new Label("Distance Table");
        tableLabel.setTextFill(Color.WHITE);
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        distanceTable = new VBox(5);
        distanceTable.setPadding(new Insets(10));
        distanceTable.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        distanceTable.setPrefWidth(200);
        
        tableArea.getChildren().addAll(tableLabel, distanceTable);
        
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
        
        // Source input
        VBox sourceControl = new VBox(5);
        sourceControl.setAlignment(Pos.CENTER);
        Label sourceInputLabel = createStyledLabel("Source:");
        sourceInputLabel.setTextFill(Color.LIGHTGRAY);
        
        sourceInput = new TextField(String.valueOf(sourceVertex));
        sourceInput.setPrefWidth(80);
        sourceInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        sourceControl.getChildren().addAll(sourceInputLabel, sourceInput);
        
        // Buttons
        startComputeBtn = createStyledButton("Start Dijkstra");
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
            verticesControl, sourceControl, startComputeBtn, nextStepBtn, resetBtn, 
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
        distanceTable.getChildren().clear();
        
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
        
        // Create nodes and distance labels
        nodeCircles = new Circle[numVertices];
        nodeLabels = new Text[numVertices];
        distanceLabels = new Text[numVertices];
        
        for (int i = 0; i < numVertices; i++) {
            Circle circle = new Circle(positions[i].x, positions[i].y, NODE_RADIUS);
            
            // Set node color based on state
            if (i == sourceVertex) {
                circle.setFill(SOURCE_NODE_COLOR);
            } else {
                circle.setFill(DEFAULT_NODE_COLOR);
            }
            
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            // Node label
            Text label = new Text(positions[i].x - 5, positions[i].y + 5, String.valueOf(i));
            label.setFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            // Distance label (above the node)
            Text distanceLabel = new Text(positions[i].x - 15, positions[i].y - NODE_RADIUS - 10, "∞");
            distanceLabel.setFill(Color.YELLOW);
            distanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            nodeCircles[i] = circle;
            nodeLabels[i] = label;
            distanceLabels[i] = distanceLabel;
            
            graphCanvas.getChildren().addAll(circle, label, distanceLabel);
        }
        
        // Update distance table
        updateDistanceTable();
        
        // Apply step highlighting if we have steps
        if (steps != null && currentStep > 0 && currentStep <= steps.size()) {
            highlightCurrentStep(currentStep - 1);
        }
    }
    
    private void updateDistanceTable() {
        distanceTable.getChildren().clear();
        
        // Create header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(5));
        header.setStyle("-fx-background-color: " + toHex(HEADER_COLOR) + "; -fx-background-radius: 3;");
        
        Label vertexHeader = createTableLabel("Vertex");
        Label distanceHeader = createTableLabel("Distance");
        Label visitedHeader = createTableLabel("Visited");
        
        header.getChildren().addAll(vertexHeader, distanceHeader, visitedHeader);
        distanceTable.getChildren().add(header);
        
        // Create rows
        if (steps != null && currentStep > 0) {
            DijkstraAlgorithm.Step currentStepData = steps.get(currentStep - 1);
            
            for (int i = 0; i < numVertices; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(3));
                
                String distanceStr = currentStepData.distance[i] == Integer.MAX_VALUE ? 
                    "∞" : String.valueOf(currentStepData.distance[i]);
                
                Label vertexLabel = createTableLabel(String.valueOf(i));
                Label distanceLabel = createTableLabel(distanceStr);
                Label visitedLabel = createTableLabel(currentStepData.visited[i] ? "✓" : "✗");
                
                // Highlight current vertex
                if (i == currentStepData.currentVertex) {
                    row.setStyle("-fx-background-color: #ff7043; -fx-background-radius: 3;");
                }
                // Highlight visited vertices
                else if (currentStepData.visited[i]) {
                    row.setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 3;");
                }
                // Highlight source vertex
                else if (i == sourceVertex) {
                    row.setStyle("-fx-background-color: #ffa726; -fx-background-radius: 3;");
                }
                
                row.getChildren().addAll(vertexLabel, distanceLabel, visitedLabel);
                distanceTable.getChildren().add(row);
            }
        } else {
            // Show initial distance state
            for (int i = 0; i < numVertices; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(3));
                
                String distanceStr = (i == sourceVertex) ? "0" : "∞";
                
                Label vertexLabel = createTableLabel(String.valueOf(i));
                Label distanceLabel = createTableLabel(distanceStr);
                Label visitedLabel = createTableLabel("✗");
                
                // Highlight source vertex
                if (i == sourceVertex) {
                    row.setStyle("-fx-background-color: #ffa726; -fx-background-radius: 3;");
                }
                
                row.getChildren().addAll(vertexLabel, distanceLabel, visitedLabel);
                distanceTable.getChildren().add(row);
            }
        }
    }
    
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
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
        
        DijkstraAlgorithm.Step step = steps.get(stepIndex);
        
        // Reset all edges to default color first
        for (int i = 0; i < edgeLines.length; i++) {
            if (edgeLines[i] != null) {
                edgeLines[i].setStroke(DEFAULT_EDGE_COLOR);
                edgeLines[i].setStrokeWidth(2);
            }
        }
        
        // Update node colors and distance labels
        for (int i = 0; i < numVertices; i++) {
            // Update distance labels
            String distanceStr = step.distance[i] == Integer.MAX_VALUE ? 
                "∞" : String.valueOf(step.distance[i]);
            distanceLabels[i].setText(distanceStr);
            
            // Update node colors
            if (step.currentVertex == i && step.currentVertex != -1) {
                nodeCircles[i].setFill(CURRENT_NODE_COLOR);
            } else if (step.visited[i]) {
                if (step.distance[i] == Integer.MAX_VALUE) {
                    nodeCircles[i].setFill(UNREACHABLE_NODE_COLOR);
                } else {
                    nodeCircles[i].setFill(PROCESSED_NODE_COLOR);
                }
            } else if (i == sourceVertex) {
                nodeCircles[i].setFill(SOURCE_NODE_COLOR);
            } else {
                nodeCircles[i].setFill(DEFAULT_NODE_COLOR);
            }
        }
        
        // Highlight edges being relaxed
        if (step.currentVertex != -1) {
            highlightRelaxedEdges(step);
        }
        
        // Update labels
        if (step.currentVertex == -1) {
            currentVertexLabel.setText("Current Vertex: - (Finalizing)");
        } else {
            currentVertexLabel.setText("Current Vertex: " + step.currentVertex);
        }
        statusLabel.setText(step.description);
        
        // Update distance table
        updateDistanceTable();
    }
    
    private void highlightRelaxedEdges(DijkstraAlgorithm.Step step) {
        int u = step.currentVertex;
        
        for (int v = 0; v < numVertices; v++) {
            if (graph[u][v] != 0 && !step.visited[v] && step.distance[u] != Integer.MAX_VALUE) {
                // Find and highlight the edge between u and v
                for (int i = 0; i < edgeLines.length; i++) {
                    if (edgeLines[i] != null) {
                        // Check if this edge connects u and v (simplified)
                        edgeLines[i].setStroke(CONSIDERING_EDGE_COLOR);
                        edgeLines[i].setStrokeWidth(3);
                    }
                }
            }
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            sourceVertex = Integer.parseInt(sourceInput.getText().trim());
            
            if (numVertices <= 0) {
                showAlert("Invalid Input", "Number of vertices must be positive.");
                return;
            }
            if (numVertices > 10) {
                showAlert("Input Warning", "For better visualization, please use 10 vertices or fewer.");
                return;
            }
            if (sourceVertex < 0 || sourceVertex >= numVertices) {
                showAlert("Invalid Input", "Source vertex must be between 0 and " + (numVertices - 1));
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for vertices and source.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing shortest paths...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateGraphBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        verticesInput.setDisable(true);
        sourceInput.setDisable(true);

        // Generate a graph if not already generated
        if (graph == null || graph.length != numVertices) {
            generateNewGraph();
        }

        // Perform Dijkstra's algorithm to get steps
        shortestDistances = algo.findShortestPaths(graph, sourceVertex);
        steps = algo.getSteps();
        currentStep = 0;

        // Update visualization
        updateVisualization();
        
        verticesLabel.setText("Vertices: " + numVertices);
        sourceLabel.setText("Source: " + sourceVertex);

        sequentialTransition = new SequentialTransition();

        // Animate through each step
        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(speedSlider.getValue()));
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
                // Build final distances string
                StringBuilder distances = new StringBuilder("Shortest Distances: [");
                for (int i = 0; i < shortestDistances.length; i++) {
                    if (shortestDistances[i] == Integer.MAX_VALUE) {
                        distances.append("∞");
                    } else {
                        distances.append(shortestDistances[i]);
                    }
                    if (i < shortestDistances.length - 1) {
                        distances.append(", ");
                    }
                }
                distances.append("]");
                
                statusLabel.setText("Status: Complete - All vertices processed");
                distanceTableLabel.setText(distances.toString());
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateGraphBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                verticesInput.setDisable(false);
                sourceInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            sourceVertex = Integer.parseInt(sourceInput.getText().trim());
            
            if (numVertices <= 0) {
                showAlert("Invalid Input", "Number of vertices must be positive.");
                return;
            }
            if (numVertices > 10) {
                showAlert("Input Warning", "For better visualization, please use 10 vertices or fewer.");
                return;
            }
            if (sourceVertex < 0 || sourceVertex >= numVertices) {
                showAlert("Invalid Input", "Source vertex must be between 0 and " + (numVertices - 1));
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for vertices and source.");
            return;
        }

        if (steps == null || currentStep == 0) {
            // Generate a graph if not already generated
            if (graph == null || graph.length != numVertices) {
                generateNewGraph();
            }
            
            // Initialize computation
            shortestDistances = algo.findShortestPaths(graph, sourceVertex);
            steps = algo.getSteps();
            currentStep = 0;
            verticesLabel.setText("Vertices: " + numVertices);
            sourceLabel.setText("Source: " + sourceVertex);
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateVisualization();
            
            if (currentStep == steps.size()) {
                // Build final distances string
                StringBuilder distances = new StringBuilder("Shortest Distances: [");
                for (int i = 0; i < shortestDistances.length; i++) {
                    if (shortestDistances[i] == Integer.MAX_VALUE) {
                        distances.append("∞");
                    } else {
                        distances.append(shortestDistances[i]);
                    }
                    if (i < shortestDistances.length - 1) {
                        distances.append(", ");
                    }
                }
                distances.append("]");
                
                statusLabel.setText("Status: Complete - All vertices processed");
                distanceTableLabel.setText(distances.toString());
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
                // 60% chance of having an edge
                if (rand.nextDouble() < 0.6) {
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
        shortestDistances = null;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        verticesLabel.setText("Vertices: " + numVertices);
        sourceLabel.setText("Source: " + sourceVertex);
        currentVertexLabel.setText("Current Vertex: -");
        distanceTableLabel.setText("Shortest Distances: -");
        verticesInput.setText(String.valueOf(numVertices));
        sourceInput.setText(String.valueOf(sourceVertex));
        
        updateVisualization();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateGraphBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        verticesInput.setDisable(false);
        sourceInput.setDisable(false);
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
            statusLabel.setText("Status: Computing shortest paths...");
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