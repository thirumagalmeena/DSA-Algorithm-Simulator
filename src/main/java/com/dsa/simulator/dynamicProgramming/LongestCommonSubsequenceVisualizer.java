package com.dsa.simulator.dynamicProgramming;

import com.dsa.algorithms.dynamicProgramming.LongestCommonSubsequence;
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

import java.util.List;

public class LongestCommonSubsequenceVisualizer extends Application {
    private static final int CELL_SIZE = 45;
    private static final int PADDING = 5;
    
    private final LongestCommonSubsequence algo = new LongestCommonSubsequence();
    private String text1 = "ABCDGH";
    private String text2 = "AEDFHR";
    private List<int[][]> steps;
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
    private TextField text1Input;
    private TextField text2Input;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7");
    private final Color COMPUTING_COLOR = Color.web("#ffa726");
    private final Color COMPUTED_COLOR = Color.web("#66bb6a");
    private final Color MATCH_COLOR = Color.web("#ff7043");
    
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
        visualizationGrid.setPadding(new Insets(20));
        
        ScrollPane scrollPane = new ScrollPane(visualizationGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-background-color: #3c3f41;");
        
        StackPane centerPane = new StackPane(scrollPane);
        centerPane.setAlignment(Pos.CENTER);
        root.setCenter(centerPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Longest Common Subsequence Visualizer");
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
        resultLabel = createStyledLabel("LCS Length: Not Computed");
        
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
        VBox text1Control = new VBox(5);
        text1Control.setAlignment(Pos.CENTER);
        Label text1Label = createStyledLabel("Text 1:");
        text1Label.setTextFill(Color.LIGHTGRAY);
        text1Input = new TextField("ABCDGH");
        text1Input.setPrefWidth(150);
        text1Input.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        text1Control.getChildren().addAll(text1Label, text1Input);
        
        VBox text2Control = new VBox(5);
        text2Control.setAlignment(Pos.CENTER);
        Label text2Label = createStyledLabel("Text 2:");
        text2Label.setTextFill(Color.LIGHTGRAY);
        text2Input = new TextField("AEDFHR");
        text2Input.setPrefWidth(150);
        text2Input.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        text2Control.getChildren().addAll(text2Label, text2Input);
        
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
        
        controlPanel.getChildren().addAll(text1Control, text2Control, startComputeBtn, nextStepBtn, 
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
        
        int[][] currentTable = steps.get(Math.min(currentStep, steps.size() - 1));
        int rows = currentTable.length;
        int cols = currentTable[0].length;
        
        cells = new Rectangle[rows][cols];
        
        // Add column headers (text2 characters)
        Text emptyCorner = new Text("");
        emptyCorner.setFill(Color.WHITE);
        visualizationGrid.add(emptyCorner, 0, 0);
        
        Text emptyHeader = new Text("ε");
        emptyHeader.setFill(Color.LIGHTBLUE);
        emptyHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        visualizationGrid.add(emptyHeader, 1, 0);
        
        for (int j = 1; j < cols; j++) {
            Text header = new Text(String.valueOf(text2.charAt(j - 1)));
            header.setFill(Color.LIGHTBLUE);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            visualizationGrid.add(header, j + 1, 0);
        }
        
        // Add row headers (text1 characters)
        Text emptyRowHeader = new Text("ε");
        emptyRowHeader.setFill(Color.LIGHTGREEN);
        emptyRowHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        visualizationGrid.add(emptyRowHeader, 0, 1);
        
        for (int i = 1; i < rows; i++) {
            Text header = new Text(String.valueOf(text1.charAt(i - 1)));
            header.setFill(Color.LIGHTGREEN);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            visualizationGrid.add(header, 0, i + 1);
        }
        
        // Find max value for color scaling
        maxValue = 0;
        for (int[] row : currentTable) {
            for (int value : row) {
                maxValue = Math.max(maxValue, value);
            }
        }
        if (maxValue == 0) maxValue = 1;
        
        // Create cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                
                // Color based on value
                double valueProportion = (double) currentTable[i][j] / maxValue;
                cell.setFill(Color.color(0, valueProportion * 0.8, valueProportion));
                cell.setArcWidth(5);
                cell.setArcHeight(5);
                cell.setStroke(Color.WHITE);
                cell.setStrokeWidth(1);
                
                // Add value text
                Text valueText = new Text(String.valueOf(currentTable[i][j]));
                valueText.setFill(Color.WHITE);
                valueText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                StackPane cellPane = new StackPane(cell, valueText);
                cellPane.setAlignment(Pos.CENTER);
                
                visualizationGrid.add(cellPane, j + 1, i + 1);
                cells[i][j] = cell;
                
                // Highlight current row being computed
                if (i == currentStep && i > 0) {
                    cell.setFill(COMPUTING_COLOR);
                } else if (i < currentStep && i > 0) {
                    // Check if characters match
                    if (i > 0 && j > 0 && text1.charAt(i - 1) == text2.charAt(j - 1)) {
                        cell.setFill(MATCH_COLOR);
                    } else {
                        cell.setFill(COMPUTED_COLOR);
                    }
                }
            }
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        text1 = text1Input.getText().trim().toUpperCase();
        text2 = text2Input.getText().trim().toUpperCase();
        
        if (text1.isEmpty() || text2.isEmpty()) {
            showAlert("Invalid Input", "Please enter non-empty strings for both texts.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");
        resultLabel.setText("LCS Length: Computing...");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        text1Input.setDisable(true);
        text2Input.setDisable(true);

        // Compute LCS to get steps
        int result = algo.lcs(text1, text2);
        steps = algo.getSteps();
        currentStep = 0;

        updateVisualization();

        sequentialTransition = new SequentialTransition();

        // Animate through each step (each row of text1)
        for (int step = 0; step <= steps.size(); step++) {
            final int currentStepIndex = step;
            PauseTransition stepTransition = new PauseTransition(Duration.millis(800));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex;
                    stepLabel.setText("Step: " + this.currentStep + " / " + text1.length());
                    updateVisualization();
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
                resultLabel.setText("LCS Length: " + result);
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                text1Input.setDisable(false);
                text2Input.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        text1 = text1Input.getText().trim().toUpperCase();
        text2 = text2Input.getText().trim().toUpperCase();
        
        if (text1.isEmpty() || text2.isEmpty()) {
            showAlert("Invalid Input", "Please enter non-empty strings for both texts.");
            return;
        }

        if (steps == null) {
            int result = algo.lcs(text1, text2);
            steps = algo.getSteps();
            currentStep = 0;
            updateVisualization();
        }

        if (currentStep <= text1.length()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep + " / " + text1.length());
            updateVisualization();
            
            if (currentStep > text1.length()) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                resultLabel.setText("LCS Length: " + algo.lcs(text1, text2));
            }
        }
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
        resultLabel.setText("LCS Length: Not Computed");
        
        visualizationGrid.getChildren().clear();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        text1Input.setDisable(false);
        text2Input.setDisable(false);
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