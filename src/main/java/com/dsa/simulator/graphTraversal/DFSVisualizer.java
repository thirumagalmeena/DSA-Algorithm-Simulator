package com.dsa.simulator.graphTraversal;

import com.dsa.algorithms.graphTraversal.DFS;
import com.dsa.algorithms.graphTraversal.GraphTraversable;
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

public class DFSVisualizer extends Application {
    private static final int NODE_RADIUS = 30;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 500;
    
    private Map<Integer, List<Integer>> graph;
    private Map<Integer, Point> nodePositions;
    private List<String> traversalSteps;
    private int currentStep = 0;
    private boolean isTraversing = false;
    private boolean isPaused = false;
    private int startNode = 0;
    
    // State for manual step-by-step
    private Stack<Integer> manualStack;
    private Set<Integer> manualVisited;
    private List<Integer> currentStackState;
    private List<Integer> currentVisitedState;
    private Integer currentNode;
    
    private Map<Integer, Circle> nodeCircles;
    private Map<String, Line> edgeLines;
    private List<Circle> visitedNodes = new ArrayList<>();
    private List<Circle> stackNodes = new ArrayList<>();
    private List<Line> exploredEdges = new ArrayList<>();
    
    private Label statusLabel;
    private Label stepLabel;
    private Label stackLabel;
    private Label visitedLabel;
    private Label currentLabel;
    private Pane graphCanvas;
    private Button startTraversalBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateGraphBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField startNodeInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for DFS
    private final Color DEFAULT_NODE_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color START_NODE_COLOR = Color.web("#ffa726"); // Orange
    private final Color CURRENT_NODE_COLOR = Color.web("#ef5350"); // Red - currently processing
    private final Color VISITED_NODE_COLOR = Color.web("#66bb6a"); // Green - visited
    private final Color STACK_NODE_COLOR = Color.web("#9575cd"); // Purple - in stack
    private final Color DEFAULT_EDGE_COLOR = Color.web("#78909c"); // Gray
    private final Color EXPLORED_EDGE_COLOR = Color.web("#ffa726"); // Orange
    
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
        
        graphCanvas = new Pane();
        graphCanvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        graphCanvas.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        drawGraph();
        
        ScrollPane scrollPane = new ScrollPane(graphCanvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        root.setCenter(scrollPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("DFS Graph Traversal Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeDefaultGraph() {
        graph = new HashMap<>();
        // Create a sample graph
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(0, 3, 4));
        graph.put(2, Arrays.asList(0, 5, 6));
        graph.put(3, Arrays.asList(1));
        graph.put(4, Arrays.asList(1, 7));
        graph.put(5, Arrays.asList(2));
        graph.put(6, Arrays.asList(2, 8));
        graph.put(7, Arrays.asList(4));
        graph.put(8, Arrays.asList(6));
        
        // Position nodes in a tree-like structure
        nodePositions = new HashMap<>();
        nodePositions.put(0, new Point(400, 50));
        nodePositions.put(1, new Point(250, 150));
        nodePositions.put(2, new Point(550, 150));
        nodePositions.put(3, new Point(150, 250));
        nodePositions.put(4, new Point(350, 250));
        nodePositions.put(5, new Point(450, 250));
        nodePositions.put(6, new Point(650, 250));
        nodePositions.put(7, new Point(350, 350));
        nodePositions.put(8, new Point(650, 350));
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        stackLabel = createStyledLabel("Stack: []");
        visitedLabel = createStyledLabel("Visited: []");
        currentLabel = createStyledLabel("Current: -");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, stackLabel, visitedLabel, currentLabel);
        return infoPanel;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        return label;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        // Start node input
        VBox startNodeControl = new VBox(5);
        startNodeControl.setAlignment(Pos.CENTER);
        Label startNodeLabel = createStyledLabel("Start Node:");
        startNodeLabel.setTextFill(Color.LIGHTGRAY);
        
        startNodeInput = new TextField(String.valueOf(startNode));
        startNodeInput.setPrefWidth(60);
        startNodeInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        startNodeControl.getChildren().addAll(startNodeLabel, startNodeInput);
        
        // Buttons
        startTraversalBtn = createStyledButton("Start DFS");
        startTraversalBtn.setOnAction(e -> startAutoTraversal());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        generateGraphBtn = createStyledButton("Generate Graph");
        generateGraphBtn.setOnAction(e -> generateNewGraph());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseTraversal());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeTraversal());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayTraversal());
        
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
            startNodeControl, startTraversalBtn, nextStepBtn, resetBtn, 
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
    
    private void drawGraph() {
        graphCanvas.getChildren().clear();
        nodeCircles = new HashMap<>();
        edgeLines = new HashMap<>();
        visitedNodes.clear();
        stackNodes.clear();
        exploredEdges.clear();
        
        // Draw edges first (so they appear behind nodes)
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            int from = entry.getKey();
            Point fromPos = nodePositions.get(from);
            
            for (int to : entry.getValue()) {
                Point toPos = nodePositions.get(to);
                
                // Only draw each edge once
                String edgeKey = Math.min(from, to) + "-" + Math.max(from, to);
                if (!edgeLines.containsKey(edgeKey)) {
                    Line edge = new Line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                    edge.setStroke(DEFAULT_EDGE_COLOR);
                    edge.setStrokeWidth(2);
                    edgeLines.put(edgeKey, edge);
                    graphCanvas.getChildren().add(edge);
                }
            }
        }
        
        // Draw nodes
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int node = entry.getKey();
            Point pos = entry.getValue();
            
            Circle circle = new Circle(pos.x, pos.y, NODE_RADIUS);
            circle.setFill(DEFAULT_NODE_COLOR);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            // Add node label
            Text label = new Text(pos.x - 5, pos.y + 5, String.valueOf(node));
            label.setFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            nodeCircles.put(node, circle);
            graphCanvas.getChildren().addAll(circle, label);
        }
    }
    
    private void startAutoTraversal() {
        if (isTraversing) return;
        
        try {
            startNode = Integer.parseInt(startNodeInput.getText().trim());
            if (!graph.containsKey(startNode)) {
                showAlert("Invalid Node", "Start node must exist in the graph.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for start node.");
            return;
        }

        isTraversing = true;
        isPaused = false;
        statusLabel.setText("Status: Traversing...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startTraversalBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateGraphBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        startNodeInput.setDisable(true);

        // Perform DFS to get steps
        GraphTraversable dfs = new DFS(graph);
        dfs.traverse(startNode);
        traversalSteps = dfs.getTraversalSteps();
        currentStep = 0;

        // Reset visualization
        resetGraphColors();
        
        // Highlight start node
        Circle startCircle = nodeCircles.get(startNode);
        if (startCircle != null) {
            startCircle.setFill(START_NODE_COLOR);
        }

        sequentialTransition = new SequentialTransition();

        // Simulate DFS steps
        Stack<Integer> stack = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        stack.push(startNode);
        visited.add(startNode);
        
        List<Integer> currentStack = new ArrayList<>(stack);
        List<Integer> currentVisited = new ArrayList<>(visited);

        for (int step = 0; step < traversalSteps.size(); step++) {
            final int currentStepIndex = step;
            final String stepDescription = traversalSteps.get(step);
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(800));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    processStep(stepDescription, stack, visited, currentStack, currentVisited);
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateInfoLabels(currentStack, currentVisited, getCurrentNodeFromStep(stepDescription));
                });
            });
            
            sequentialTransition.getChildren().add(stepTransition);
        }

        // Final state
        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                
                isTraversing = false;
                startTraversalBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateGraphBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                startNodeInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void processStep(String stepDescription, Stack<Integer> stack, Set<Integer> visited, 
                           List<Integer> currentStack, List<Integer> currentVisited) {
        if (stepDescription.startsWith("Visited:")) {
            int node = Integer.parseInt(stepDescription.split(": ")[1]);
            
            // Mark node as visited
            Circle circle = nodeCircles.get(node);
            if (circle != null) {
                circle.setFill(VISITED_NODE_COLOR);
                visitedNodes.add(circle);
            }
            
            // Remove from stack
            stack.pop();
            currentStack.remove((Integer) node);
            
        } else if (stepDescription.startsWith("Going deeper to:")) {
            int node = Integer.parseInt(stepDescription.split(": ")[1]);
            
            // Add to stack and mark as in stack
            stack.push(node);
            visited.add(node);
            currentStack.add(node);
            currentVisited.add(node);
            
            Circle circle = nodeCircles.get(node);
            if (circle != null) {
                circle.setFill(STACK_NODE_COLOR);
                stackNodes.add(circle);
            }
            
            // Highlight edge from current node to this new node
            highlightEdge(getCurrentNodeFromTraversal(), node);
        }
    }
    
    private Integer getCurrentNodeFromStep(String stepDescription) {
        if (stepDescription.startsWith("Visited:")) {
            return Integer.parseInt(stepDescription.split(": ")[1]);
        } else if (stepDescription.startsWith("Going deeper to:")) {
            return Integer.parseInt(stepDescription.split(": ")[1]);
        }
        return null;
    }
    
    private Integer getCurrentNodeFromTraversal() {
        for (int i = currentStep - 1; i >= 0; i--) {
            String step = traversalSteps.get(i);
            if (step.startsWith("Visited:") || step.startsWith("Going deeper to:")) {
                return Integer.parseInt(step.split(": ")[1]);
            }
        }
        return startNode;
    }
    
    private void highlightEdge(int from, int to) {
        String edgeKey = Math.min(from, to) + "-" + Math.max(from, to);
        Line edge = edgeLines.get(edgeKey);
        if (edge != null) {
            edge.setStroke(EXPLORED_EDGE_COLOR);
            edge.setStrokeWidth(3);
            exploredEdges.add(edge);
        }
    }
    
    private void updateInfoLabels(List<Integer> stack, List<Integer> visited, Integer currentNode) {
        stackLabel.setText("Stack: " + stack);
        visitedLabel.setText("Visited: " + visited);
        if (currentNode != null) {
            currentLabel.setText("Current: " + currentNode);
            // Highlight current node
            Circle circle = nodeCircles.get(currentNode);
            if (circle != null) {
                circle.setFill(CURRENT_NODE_COLOR);
            }
        }
    }
    
    private void performNextStep() {
        if (isTraversing) return;

        try {
            startNode = Integer.parseInt(startNodeInput.getText().trim());
            if (!graph.containsKey(startNode)) {
                showAlert("Invalid Node", "Start node must exist in the graph.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for start node.");
            return;
        }

        if (traversalSteps == null || currentStep == 0) {
            // Initialize manual traversal state
            initializeManualTraversal();
        }

        if (currentStep < traversalSteps.size()) {
            String stepDescription = traversalSteps.get(currentStep);
            processManualStep(stepDescription);
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            statusLabel.setText("Status: Step " + currentStep + " of " + traversalSteps.size());
            
            if (currentStep == traversalSteps.size()) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
            }
        }
    }
    
    private void initializeManualTraversal() {
        GraphTraversable dfs = new DFS(graph);
        dfs.traverse(startNode);
        traversalSteps = dfs.getTraversalSteps();
        currentStep = 0;
        
        // Initialize manual state
        manualStack = new Stack<>();
        manualVisited = new HashSet<>();
        currentStackState = new ArrayList<>();
        currentVisitedState = new ArrayList<>();
        currentNode = null;
        
        manualStack.push(startNode);
        manualVisited.add(startNode);
        currentStackState.add(startNode);
        currentVisitedState.add(startNode);
        
        // Reset visualization
        resetGraphColors();
        
        // Highlight start node
        Circle startCircle = nodeCircles.get(startNode);
        if (startCircle != null) {
            startCircle.setFill(START_NODE_COLOR);
        }
        
        updateManualInfoLabels();
    }
    
    private void processManualStep(String stepDescription) {
        if (stepDescription.startsWith("Visited:")) {
            int node = Integer.parseInt(stepDescription.split(": ")[1]);
            
            // Process node visit
            manualStack.pop(); // Remove from stack
            currentStackState.remove((Integer) node);
            currentNode = node;
            
            // Mark node as visited
            Circle circle = nodeCircles.get(node);
            if (circle != null) {
                circle.setFill(VISITED_NODE_COLOR);
            }
            
        } else if (stepDescription.startsWith("Going deeper to:")) {
            int node = Integer.parseInt(stepDescription.split(": ")[1]);
            
            // Add neighbor to stack
            manualStack.push(node);
            manualVisited.add(node);
            currentStackState.add(node);
            currentVisitedState.add(node);
            
            // Mark node as in stack
            Circle circle = nodeCircles.get(node);
            if (circle != null) {
                circle.setFill(STACK_NODE_COLOR);
            }
            
            // Highlight edge from current node to this new node
            if (currentNode != null) {
                highlightEdge(currentNode, node);
            }
        }
        
        updateManualInfoLabels();
    }
    
    private void updateManualInfoLabels() {
        stackLabel.setText("Stack: " + currentStackState);
        visitedLabel.setText("Visited: " + currentVisitedState);
        if (currentNode != null) {
            currentLabel.setText("Current: " + currentNode);
            // Highlight current node
            Circle circle = nodeCircles.get(currentNode);
            if (circle != null) {
                circle.setFill(CURRENT_NODE_COLOR);
            }
        }
    }
    
    private void resetGraphColors() {
        // Reset all nodes to default color
        for (Circle circle : nodeCircles.values()) {
            circle.setFill(DEFAULT_NODE_COLOR);
        }
        
        // Reset all edges to default color
        for (Line edge : edgeLines.values()) {
            edge.setStroke(DEFAULT_EDGE_COLOR);
            edge.setStrokeWidth(2);
        }
        
        visitedNodes.clear();
        stackNodes.clear();
        exploredEdges.clear();
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        traversalSteps = null;
        currentStep = 0;
        manualStack = null;
        manualVisited = null;
        currentStackState = null;
        currentVisitedState = null;
        currentNode = null;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stackLabel.setText("Stack: []");
        visitedLabel.setText("Visited: []");
        currentLabel.setText("Current: -");
        
        resetGraphColors();
        
        isTraversing = false;
        isPaused = false;
        startTraversalBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateGraphBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        startNodeInput.setDisable(false);
    }
    
    private void generateNewGraph() {
        if (isTraversing) return;
        
        Random rand = new Random();
        graph = new HashMap<>();
        nodePositions = new HashMap<>();
        
        // Generate a random graph with 6-10 nodes
        int nodeCount = rand.nextInt(5) + 6;
        
        // Position nodes in a circular layout
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        double radius = Math.min(CANVAS_WIDTH, CANVAS_HEIGHT) * 0.35;
        
        // Initialize with a connected graph
        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            nodePositions.put(i, new Point(x, y));
            graph.put(i, new ArrayList<>());
            
            // Ensure connectivity by connecting to the previous node
            if (i > 0) {
                graph.get(i - 1).add(i);
                graph.get(i).add(i - 1);
            }
        }
        
        // Add random additional edges to make the graph more interesting
        for (int i = 0; i < nodeCount; i++) {
            int additionalEdges = rand.nextInt(2); // 0 to 1 additional edges per node
            Set<Integer> connectedNodes = new HashSet<>(graph.get(i));
            for (int j = 0; j < additionalEdges; j++) {
                int neighbor = rand.nextInt(nodeCount);
                if (neighbor != i && !connectedNodes.contains(neighbor)) {
                    graph.get(i).add(neighbor);
                    graph.get(neighbor).add(i);
                    connectedNodes.add(neighbor);
                }
            }
        }
        
        // Reset visualization
        resetVisualization();
        drawGraph();
    }
    
    private void replayTraversal() {
        resetVisualization();
        startAutoTraversal();
    }
    
    private void pauseTraversal() {
        if (sequentialTransition != null && isTraversing) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeTraversal() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Traversing...");
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