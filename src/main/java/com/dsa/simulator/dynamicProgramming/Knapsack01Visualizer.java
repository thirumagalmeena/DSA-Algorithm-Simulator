package com.dsa.simulator.dynamicProgramming;

import com.dsa.algorithms.dynamicProgramming.Knapsack01;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Knapsack01Visualizer extends Application {
    private static final int CELL_SIZE = 40;
    private static final int PADDING = 5;
    
    private final Knapsack01 algo = new Knapsack01();
    private int[] weights = {1, 2, 3};
    private int[] values = {6, 10, 12};
    private int capacity = 5;
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int maxValue = 0;
    
    private Rectangle[][] cells;
    private Label statusLabel;
    private Label stepLabel;
    private Label resultLabel;
    private GridPane visualizationGrid;
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField weightsInput;
    private TextField valuesInput;
    private TextField capacityInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7"); // Light blue
    private final Color COMPUTING_COLOR = Color.web("#ffa726"); // Orange
    private final Color COMPUTED_COLOR = Color.web("#66bb6a"); // Green
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        visualizationGrid = new GridPane();
        visualizationGrid.setAlignment(Pos.CENTER);
        visualizationGrid.setHgap(PADDING);
        visualizationGrid.setVgap(PADDING);
        visualizationGrid.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        updateVisualization();
        
        StackPane centerPane = new StackPane(visualizationGrid);
        centerPane.setAlignment(Pos.CENTER);
        root.setCenter(centerPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("0/1 Knapsack Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        resultLabel = createStyledLabel("Result: Not Computed");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, resultLabel);
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
        
        // Input fields
        VBox weightsControl = new VBox(5);
        weightsControl.setAlignment(Pos.CENTER);
        Label weightsLabel = createStyledLabel("Weights (comma-separated):");
        weightsLabel.setTextFill(Color.LIGHTGRAY);
        weightsInput = new TextField("1,2,3");
        weightsInput.setPrefWidth(150);
        weightsInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        weightsControl.getChildren().addAll(weightsLabel, weightsInput);
        
        VBox valuesControl = new VBox(5);
        valuesControl.setAlignment(Pos.CENTER);
        Label valuesLabel = createStyledLabel("Values (comma-separated):");
        valuesLabel.setTextFill(Color.LIGHTGRAY);
        valuesInput = new TextField("6,10,12");
        valuesInput.setPrefWidth(150);
        valuesInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        valuesControl.getChildren().addAll(valuesLabel, valuesInput);
        
        VBox capacityControl = new VBox(5);
        capacityControl.setAlignment(Pos.CENTER);
        Label capacityLabel = createStyledLabel("Capacity:");
        capacityLabel.setTextFill(Color.LIGHTGRAY);
        capacityInput = new TextField("5");
        capacityInput.setPrefWidth(80);
        capacityInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        capacityControl.getChildren().addAll(capacityLabel, capacityInput);
        
        // Buttons
        startComputeBtn = createStyledButton("Start Compute");
        startComputeBtn.setOnAction(e -> startAutoCompute());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseComputing());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeComputing());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayComputing());
        
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
        
        controlPanel.getChildren().addAll(weightsControl, valuesControl, capacityControl, startComputeBtn, nextStepBtn, 
                                        resetBtn, pauseBtn, resumeBtn, replayBtn, speedControl);
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
        visualizationGrid.getChildren().clear();
        if (steps == null || steps.isEmpty()) {
            return;
        }
        
        int n = steps.get(0).length - 1; // Number of columns (capacity + 1)
        cells = new Rectangle[steps.size()][n + 1];
        
        // Add row and column headers
        for (int i = 0; i <= n; i++) {
            Text header = new Text(String.valueOf(i));
            header.setFill(Color.WHITE);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            visualizationGrid.add(header, i + 1, 0);
        }
        for (int i = 0; i < steps.size(); i++) {
            Text header = new Text(i == 0 ? "No Items" : "Item " + i);
            header.setFill(Color.WHITE);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            visualizationGrid.add(header, 0, i + 1);
        }
        
        maxValue = 0;
        for (int[] row : steps) {
            for (int value : row) {
                maxValue = Math.max(maxValue, value);
            }
        }
        
        for (int i = 0; i < steps.size(); i++) {
            int[] row = steps.get(i);
            for (int j = 0; j <= n; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                double valueProportion = (double) row[j] / maxValue;
                cell.setFill(Color.color(0, valueProportion, 0)); // Green intensity based on value
                cell.setArcWidth(5);
                cell.setArcHeight(5);
                cell.setStroke(Color.WHITE);
                cell.setStrokeWidth(1);
                
                // Add value text
                Text valueText = new Text(String.valueOf(row[j]));
                valueText.setFill(Color.WHITE);
                valueText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                StackPane cellPane = new StackPane(cell, valueText);
                cellPane.setAlignment(Pos.CENTER);
                
                visualizationGrid.add(cellPane, j + 1, i + 1);
                cells[i][j] = cell;
                
                // Highlight current step
                if (i < currentStep) {
                    cell.setFill(COMPUTED_COLOR);
                } else if (i == currentStep && j == n) {
                    cell.setFill(COMPUTING_COLOR);
                }
            }
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            weights = parseArray(weightsInput.getText().trim());
            values = parseArray(valuesInput.getText().trim());
            capacity = Integer.parseInt(capacityInput.getText().trim());
            if (weights.length != values.length || capacity < 0) {
                showAlert("Invalid Input", "Weights and values must have the same length, and capacity must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers separated by commas for weights and values, and a valid capacity.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");
        resultLabel.setText("Result: Computing...");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        weightsInput.setDisable(true);
        valuesInput.setDisable(true);
        capacityInput.setDisable(true);

        // Compute knapsack to get steps
        int result = algo.knapsack(weights, values, capacity);
        List<int[]> originalSteps = algo.getSteps();
        
        // Insert a row of zeros at the beginning for "No Items"
        steps = new ArrayList<>();
        int[] zeroRow = new int[capacity + 1];
        steps.add(zeroRow);
        steps.addAll(originalSteps);
        
        currentStep = 0;

        updateVisualization();

        sequentialTransition = new SequentialTransition();

        // Animate through each step
        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            PauseTransition stepTransition = new PauseTransition(Duration.millis(800));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateVisualization();
                    if (currentStepIndex < cells.length) {
                        for (int j = 0; j < cells[currentStepIndex].length; j++) {
                            cells[currentStepIndex][j].setFill(COMPUTED_COLOR);
                        }
                    }
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
                resultLabel.setText("Result: " + result);
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                weightsInput.setDisable(false);
                valuesInput.setDisable(false);
                capacityInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            weights = parseArray(weightsInput.getText().trim());
            values = parseArray(valuesInput.getText().trim());
            capacity = Integer.parseInt(capacityInput.getText().trim());
            if (weights.length != values.length || capacity < 0) {
                showAlert("Invalid Input", "Weights and values must have the same length, and capacity must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers separated by commas for weights and values, and a valid capacity.");
            return;
        }

        if (steps == null) {
            int result = algo.knapsack(weights, values, capacity);
            steps = algo.getSteps();
            currentStep = 0;
            updateVisualization();
        }

        if (currentStep < steps.size()) {
            for (int j = 0; j < cells[currentStep].length; j++) {
                cells[currentStep][j].setFill(COMPUTING_COLOR);
            }
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateVisualization();
            
            if (currentStep == steps.size()) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                resultLabel.setText("Result: " + algo.knapsack(weights, values, capacity));
            }
        }
    }
    
    private int[] parseArray(String input) {
        String[] parts = input.split(",");
        int[] array = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Integer.parseInt(parts[i].trim());
        }
        return array;
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        steps = null;
        currentStep = 0;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        resultLabel.setText("Result: Not Computed");
        
        visualizationGrid.getChildren().clear();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        weightsInput.setDisable(false);
        valuesInput.setDisable(false);
        capacityInput.setDisable(false);
    }
    
    private void replayComputing() {
        resetVisualization();
        startAutoCompute();
    }
    
    private void pauseComputing() {
        if (sequentialTransition != null && isComputing) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeComputing() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Computing...");
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