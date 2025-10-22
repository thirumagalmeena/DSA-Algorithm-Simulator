package com.dsa.simulator.greedy;

import com.dsa.algorithms.greedy.TopologicalOrdering;
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

import java.util.*;

public class TopologicalOrderingVisualizer extends Application {
    private static final int NODE_RADIUS = 25;
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 400;
    
    private final TopologicalOrdering algo = new TopologicalOrdering();
    private List<List<Integer>> graph;
    private int numVertices = 6;
    
    private List<TopologicalOrdering.Step> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private List<Integer> topologicalOrder;
    
    private Circle[] nodeCircles;
    private Line[] edgeLines;
    private Text[] nodeLabels;
    private Text[] inDegreeLabels;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label verticesLabel;
    private Label orderLabel;
    private Label queueLabel;
    private Pane graphCanvas;
    private VBox infoPanel;
    private TextArea orderDisplay;
    private TextArea queueDisplay;
    private TextArea inDegreeDisplay;
    
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
    
    // Color definitions
    private final Color DEFAULT_NODE_COLOR = Color.web("#4fc3f7");
    private final Color PROCESSED_NODE_COLOR = Color.web("#66bb6a");
    private final Color QUEUE_NODE_COLOR = Color.web("#ffa726");
    private final Color DEFAULT_EDGE_COLOR = Color.web("#78909c");
    
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
        
        initializeDefaultGraph();
        
        HBox topInfoPanel = createTopInfoPanel();
        root.setTop(topInfoPanel);
        
        HBox visualizationArea = createVisualizationArea();
        root.setCenter(visualizationArea);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Topological Ordering - Kahn's Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Platform.runLater(() -> updateVisualization());
    }
    
    private void initializeDefaultGraph() {
        numVertices = 6;
        graph = new ArrayList<>();
        
        for (int i = 0; i < numVertices; i++) {
            graph.add(new ArrayList<>());
        }
        
        graph.get(0).add(1);
        graph.get(0).add(2);
        graph.get(1).add(3);
        graph.get(2).add(3);
        graph.get(2).add(4);
        graph.get(3).add(5);
        graph.get(4).add(5);
        
        initializeArrays();
    }
    
    private void initializeArrays() {
        nodeCircles = new Circle[numVertices];
        nodeLabels = new Text[numVertices];
        inDegreeLabels = new Text[numVertices];
        edgeLines = new Line[0];
    }
    
    private HBox createTopInfoPanel() {
        HBox topInfoPanel = new HBox(20);
        topInfoPanel.setPadding(new Insets(10, 15, 15, 15));
        topInfoPanel.setAlignment(Pos.CENTER_LEFT);
        topInfoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        verticesLabel = createStyledLabel("Vertices: " + numVertices);
        orderLabel = createStyledLabel("Topological Order: []");
        queueLabel = createStyledLabel("Queue: []");
        
        topInfoPanel.getChildren().addAll(statusLabel, stepLabel, verticesLabel, orderLabel, queueLabel);
        return topInfoPanel;
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
        
        VBox graphArea = new VBox(10);
        graphArea.setAlignment(Pos.TOP_CENTER);
        
        Label graphLabel = new Label("Graph Visualization");
        graphLabel.setTextFill(Color.WHITE);
        graphLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        graphCanvas = new Pane();
        graphCanvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        graphCanvas.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        
        graphArea.getChildren().addAll(graphLabel, graphCanvas);
        
        VBox infoArea = new VBox(10);
        infoArea.setAlignment(Pos.TOP_CENTER);
        
        Label infoLabel = new Label("Algorithm Information");
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        infoPanel = createInfoPanel();
        
        infoArea.getChildren().addAll(infoLabel, infoPanel);
        
        visualizationArea.getChildren().addAll(graphArea, infoArea);
        
        return visualizationArea;
    }
    
    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(15);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        infoPanel.setPrefWidth(350);
        
        VBox orderBox = new VBox(5);
        Label orderTitle = createStyledLabel("Topological Order:");
        orderDisplay = new TextArea();
        orderDisplay.setPrefHeight(80);
        orderDisplay.setEditable(false);
        orderDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        orderBox.getChildren().addAll(orderTitle, orderDisplay);
        
        VBox queueBox = new VBox(5);
        Label queueTitle = createStyledLabel("Current Queue:");
        queueDisplay = new TextArea();
        queueDisplay.setPrefHeight(80);
        queueDisplay.setEditable(false);
        queueDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        queueBox.getChildren().addAll(queueTitle, queueDisplay);
        
        VBox inDegreeBox = new VBox(5);
        Label inDegreeTitle = createStyledLabel("In-Degrees:");
        inDegreeDisplay = new TextArea();
        inDegreeDisplay.setPrefHeight(120);
        inDegreeDisplay.setEditable(false);
        inDegreeDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        inDegreeBox.getChildren().addAll(inDegreeTitle, inDegreeDisplay);
        
        infoPanel.getChildren().addAll(orderBox, queueBox, inDegreeBox);
        return infoPanel;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        VBox verticesControl = new VBox(5);
        verticesControl.setAlignment(Pos.CENTER);
        Label verticesInputLabel = createStyledLabel("Vertices:");
        verticesInputLabel.setTextFill(Color.LIGHTGRAY);
        
        verticesInput = new TextField(String.valueOf(numVertices));
        verticesInput.setPrefWidth(80);
        verticesInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        verticesControl.getChildren().addAll(verticesInputLabel, verticesInput);
        
        startComputeBtn = createStyledButton("Start Algorithm");
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
        updateInfoDisplays();
        
        if (graph == null) return;
        
        Point[] positions = calculateNodePositions();
        createEdges(positions);
        createNodesAndLabels(positions);
        
        if (steps != null && currentStep > 0 && currentStep <= steps.size()) {
            highlightCurrentStep(currentStep - 1);
        }
    }
    
    private Point[] calculateNodePositions() {
        Point[] positions = new Point[numVertices];
        double horizontalSpacing = CANVAS_WIDTH / (numVertices + 1);
        double verticalSpacing = CANVAS_HEIGHT / 4;
        
        for (int i = 0; i < numVertices; i++) {
            double x = horizontalSpacing * (i + 1);
            double y = 100 + (i % 3) * verticalSpacing;
            positions[i] = new Point(x, y);
        }
        
        return positions;
    }
    
    private void createEdges(Point[] positions) {
        int edgeCount = 0;
        for (int u = 0; u < numVertices; u++) {
            edgeCount += graph.get(u).size();
        }
        
        edgeLines = new Line[edgeCount];
        int edgeIndex = 0;
        
        for (int u = 0; u < numVertices; u++) {
            for (int v : graph.get(u)) {
                Line edge = new Line(positions[u].x, positions[u].y, positions[v].x, positions[v].y);
                edge.setStroke(DEFAULT_EDGE_COLOR);
                edge.setStrokeWidth(2);
                
                addArrowHead(edge, positions[u], positions[v]);
                
                edgeLines[edgeIndex] = edge;
                edgeIndex++;
                
                graphCanvas.getChildren().add(edge);
            }
        }
    }
    
    private void addArrowHead(Line edge, Point start, Point end) {
        double arrowLength = 10;
        double arrowWidth = 5;
        
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        if (length == 0) return;
        
        double unitDx = dx / length;
        double unitDy = dy / length;
        
        double arrowStartX = end.x - unitDx * NODE_RADIUS;
        double arrowStartY = end.y - unitDy * NODE_RADIUS;
        
        double perpX = -unitDy;
        double perpY = unitDx;
        
        double arrowTipX = arrowStartX;
        double arrowTipY = arrowStartY;
        double arrowLeftX = arrowStartX - unitDx * arrowLength + perpX * arrowWidth;
        double arrowLeftY = arrowStartY - unitDy * arrowLength + perpY * arrowWidth;
        double arrowRightX = arrowStartX - unitDx * arrowLength - perpX * arrowWidth;
        double arrowRightY = arrowStartY - unitDy * arrowLength - perpY * arrowWidth;
        
        Line leftLine = new Line(arrowTipX, arrowTipY, arrowLeftX, arrowLeftY);
        Line rightLine = new Line(arrowTipX, arrowTipY, arrowRightX, arrowRightY);
        leftLine.setStroke(DEFAULT_EDGE_COLOR);
        rightLine.setStroke(DEFAULT_EDGE_COLOR);
        leftLine.setStrokeWidth(2);
        rightLine.setStrokeWidth(2);
        
        graphCanvas.getChildren().addAll(leftLine, rightLine);
    }
    
    private void createNodesAndLabels(Point[] positions) {
        int[] initialInDegree = calculateInitialInDegrees();
        
        for (int i = 0; i < numVertices; i++) {
            Circle circle = new Circle(positions[i].x, positions[i].y, NODE_RADIUS);
            circle.setFill(DEFAULT_NODE_COLOR);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            Text label = new Text(positions[i].x - 5, positions[i].y + 5, String.valueOf(i));
            label.setFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            Text inDegreeLabel = new Text(positions[i].x - 8, positions[i].y - NODE_RADIUS - 5, 
                                         String.valueOf(initialInDegree[i]));
            inDegreeLabel.setFill(Color.YELLOW);
            inDegreeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            nodeCircles[i] = circle;
            nodeLabels[i] = label;
            inDegreeLabels[i] = inDegreeLabel;
            
            graphCanvas.getChildren().addAll(circle, label, inDegreeLabel);
        }
    }
    
    private void updateInfoDisplays() {
        if (graph == null) return;
        
        int[] currentInDegrees;
        
        if (steps != null && currentStep > 0) {
            TopologicalOrdering.Step currentStepData = steps.get(currentStep - 1);
            currentInDegrees = currentStepData.inDegree;
            
            orderDisplay.setText(currentStepData.order.toString());
            queueDisplay.setText(currentStepData.queueState.toString());
        } else {
            currentInDegrees = calculateInitialInDegrees();
            orderDisplay.setText("[]");
            queueDisplay.setText("[]");
        }
        
        StringBuilder inDegreeText = new StringBuilder();
        for (int i = 0; i < currentInDegrees.length; i++) {
            inDegreeText.append("Vertex ").append(i).append(": ").append(currentInDegrees[i]).append("\n");
        }
        inDegreeDisplay.setText(inDegreeText.toString());
        
        for (int i = 0; i < numVertices; i++) {
            if (inDegreeLabels[i] != null) {
                inDegreeLabels[i].setText(String.valueOf(currentInDegrees[i]));
            }
        }
    }
    
    private int[] calculateInitialInDegrees() {
        int[] inDegree = new int[numVertices];
        for (int u = 0; u < numVertices; u++) {
            for (int v : graph.get(u)) {
                inDegree[v]++;
            }
        }
        return inDegree;
    }
    
    private void highlightCurrentStep(int stepIndex) {
        if (stepIndex < 0 || stepIndex >= steps.size()) return;
        
        TopologicalOrdering.Step step = steps.get(stepIndex);
        
        for (int i = 0; i < numVertices; i++) {
            if (nodeCircles[i] != null) {
                nodeCircles[i].setFill(DEFAULT_NODE_COLOR);
            }
        }
        
        for (int i = 0; i < numVertices; i++) {
            if (nodeCircles[i] == null) continue;
            
            if (step.order.contains(i)) {
                nodeCircles[i].setFill(PROCESSED_NODE_COLOR);
            } else if (step.queueState.contains(i)) {
                nodeCircles[i].setFill(QUEUE_NODE_COLOR);
            }
        }
        
        statusLabel.setText(step.description);
        orderLabel.setText("Topological Order: " + step.order);
        queueLabel.setText("Queue: " + step.queueState);
        verticesLabel.setText("Vertices: " + numVertices);
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            if (numVertices <= 0 || numVertices > 15) {
                showAlert("Invalid Input", "Number of vertices must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of vertices.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing topological order...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateGraphBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        verticesInput.setDisable(true);

        if (graph == null || graph.size() != numVertices) {
            generateNewGraph();
        }

        topologicalOrder = algo.findTopologicalOrder(graph);
        steps = algo.getSteps();
        currentStep = 0;

        updateVisualization();
        
        verticesLabel.setText("Vertices: " + numVertices);

        sequentialTransition = new SequentialTransition();

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

        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                if (topologicalOrder.isEmpty()) {
                    statusLabel.setText("Status: Cycle detected - Graph is not a DAG!");
                    statusLabel.setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold;");
                } else {
                    statusLabel.setText("Status: Complete - Valid topological order found!");
                    statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                }
                
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

        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            if (numVertices <= 0 || numVertices > 15) {
                showAlert("Invalid Input", "Number of vertices must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of vertices.");
            return;
        }

        if (steps == null || currentStep == 0) {
            if (graph == null || graph.size() != numVertices) {
                generateNewGraph();
            }
            
            topologicalOrder = algo.findTopologicalOrder(graph);
            steps = algo.getSteps();
            currentStep = 0;
            verticesLabel.setText("Vertices: " + numVertices);
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateVisualization();
            
            if (currentStep == steps.size()) {
                if (topologicalOrder.isEmpty()) {
                    statusLabel.setText("Status: Cycle detected - Graph is not a DAG!");
                    statusLabel.setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold;");
                } else {
                    statusLabel.setText("Status: Complete - Valid topological order found!");
                    statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                }
            }
        }
    }
    
    private void generateNewGraph() {
        if (isComputing) return;
        
        try {
            numVertices = Integer.parseInt(verticesInput.getText().trim());
            if (numVertices <= 0 || numVertices > 15) {
                showAlert("Invalid Input", "Number of vertices must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of vertices.");
            return;
        }
        
        Random rand = new Random();
        graph = new ArrayList<>();
        
        for (int i = 0; i < numVertices; i++) {
            graph.add(new ArrayList<>());
        }
        
        int edgeCount = 0;
        int maxEdges = Math.max(1, numVertices * (numVertices - 1) / 4);
        
        while (edgeCount < maxEdges) {
            int u = rand.nextInt(numVertices);
            int v = rand.nextInt(numVertices);
            
            if (u < v && !graph.get(u).contains(v)) {
                if (rand.nextDouble() < 0.4) {
                    graph.get(u).add(v);
                    edgeCount++;
                }
            }
        }
        
        if (edgeCount == 0 && numVertices > 1) {
            for (int i = 0; i < numVertices - 1; i++) {
                graph.get(i).add(i + 1);
            }
        }
        
        initializeArrays();
        resetVisualization();
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        steps = null;
        currentStep = 0;
        topologicalOrder = null;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        verticesLabel.setText("Vertices: " + numVertices);
        orderLabel.setText("Topological Order: []");
        queueLabel.setText("Queue: []");
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
            statusLabel.setText("Status: Computing topological order...");
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