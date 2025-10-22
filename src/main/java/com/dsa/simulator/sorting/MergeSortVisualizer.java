package com.dsa.simulator.sorting;

import com.dsa.algorithms.sorting.MergeSort;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MergeSortVisualizer extends Application {
    private static final int ARRAY_ELEMENT_WIDTH = 60;
    private static final int ARRAY_ELEMENT_HEIGHT = 40;
    private static final int SPACING = 5;
    
    private final Sortable algo = new MergeSort();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length);
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isSorting = false;
    private boolean isPaused = false;
    
    private Label[] arrayLabels;
    private int comparisons = 0;
    private int merges = 0;
    
    private Label comparisonsLabel;
    private Label mergesLabel;
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
    
    // CSS style definitions
    private final String DEFAULT_STYLE = "-fx-background-color: #4fc3f7; -fx-background-radius: 5; -fx-border-color: #1976d2; -fx-border-radius: 5;";
    private final String COMPARING_STYLE = "-fx-background-color: #ffa726; -fx-background-radius: 5; -fx-border-color: #f57c00; -fx-border-radius: 5;";
    private final String MERGING_STYLE = "-fx-background-color: #ef5350; -fx-background-radius: 5; -fx-border-color: #c62828; -fx-border-radius: 5;";
    private final String SORTED_STYLE = "-fx-background-color: #66bb6a; -fx-background-radius: 5; -fx-border-color: #2e7d32; -fx-border-radius: 5;";
    private final String LEFT_PARTITION_STYLE = "-fx-background-color: #ab47bc; -fx-background-radius: 5; -fx-border-color: #7b1fa2; -fx-border-radius: 5;";
    private final String RIGHT_PARTITION_STYLE = "-fx-background-color: #ff7043; -fx-background-radius: 5; -fx-border-color: #e65100; -fx-border-radius: 5;";
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        visualizationBox = new HBox(SPACING);
        visualizationBox.setAlignment(Pos.CENTER);
        visualizationBox.setPadding(new Insets(20));
        visualizationBox.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        updateVisualization();
        
        ScrollPane scrollPane = new ScrollPane(visualizationBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        root.setCenter(scrollPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setTitle("Merge Sort Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        comparisonsLabel = createStyledLabel("Comparisons: 0");
        mergesLabel = createStyledLabel("Merges: 0");
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        
        infoPanel.getChildren().addAll(comparisonsLabel, mergesLabel, statusLabel, stepLabel);
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
        
        VBox speedControl = new VBox(5);
        speedControl.setAlignment(Pos.CENTER);
        Label speedLabel = createStyledLabel("Speed:");
        speedLabel.setTextFill(Color.LIGHTGRAY);
        
        speedSlider = new Slider(0.1, 5.0, 1.0); // Changed to 0.1-5.0 range for better control
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setMinorTickCount(4);
        speedSlider.setSnapToTicks(false);
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
        arrayLabels = new Label[array.length];
        
        for (int i = 0; i < array.length; i++) {
            Label elementLabel = new Label(String.valueOf(array[i]));
            elementLabel.setPrefSize(ARRAY_ELEMENT_WIDTH, ARRAY_ELEMENT_HEIGHT);
            elementLabel.setAlignment(Pos.CENTER);
            elementLabel.setStyle(DEFAULT_STYLE);
            elementLabel.setTextFill(Color.WHITE);
            elementLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            visualizationBox.getChildren().add(elementLabel);
            arrayLabels[i] = elementLabel;
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

        steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
        comparisons = 0;
        merges = 0;
        currentStep = 0;

        sequentialTransition = new SequentialTransition();

        for (int step = 1; step < steps.size(); step++) {
            final int currentStepIndex = step;
            int[] currentStepArray = steps.get(step);
            int[] previousStep = steps.get(step - 1);

            // Reset all elements to default style with proper timing
            PauseTransition resetStyles = new PauseTransition(Duration.millis(200));
            resetStyles.setOnFinished(e -> {
                for (Label label : arrayLabels) {
                    label.setStyle(DEFAULT_STYLE);
                }
            });
            sequentialTransition.getChildren().add(resetStyles);

            // Highlight partitions with better timing
            PauseTransition highlightPartitions = new PauseTransition(Duration.millis(600));
            highlightPartitions.setOnFinished(e -> {
                highlightPartitionsWithMergeLogic(currentStepArray, previousStep);
            });
            sequentialTransition.getChildren().add(highlightPartitions);

            // Find and highlight changed elements (merge operations)
            boolean hasChanges = false;
            for (int i = 0; i < array.length; i++) {
                if (i < currentStepArray.length && previousStep[i] != currentStepArray[i]) {
                    hasChanges = true;
                    final int index = i;
                    
                    // Highlight merging elements with better timing
                    PauseTransition highlightMerge = new PauseTransition(Duration.millis(400));
                    highlightMerge.setOnFinished(e -> {
                        if (index < arrayLabels.length) {
                            arrayLabels[index].setStyle(MERGING_STYLE);
                        }
                    });
                    sequentialTransition.getChildren().add(highlightMerge);
                }
            }

            if (hasChanges) {
                merges++;
                Platform.runLater(() -> mergesLabel.setText("Merges: " + merges));
            }

            // Update the array and visualization with proper timing
            PauseTransition updateStep = new PauseTransition(Duration.millis(800));
            updateStep.setOnFinished(e -> {
                array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                updateVisualization();
                Platform.runLater(() -> {
                    currentStep = currentStepIndex;
                    stepLabel.setText("Step: " + currentStep);
                });
            });
            sequentialTransition.getChildren().add(updateStep);
        }

        // Final sorted state with better timing
        PauseTransition markSorted = new PauseTransition(Duration.millis(1000));
        markSorted.setOnFinished(e -> {
            for (Label label : arrayLabels) {
                label.setStyle(SORTED_STYLE);
            }
        });
        sequentialTransition.getChildren().add(markSorted);

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

        // Set speed based on slider (inverted for more intuitive control)
        double speed = speedSlider.getValue();
        sequentialTransition.setRate(speed);
        sequentialTransition.play();
    }
    
    // Improved partition highlighting logic
    private void highlightPartitionsWithMergeLogic(int[] currentArray, int[] previousArray) {
        // Find the range where changes occurred
        int changeStart = -1;
        int changeEnd = -1;
        
        for (int i = 0; i < currentArray.length; i++) {
            if (i < previousArray.length && currentArray[i] != previousArray[i]) {
                if (changeStart == -1) changeStart = i;
                changeEnd = i;
            }
        }
        
        if (changeStart == -1) return; // No changes
        
        // Highlight left and right partitions around the changed area
        int midPoint = changeStart + (changeEnd - changeStart) / 2;
        
        for (int i = 0; i < currentArray.length; i++) {
            if (i >= changeStart && i <= changeEnd) {
                // This is the merging area
                if (i < arrayLabels.length) {
                    arrayLabels[i].setStyle(MERGING_STYLE);
                }
            } else if (i < changeStart) {
                // Left partition
                if (i < arrayLabels.length) {
                    arrayLabels[i].setStyle(LEFT_PARTITION_STYLE);
                }
            } else {
                // Right partition
                if (i < arrayLabels.length) {
                    arrayLabels[i].setStyle(RIGHT_PARTITION_STYLE);
                }
            }
        }
    }
    
    // Helper method to highlight sorted subarrays
    private void highlightSortedSubarrays(int[] currentArray) {
        // Find sorted subarrays in the current step
        int i = 0;
        while (i < currentArray.length) {
            int j = i;
            while (j < currentArray.length - 1 && currentArray[j] <= currentArray[j + 1]) {
                j++;
            }
            
            // If we found a sorted subarray of length > 1, highlight it
            if (j > i) {
                // Determine if this is a left or right partition based on position
                boolean isLeftPartition = i < currentArray.length / 2;
                
                for (int k = i; k <= j; k++) {
                    if (k < arrayLabels.length) {
                        if (isLeftPartition) {
                            arrayLabels[k].setStyle(LEFT_PARTITION_STYLE);
                        } else {
                            arrayLabels[k].setStyle(RIGHT_PARTITION_STYLE);
                        }
                    }
                }
                i = j + 1;
            } else {
                i++;
            }
        }
    }
    
    private void performNextStep() {
        if (isSorting) return;

        if (steps == null) {
            steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
            currentStep = 0;
            comparisons = 0;
            merges = 0;
        }

        if (currentStep < steps.size() - 1) {
            currentStep++;
            int[] current = steps.get(currentStep);
            int[] previous = steps.get(currentStep - 1);
            array = Arrays.copyOf(current, current.length);
            updateVisualization();

            // Highlight partitions (left and right arrays)
            highlightSortedSubarrays(current);

            // Highlight changed elements (merge operations)
            for (int i = 0; i < array.length; i++) {
                if (i < current.length && previous[i] != current[i]) {
                    arrayLabels[i].setStyle(MERGING_STYLE);
                    merges++;
                }
            }

            stepLabel.setText("Step: " + currentStep);
            mergesLabel.setText("Merges: " + merges);

            if (currentStep == steps.size() - 1) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");

                for (Label label : arrayLabels) {
                    label.setStyle(SORTED_STYLE);
                }
            }
        }
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        Random rand = new Random();
        array = new int[7];
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(90) + 10;
        }
        
        originalArray = Arrays.copyOf(array, array.length);
        
        steps = null;
        comparisons = 0;
        merges = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        mergesLabel.setText("Merges: 0");
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
        
        array = Arrays.copyOf(originalArray, originalArray.length);
        
        steps = null;
        comparisons = 0;
        merges = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        mergesLabel.setText("Merges: 0");
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
        TextInputDialog dialog = new TextInputDialog(Arrays.toString(array).replaceAll("[\\[\\]]", ""));
        dialog.setTitle("Enter Values");
        dialog.setHeaderText("Merge Sort Input");
        dialog.setContentText("Enter comma-separated integers:");
        
        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] values = input.split(",");
                int[] newArray = new int[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    newArray[i] = Integer.parseInt(values[i].trim());
                }
                
                array = newArray;
                originalArray = Arrays.copyOf(array, array.length);
                steps = null;
                comparisons = 0;
                merges = 0;
                currentStep = 0;
                
                comparisonsLabel.setText("Comparisons: 0");
                mergesLabel.setText("Merges: 0");
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