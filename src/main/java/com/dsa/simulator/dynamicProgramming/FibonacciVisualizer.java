package com.dsa.simulator.dynamicProgramming;

import com.dsa.algorithms.dynamicProgramming.Fibonacci;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class FibonacciVisualizer extends Application {
    private static final int SQUARE_SIZE = 50; // Uniform square size
    private static final int SPACING = 10;
    
    private final Fibonacci algo = new Fibonacci();
    private int n = 10;
    private List<Integer> fibSteps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int fibResult = -1;
    
    private Rectangle[] squares;
    private int computations = 0;
    
    private Label computationsLabel;
    private Label statusLabel;
    private Label stepLabel;
    private Label nLabel;
    private Label resultLabel;
    private HBox visualizationBox;
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField nInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color COMPUTING_COLOR = Color.web("#ffa726"); // Orange - currently computing
    private final Color COMPUTED_COLOR = Color.web("#66bb6a"); // Green - computed
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        visualizationBox = new HBox(SPACING);
        visualizationBox.setAlignment(Pos.CENTER); // Changed to center alignment
        visualizationBox.setPadding(new Insets(0, 0, 0, 0)); // Removed unnecessary padding
        visualizationBox.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        updateVisualization();
        
        // Center the visualizationBox vertically and horizontally in the center
        StackPane centerPane = new StackPane(visualizationBox);
        centerPane.setAlignment(Pos.CENTER);
        root.setCenter(centerPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Fibonacci Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        computationsLabel = createStyledLabel("Computations: 0");
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        nLabel = createStyledLabel("n: " + n);
        resultLabel = createStyledLabel("Result: Not Computed");
        
        infoPanel.getChildren().addAll(computationsLabel, statusLabel, stepLabel, nLabel, resultLabel);
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
        
        // n input
        VBox nControl = new VBox(5);
        nControl.setAlignment(Pos.CENTER);
        Label nInputLabel = createStyledLabel("n Value:");
        nInputLabel.setTextFill(Color.LIGHTGRAY);
        
        nInput = new TextField(String.valueOf(n));
        nInput.setPrefWidth(80);
        nInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        nControl.getChildren().addAll(nInputLabel, nInput);
        
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
        
        controlPanel.getChildren().addAll(
            nControl, startComputeBtn, nextStepBtn, resetBtn, 
            pauseBtn, resumeBtn, replayBtn, speedControl
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
        visualizationBox.getChildren().clear();
        if (fibSteps == null || fibSteps.isEmpty()) {
            return;
        }
        
        squares = new Rectangle[fibSteps.size()];
        int maxValue = fibSteps.stream().mapToInt(Integer::intValue).max().orElse(1);
        double scaleFactor = Math.sqrt((double) maxValue / SQUARE_SIZE); // Adjust size based on max value
        
        for (int i = 0; i < fibSteps.size(); i++) {
            // Use a consistent square size, with opacity to indicate value
            Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
            
            // Set color based on state
            if (i < currentStep) {
                square.setFill(COMPUTED_COLOR);
            } else if (i == currentStep) {
                square.setFill(COMPUTING_COLOR);
            } else {
                square.setFill(DEFAULT_COLOR);
            }
            
            square.setArcWidth(5);
            square.setArcHeight(5);
            square.setStroke(Color.WHITE);
            square.setStrokeWidth(2);
            
            // Adjust opacity to reflect value (proportional to Fibonacci number)
            double valueProportion = (double) fibSteps.get(i) / maxValue;
            square.setOpacity(0.3 + 0.7 * valueProportion); // Opacity from 0.3 to 1.0
            
            VBox squareContainer = new VBox(5);
            squareContainer.setAlignment(Pos.CENTER);
            
            Label valueLabel = new Label(String.valueOf(fibSteps.get(i)));
            valueLabel.setTextFill(Color.WHITE);
            valueLabel.setStyle("-fx-font-weight: bold;");
            
            squareContainer.getChildren().addAll(valueLabel, square);
            visualizationBox.getChildren().add(squareContainer);
            squares[i] = square;
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            n = Integer.parseInt(nInput.getText().trim());
            if (n < 0) {
                showAlert("Invalid Input", "n must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for n.");
            return;
        }

        isComputing = true;
        isPaused = false;
        fibResult = -1;
        statusLabel.setText("Status: Computing...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");
        resultLabel.setText("Result: Computing...");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        nInput.setDisable(true);

        // Compute fib to get steps
        fibResult = algo.fib(n);
        fibSteps = algo.getSteps();
        computations = 0;
        currentStep = 0;

        updateVisualization();

        sequentialTransition = new SequentialTransition();

        // Animate through each compute step
        for (int step = 0; step < fibSteps.size(); step++) {
            final int currentStepIndex = step;
            
            // Highlight current computing square
            if (step < squares.length) {
                FillTransition highlight = new FillTransition(Duration.millis(300), squares[step]);
                highlight.setFromValue((Color) squares[step].getFill());
                highlight.setToValue(COMPUTING_COLOR);
                
                SequentialTransition computeStep = new SequentialTransition(highlight);
                computeStep.setOnFinished(e -> {
                    computations++;
                    Platform.runLater(() -> {
                        computationsLabel.setText("Computations: " + computations);
                        this.currentStep = currentStepIndex + 1;
                        stepLabel.setText("Step: " + this.currentStep);
                        squares[currentStepIndex].setFill(COMPUTED_COLOR);
                    });
                });
                
                sequentialTransition.getChildren().add(computeStep);
            }
        }

        // Final state
        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                resultLabel.setText("Result: " + fibResult);
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                nInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            n = Integer.parseInt(nInput.getText().trim());
            if (n < 0) {
                showAlert("Invalid Input", "n must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for n.");
            return;
        }

        if (fibSteps == null) {
            fibResult = algo.fib(n);
            fibSteps = algo.getSteps();
            currentStep = 0;
            computations = 0;
            fibResult = -1;
            updateVisualization();
        }

        if (currentStep < fibSteps.size()) {
            // Highlight current square
            squares[currentStep].setFill(COMPUTING_COLOR);
            
            computations++;
            currentStep++;
            
            // Update computed color after computation
            if (currentStep - 1 < squares.length) {
                squares[currentStep - 1].setFill(COMPUTED_COLOR);
            }
            
            stepLabel.setText("Step: " + currentStep);
            computationsLabel.setText("Computations: " + computations);
            nLabel.setText("n: " + n);
            
            // If reached end
            if (currentStep == fibSteps.size()) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                resultLabel.setText("Result: " + fibResult);
            }
        }
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        fibSteps = null;
        computations = 0;
        currentStep = 0;
        fibResult = -1;
        
        computationsLabel.setText("Computations: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        resultLabel.setText("Result: Not Computed");
        nLabel.setText("n: " + n);
        
        visualizationBox.getChildren().clear();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        nInput.setDisable(false);
    }
    
    private void replayComputing() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        computations = 0;
        currentStep = 0;
        fibResult = -1;
        
        computationsLabel.setText("Computations: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        resultLabel.setText("Result: Not Computed");
        nLabel.setText("n: " + n);
        
        visualizationBox.getChildren().clear();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        nInput.setDisable(false);
        
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