package com.dsa.simulator.sorting;

import com.dsa.algorithms.sorting.BubbleSort;
import com.dsa.algorithms.sorting.Sortable;
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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BubbleSortVisualizer extends Application {
    private static final int MAX_BAR_HEIGHT = 300;
    private static final int BAR_WIDTH = 40;
    private static final int SPACING = 10;
    
    private final Sortable algo = new BubbleSort();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length); // Store original for replay
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isSorting = false;
    private boolean isPaused = false;
    
    private Rectangle[] bars;
    private int comparisons = 0;
    private int swaps = 0;
    
    private Label comparisonsLabel;
    private Label swapsLabel;
    private Label statusLabel;
    private Label stepLabel;
    private HBox visualizationBox;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button autoSortBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color COMPARING_COLOR = Color.web("#ffa726"); // Orange
    private final Color SWAPPING_COLOR = Color.web("#ef5350"); // Red
    private final Color SORTED_COLOR = Color.web("#66bb6a"); // Green
    
    @Override
    public void start(Stage primaryStage) {
        // Create the main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        // Create top panel with information
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        // Create visualization area
        visualizationBox = new HBox(SPACING);
        visualizationBox.setAlignment(Pos.BOTTOM_CENTER);
        visualizationBox.setPadding(new Insets(20, 0, 40, 0));
        visualizationBox.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        updateVisualization();
        
        ScrollPane scrollPane = new ScrollPane(visualizationBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        root.setCenter(scrollPane);
        
        // Create control panel
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        // Set up the scene
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Bubble Sort Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        // Create labels with styling
        comparisonsLabel = createStyledLabel("Comparisons: 0");
        swapsLabel = createStyledLabel("Swaps: 0");
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        
        infoPanel.getChildren().addAll(comparisonsLabel, swapsLabel, statusLabel, stepLabel);
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
        
        // Create buttons
        autoSortBtn = createStyledButton("Auto Sort");
        autoSortBtn.setOnAction(e -> startAutoSort());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        Button enterValuesBtn = createStyledButton("Enter Values");
        enterValuesBtn.setOnAction(e -> showValueInputDialog());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseSorting());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeSorting());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replaySorting());
        
        // Create speed slider
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
            autoSortBtn, nextStepBtn, resetBtn, enterValuesBtn, 
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
        bars = new Rectangle[array.length];
        
        int maxValue = Arrays.stream(array).max().orElse(1);
        
        for (int i = 0; i < array.length; i++) {
            double height = (double) array[i] / maxValue * MAX_BAR_HEIGHT;
            Rectangle bar = new Rectangle(BAR_WIDTH, height);
            bar.setFill(DEFAULT_COLOR);
            bar.setArcWidth(5);
            bar.setArcHeight(5);
            
            // Add value label
            VBox barContainer = new VBox(5);
            barContainer.setAlignment(Pos.BOTTOM_CENTER);
            
            Label valueLabel = new Label(String.valueOf(array[i]));
            valueLabel.setTextFill(Color.WHITE);
            valueLabel.setStyle("-fx-font-weight: bold;");
            
            barContainer.getChildren().addAll(valueLabel, bar);
            visualizationBox.getChildren().add(barContainer);
            bars[i] = bar;
        }
    }
    
    private void startAutoSort() {
        if (isSorting) return;

        isSorting = true;
        isPaused = false;
        statusLabel.setText("Status: Sorting...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        autoSortBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);

        // Generate steps using your BubbleSort algorithm
        steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
        comparisons = 0;
        swaps = 0;
        currentStep = 0;

        sequentialTransition = new SequentialTransition();

        // Process each step with animations
        for (int step = 1; step < steps.size(); step++) {
            final int currentStepIndex = step; // Create final copy for lambda
            int[] previousStep = steps.get(step - 1);
            int[] currentStepArray = steps.get(step);

            // Reset bars to default before this step
            PauseTransition preStepReset = new PauseTransition(Duration.ZERO);
            preStepReset.setOnFinished(e -> {
                for (Rectangle bar : bars) {
                    bar.setFill(DEFAULT_COLOR);
                }
            });
            sequentialTransition.getChildren().add(preStepReset);

            // Find which indices are being compared (improved detection)
            int compareIdx1 = -1;
            int compareIdx2 = -1;
            boolean isSwap = false;

            // Detect swap first
            for (int i = 0; i < previousStep.length - 1; i++) {
                if (previousStep[i] != currentStepArray[i] || previousStep[i + 1] != currentStepArray[i + 1]) {
                    compareIdx1 = i;
                    compareIdx2 = i + 1;
                    isSwap = true;
                    break;
                }
            }
            // If no swap, infer the comparison position (bubble sort typically compares sequentially per pass)
            if (compareIdx1 == -1) {
                // Simple heuristic: step % n for inner loop position in outer pass
                int n = previousStep.length;
                compareIdx1 = (step - 1) % (n - 1);
                compareIdx2 = compareIdx1 + 1;
                if (compareIdx2 >= n) compareIdx2 = n - 1;
            }

            if (compareIdx1 != -1 && compareIdx2 != -1 && bars != null && compareIdx1 < bars.length && compareIdx2 < bars.length) {
                // Show comparison (orange) - dynamic from current fill
                ParallelTransition highlight = new ParallelTransition();
                FillTransition ft1 = new FillTransition(Duration.millis(300), bars[compareIdx1]);
                ft1.setFromValue((Color) bars[compareIdx1].getFill());
                ft1.setToValue(COMPARING_COLOR);
                FillTransition ft2 = new FillTransition(Duration.millis(300), bars[compareIdx2]);
                ft2.setFromValue((Color) bars[compareIdx2].getFill());
                ft2.setToValue(COMPARING_COLOR);
                highlight.getChildren().addAll(ft1, ft2);

                sequentialTransition.getChildren().add(highlight);

                comparisons++;
                Platform.runLater(() -> comparisonsLabel.setText("Comparisons: " + comparisons));

                if (isSwap) {
                    // Handle swap animation (red) - dynamic from current (orange)
                    FillTransition swapColor1 = new FillTransition(Duration.millis(200), bars[compareIdx1]);
                    swapColor1.setFromValue(COMPARING_COLOR);
                    swapColor1.setToValue(SWAPPING_COLOR);
                    FillTransition swapColor2 = new FillTransition(Duration.millis(200), bars[compareIdx2]);
                    swapColor2.setFromValue(COMPARING_COLOR);
                    swapColor2.setToValue(SWAPPING_COLOR);
                    ParallelTransition swapColor = new ParallelTransition(swapColor1, swapColor2);

                    // Swap animation
                    TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), bars[compareIdx1]);
                    TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), bars[compareIdx2]);

                    double distance = BAR_WIDTH + SPACING;
                    tt1.setByX(distance);
                    tt2.setByX(-distance);

                    ParallelTransition swapAnimation = new ParallelTransition(tt1, tt2);

                    // Reset colors to default after swap - dynamic from current (red)
                    PauseTransition pause = new PauseTransition(Duration.millis(100));
                    FillTransition reset1 = new FillTransition(Duration.millis(300), bars[compareIdx1]);
                    reset1.setFromValue(SWAPPING_COLOR);
                    reset1.setToValue(DEFAULT_COLOR);
                    FillTransition reset2 = new FillTransition(Duration.millis(300), bars[compareIdx2]);
                    reset2.setFromValue(SWAPPING_COLOR);
                    reset2.setToValue(DEFAULT_COLOR);
                    ParallelTransition resetColors = new ParallelTransition(reset1, reset2);

                    TranslateTransition resetPos1 = new TranslateTransition(Duration.ZERO, bars[compareIdx1]);
                    TranslateTransition resetPos2 = new TranslateTransition(Duration.ZERO, bars[compareIdx2]);
                    resetPos1.setToX(0);
                    resetPos2.setToX(0);
                    ParallelTransition resetPositions = new ParallelTransition(resetPos1, resetPos2);

                    // Actually update the array after reset
                    SequentialTransition fullSwap = new SequentialTransition(
                        swapColor, swapAnimation, pause, resetColors, resetPositions
                    );

                    fullSwap.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();

                        swaps++;
                        Platform.runLater(() -> {
                            swapsLabel.setText("Swaps: " + swaps);
                            this.currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + this.currentStep);
                        });
                    });

                    sequentialTransition.getChildren().add(fullSwap);
                } else {
                    // No swap, reset colors after comparison - dynamic from current (orange)
                    PauseTransition pause = new PauseTransition(Duration.millis(600));
                    FillTransition reset1 = new FillTransition(Duration.millis(300), bars[compareIdx1]);
                    reset1.setFromValue(COMPARING_COLOR);
                    reset1.setToValue(DEFAULT_COLOR);
                    FillTransition reset2 = new FillTransition(Duration.millis(300), bars[compareIdx2]);
                    reset2.setFromValue(COMPARING_COLOR);
                    reset2.setToValue(DEFAULT_COLOR);
                    ParallelTransition resetColors = new ParallelTransition(reset1, reset2);

                    SequentialTransition noSwap = new SequentialTransition(pause, resetColors);
                    noSwap.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();
                        Platform.runLater(() -> {
                            this.currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + this.currentStep);
                        });
                    });
                    sequentialTransition.getChildren().add(noSwap);
                }
            }
        }

        // Final sorted marking - dynamic from current fill
        ParallelTransition markAllSorted = new ParallelTransition();
        for (int i = 0; i < array.length; i++) {
            FillTransition ft = new FillTransition(Duration.millis(500), bars[i]);
            ft.setFromValue((Color) bars[i].getFill());
            ft.setToValue(SORTED_COLOR);
            markAllSorted.getChildren().add(ft);
        }
        sequentialTransition.getChildren().add(markAllSorted);

        sequentialTransition.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                isSorting = false;
                autoSortBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
            });
        });

        // Set speed based on slider
        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isSorting) return;

        if (steps == null) {
            steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
            currentStep = 0;
            comparisons = 0;
            swaps = 0;
        }

        if (currentStep < steps.size() - 1) {
            currentStep++;
            int[] previous = steps.get(currentStep - 1);
            int[] current = steps.get(currentStep);
            array = Arrays.copyOf(current, current.length);
            updateVisualization();

            // Improved detection for comparison indices
            int compareIdx1 = -1;
            int compareIdx2 = -1;
            boolean isSwap = false;

            // Detect swap
            for (int i = 0; i < previous.length - 1; i++) {
                if (previous[i] != current[i] || previous[i + 1] != current[i + 1]) {
                    compareIdx1 = i;
                    compareIdx2 = i + 1;
                    isSwap = true;
                    break;
                }
            }
            // If no swap, infer comparison position
            if (compareIdx1 == -1) {
                int n = previous.length;
                compareIdx1 = (currentStep - 1) % (n - 1);
                compareIdx2 = compareIdx1 + 1;
                if (compareIdx2 >= n) compareIdx2 = n - 1;
            }

            // Reset all bars to DEFAULT_COLOR before highlighting
            for (Rectangle bar : bars) {
                bar.setFill(DEFAULT_COLOR);
            }

            if (compareIdx1 != -1 && compareIdx2 != -1 && bars != null && compareIdx1 < bars.length && compareIdx2 < bars.length) {
                // Highlight comparison in orange
                bars[compareIdx1].setFill(COMPARING_COLOR);
                bars[compareIdx2].setFill(COMPARING_COLOR);
                comparisons++;

                if (isSwap) {
                    // Highlight swap in red
                    bars[compareIdx1].setFill(SWAPPING_COLOR);
                    bars[compareIdx2].setFill(SWAPPING_COLOR);
                    swaps++;
                }
                // Note: In manual mode, colors persist until next step/reset for visibility
            }

            // Update labels
            stepLabel.setText("Step: " + currentStep);
            comparisonsLabel.setText("Comparisons: " + comparisons);
            swapsLabel.setText("Swaps: " + swaps);

            // If we've reached the end, mark as complete
            if (currentStep == steps.size() - 1) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");

                // Mark all elements as sorted
                for (int i = 0; i < bars.length; i++) {
                    bars[i].setFill(SORTED_COLOR);
                }
            }
        }
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        // Generate a new random array
        Random rand = new Random();
        array = new int[7]; // Keep the same length as original
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(90) + 10; // Values between 10 and 100
        }
        
        // Store the new array as original for replay
        originalArray = Arrays.copyOf(array, array.length);
        
        steps = null;
        comparisons = 0;
        swaps = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        swapsLabel.setText("Swaps: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        
        updateVisualization();
        
        isSorting = false;
        isPaused = false;
        autoSortBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
    }
    
    private void replaySorting() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        // Reset to the original array (not a new random one)
        array = Arrays.copyOf(originalArray, originalArray.length);
        
        steps = null;
        comparisons = 0;
        swaps = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        swapsLabel.setText("Swaps: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        
        updateVisualization();
        
        isSorting = false;
        isPaused = false;
        autoSortBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        
        // Start auto sort with the same data
        startAutoSort();
    }
    
    private void pauseSorting() {
        if (sequentialTransition != null && isSorting) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeSorting() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Sorting...");
            pauseBtn.setDisable(false);
            resumeBtn.setDisable(true);
        }
    }
    
    private void showValueInputDialog() {
        // Create a custom dialog for input values
        TextInputDialog dialog = new TextInputDialog(Arrays.toString(array).replaceAll("[\\[\\]]", ""));
        dialog.setTitle("Enter Values");
        dialog.setHeaderText("Bubble Sort Input");
        dialog.setContentText("Enter comma-separated integers:");
        
        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] values = input.split(",");
                int[] newArray = new int[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    newArray[i] = Integer.parseInt(values[i].trim());
                }
                
                array = newArray;
                originalArray = Arrays.copyOf(array, array.length); // Store for replay
                steps = null; // Reset steps so new sorting will use the new values
                comparisons = 0;
                swaps = 0;
                currentStep = 0;
                
                comparisonsLabel.setText("Comparisons: 0");
                swapsLabel.setText("Swaps: 0");
                stepLabel.setText("Step: 0");
                statusLabel.setText("Status: Ready");
                statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
                
                updateVisualization();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please enter valid integers separated by commas.");
                alert.showAndWait();
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}