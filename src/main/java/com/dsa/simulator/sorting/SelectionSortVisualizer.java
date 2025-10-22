package com.dsa.simulator.sorting;

import com.dsa.algorithms.sorting.SelectionSort;
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

public class SelectionSortVisualizer extends Application {
    private static final int BOX_SIZE = 60;
    private static final int SPACING = 10;
    
    private final Sortable algo = new SelectionSort();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length);
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isSorting = false;
    private boolean isPaused = false;
    
    private StackPane[] boxes;
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
    private final Color MINIMUM_COLOR = Color.web("#ab47bc"); // Purple for minimum element
    private final Color SWAPPING_COLOR = Color.web("#ef5350"); // Red for swapping
    private final Color SORTED_COLOR = Color.web("#66bb6a"); // Green for sorted elements
    
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
        visualizationBox.setAlignment(Pos.CENTER);
        visualizationBox.setPadding(new Insets(20));
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
        primaryStage.setTitle("Selection Sort Visualizer");
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
        boxes = new StackPane[array.length];
        
        for (int i = 0; i < array.length; i++) {
            // Create box with value
            StackPane box = new StackPane();
            box.setPrefSize(BOX_SIZE, BOX_SIZE);
            box.setStyle("-fx-background-color: #4fc3f7; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
            
            // Create value label
            Label valueLabel = new Label(String.valueOf(array[i]));
            valueLabel.setTextFill(Color.WHITE);
            valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            box.getChildren().add(valueLabel);
            visualizationBox.getChildren().add(box);
            boxes[i] = box;
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

        // Generate steps using SelectionSort algorithm
        steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
        comparisons = 0;
        swaps = 0;
        currentStep = 0;

        sequentialTransition = new SequentialTransition();

        // Process each step with animations
        for (int step = 1; step < steps.size(); step++) {
            final int currentStepIndex = step;
            int[] previousStep = steps.get(step - 1);
            int[] currentStepArray = steps.get(step);

            // Reset boxes to default before this step
            PauseTransition preStepReset = new PauseTransition(Duration.ZERO);
            preStepReset.setOnFinished(e -> {
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setStyle("-fx-background-color: #4fc3f7; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                }
                
                // Mark already sorted elements
                for (int i = 0; i < currentStepIndex - 1; i++) {
                    if (i < boxes.length) {
                        boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                    }
                }
            });
            sequentialTransition.getChildren().add(preStepReset);

            // Find which elements were involved in this step
            int currentMinIndex = -1;
            int swapIndex = -1;
            
            // Find the current element being placed (i)
            for (int i = 0; i < previousStep.length; i++) {
                if (previousStep[i] != currentStepArray[i]) {
                    swapIndex = i;
                    break;
                }
            }
            
            // Find the minimum element that was swapped
            if (swapIndex != -1) {
                int swappedValue = currentStepArray[swapIndex];
                for (int i = 0; i < previousStep.length; i++) {
                    if (previousStep[i] == swappedValue && i != swapIndex) {
                        currentMinIndex = i;
                        break;
                    }
                }
            }
            
            final int finalCurrentMinIndex = currentMinIndex;
            final int finalSwapIndex = swapIndex;
            
            if (finalCurrentMinIndex != -1 && finalSwapIndex != -1) {
                // Highlight the current minimum element in purple
                PauseTransition highlightMin = new PauseTransition(Duration.ZERO);
                highlightMin.setOnFinished(e -> {
                    if (finalCurrentMinIndex < boxes.length) {
                        boxes[finalCurrentMinIndex].setStyle("-fx-background-color: #ab47bc; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                    }
                });
                sequentialTransition.getChildren().add(highlightMin);
                
                // Pause to show the minimum
                PauseTransition minPause = new PauseTransition(Duration.millis(300));
                sequentialTransition.getChildren().add(minPause);
                
                // Show comparisons (all elements from swapIndex+1 to end)
                ParallelTransition comparisonAnimation = new ParallelTransition();
                for (int i = finalSwapIndex + 1; i < previousStep.length; i++) {
                    if (i < boxes.length) {
                        final int boxIndex = i;
                        PauseTransition compPause = new PauseTransition(Duration.ZERO);
                        compPause.setOnFinished(e -> {
                            boxes[boxIndex].setStyle("-fx-background-color: #ffa726; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                        });
                        comparisonAnimation.getChildren().add(compPause);
                        comparisons++;
                    }
                }
                
                if (comparisonAnimation.getChildren().size() > 0) {
                    sequentialTransition.getChildren().add(comparisonAnimation);
                    Platform.runLater(() -> comparisonsLabel.setText("Comparisons: " + comparisons));
                }
                
                // Pause to show comparisons
                PauseTransition compPause = new PauseTransition(Duration.millis(300));
                sequentialTransition.getChildren().add(compPause);
                
                // Animate the swap
                if (finalCurrentMinIndex < boxes.length && finalSwapIndex < boxes.length) {
                    // Highlight both elements as swapping (red)
                    PauseTransition swapColor = new PauseTransition(Duration.ZERO);
                    swapColor.setOnFinished(e -> {
                        boxes[finalCurrentMinIndex].setStyle("-fx-background-color: #ef5350; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                        boxes[finalSwapIndex].setStyle("-fx-background-color: #ef5350; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                    });
                    sequentialTransition.getChildren().add(swapColor);
                    
                    // Swap animation (move elements)
                    TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), boxes[finalCurrentMinIndex]);
                    TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), boxes[finalSwapIndex]);
                    
                    double distance = (finalSwapIndex - finalCurrentMinIndex) * (BOX_SIZE + SPACING);
                    tt1.setByX(distance);
                    tt2.setByX(-distance);
                    
                    ParallelTransition swapAnimation = new ParallelTransition(tt1, tt2);
                    sequentialTransition.getChildren().add(swapAnimation);
                    
                    // Reset positions and update array
                    PauseTransition postSwapPause = new PauseTransition(Duration.millis(100));
                    SequentialTransition updateStep = new SequentialTransition(postSwapPause);
                    updateStep.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();
                        
                        // Mark sorted elements
                        for (int i = 0; i <= finalSwapIndex; i++) {
                            if (i < boxes.length) {
                                boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                            }
                        }
                        
                        swaps++;
                        Platform.runLater(() -> {
                            swapsLabel.setText("Swaps: " + swaps);
                            currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + currentStep);
                        });
                    });
                    
                    sequentialTransition.getChildren().add(updateStep);
                }
            } else {
                // No swap in this step (just comparisons)
                PauseTransition noSwapPause = new PauseTransition(Duration.millis(600));
                noSwapPause.setOnFinished(e -> {
                    array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                    updateVisualization();
                    
                    // Mark sorted elements
                    for (int i = 0; i < currentStepIndex; i++) {
                        if (i < boxes.length) {
                            boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                        }
                    }
                    
                    Platform.runLater(() -> {
                        currentStep = currentStepIndex;
                        stepLabel.setText("Step: " + currentStep);
                    });
                });
                sequentialTransition.getChildren().add(noSwapPause);
            }
        }

        // Final state - all elements sorted
        PauseTransition finalState = new PauseTransition(Duration.ZERO);
        finalState.setOnFinished(e -> {
            for (int i = 0; i < boxes.length; i++) {
                boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
            }
        });
        sequentialTransition.getChildren().add(finalState);

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

            // Find which elements were involved in this step
            int currentMinIndex = -1;
            int swapIndex = -1;
            
            // Find the current element being placed (i)
            for (int i = 0; i < previous.length; i++) {
                if (previous[i] != current[i]) {
                    swapIndex = i;
                    break;
                }
            }
            
            // Find the minimum element that was swapped
            if (swapIndex != -1) {
                int swappedValue = current[swapIndex];
                for (int i = 0; i < previous.length; i++) {
                    if (previous[i] == swappedValue && i != swapIndex) {
                        currentMinIndex = i;
                        break;
                    }
                }
            }
            
            // Reset all boxes to default before highlighting
            for (int i = 0; i < boxes.length; i++) {
                boxes[i].setStyle("-fx-background-color: #4fc3f7; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
            }
            
            // Mark already sorted elements
            for (int i = 0; i < currentStep - 1; i++) {
                if (i < boxes.length) {
                    boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                }
            }
            
            if (currentMinIndex != -1 && swapIndex != -1) {
                // Highlight the current minimum element in purple
                boxes[currentMinIndex].setStyle("-fx-background-color: #ab47bc; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                
                // Show comparisons (all elements from swapIndex+1 to end)
                for (int i = swapIndex + 1; i < previous.length; i++) {
                    if (i < boxes.length) {
                        boxes[i].setStyle("-fx-background-color: #ffa726; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                        comparisons++;
                    }
                }
                
                // Highlight both elements as swapping (red)
                boxes[currentMinIndex].setStyle("-fx-background-color: #ef5350; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                boxes[swapIndex].setStyle("-fx-background-color: #ef5350; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
                
                swaps++;
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
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setStyle("-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2b2b2b; -fx-border-radius: 5;");
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
        dialog.setHeaderText("Selection Sort Input");
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