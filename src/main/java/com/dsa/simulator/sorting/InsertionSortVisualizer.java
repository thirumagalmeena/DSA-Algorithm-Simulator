package com.dsa.simulator.sorting;

import com.dsa.algorithms.sorting.InsertionSort;
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

public class InsertionSortVisualizer extends Application {
    private static final int MAX_BAR_HEIGHT = 300;
    private static final int BAR_WIDTH = 40;
    private static final int SPACING = 10;
    
    private final Sortable algo = new InsertionSort();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length); // Store original for replay
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isSorting = false;
    private boolean isPaused = false;
    
    private Rectangle[] bars;
    private int comparisons = 0;
    private int insertions = 0;
    
    private Label comparisonsLabel;
    private Label insertionsLabel;
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
    private final Color INSERTING_COLOR = Color.web("#ef5350"); // Red
    private final Color SORTED_COLOR = Color.web("#66bb6a"); // Green
    private final Color KEY_COLOR = Color.web("#ab47bc"); // Purple for the key element
    
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
        primaryStage.setTitle("Insertion Sort Visualizer");
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
        insertionsLabel = createStyledLabel("Insertions: 0");
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        
        infoPanel.getChildren().addAll(comparisonsLabel, insertionsLabel, statusLabel, stepLabel);
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

        // Generate steps using InsertionSort algorithm
        steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
        comparisons = 0;
        insertions = 0;
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

            // Find which index is the key being inserted
            int keyIndex = -1;
            int insertPosition = -1;
            
            // Find the key index (the element that was moved)
            for (int i = 0; i < previousStep.length; i++) {
                if (i < currentStepArray.length && previousStep[i] != currentStepArray[i]) {
                    keyIndex = i;
                    break;
                }
            }
            
            // If we found a key, find where it was inserted
            if (keyIndex != -1) {
                int keyValue = currentStepArray[keyIndex];
                
                // Find where this value was in the previous step
                for (int i = 0; i < previousStep.length; i++) {
                    if (previousStep[i] == keyValue && i != keyIndex) {
                        insertPosition = i;
                        break;
                    }
                }
                
                // If we found the insertion position, animate it
                if (insertPosition != -1 && bars != null && keyIndex < bars.length && insertPosition < bars.length) {
                    // Highlight the key element in purple
                    FillTransition highlightKey = new FillTransition(Duration.millis(300), bars[keyIndex]);
                    highlightKey.setFromValue((Color) bars[keyIndex].getFill());
                    highlightKey.setToValue(KEY_COLOR);
                    sequentialTransition.getChildren().add(highlightKey);
                    
                    // Pause to show the key
                    PauseTransition keyPause = new PauseTransition(Duration.millis(300));
                    sequentialTransition.getChildren().add(keyPause);
                    
                    // Show comparisons (if any)
                    int compareStart = Math.min(keyIndex, insertPosition);
                    int compareEnd = Math.max(keyIndex, insertPosition);
                    
                    // Animate comparisons
                    ParallelTransition comparisonAnimation = new ParallelTransition();
                    for (int i = compareStart; i <= compareEnd; i++) {
                        if (i != keyIndex) {
                            FillTransition ft = new FillTransition(Duration.millis(300), bars[i]);
                            ft.setFromValue((Color) bars[i].getFill());
                            ft.setToValue(COMPARING_COLOR);
                            comparisonAnimation.getChildren().add(ft);
                            comparisons++;
                        }
                    }
                    
                    if (comparisonAnimation.getChildren().size() > 0) {
                        sequentialTransition.getChildren().add(comparisonAnimation);
                        Platform.runLater(() -> comparisonsLabel.setText("Comparisons: " + comparisons));
                    }
                    
                    // Animate the insertion (movement of the key)
                    TranslateTransition moveKey = new TranslateTransition(Duration.millis(500), bars[keyIndex]);
                    double distance = (insertPosition - keyIndex) * (BAR_WIDTH + SPACING);
                    moveKey.setByX(distance);
                    
                    // Change color to inserting during movement
                    FillTransition insertColor = new FillTransition(Duration.millis(200), bars[keyIndex]);
                    insertColor.setFromValue(KEY_COLOR);
                    insertColor.setToValue(INSERTING_COLOR);
                    
                    ParallelTransition insertAnimation = new ParallelTransition(moveKey, insertColor);
                    sequentialTransition.getChildren().add(insertAnimation);
                    
                    // Reset colors after insertion
                    PauseTransition postInsertPause = new PauseTransition(Duration.millis(100));
                    FillTransition resetKey = new FillTransition(Duration.millis(300), bars[keyIndex]);
                    resetKey.setFromValue(INSERTING_COLOR);
                    resetKey.setToValue(DEFAULT_COLOR);
                    
                    ParallelTransition resetComparisons = new ParallelTransition();
                    for (int i = compareStart; i <= compareEnd; i++) {
                        if (i != keyIndex) {
                            FillTransition ft = new FillTransition(Duration.millis(300), bars[i]);
                            ft.setFromValue(COMPARING_COLOR);
                            ft.setToValue(DEFAULT_COLOR);
                            resetComparisons.getChildren().add(ft);
                        }
                    }
                    
                    TranslateTransition resetPosition = new TranslateTransition(Duration.ZERO, bars[keyIndex]);
                    resetPosition.setToX(0);
                    
                    SequentialTransition postInsert = new SequentialTransition(
                        postInsertPause, resetKey, resetComparisons, resetPosition
                    );
                    
                    postInsert.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();
                        
                        insertions++;
                        Platform.runLater(() -> {
                            insertionsLabel.setText("Insertions: " + insertions);
                            this.currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + this.currentStep);
                        });
                    });
                    
                    sequentialTransition.getChildren().add(postInsert);
                }
            } else {
                // No movement in this step, just update the array
                PauseTransition noMovePause = new PauseTransition(Duration.millis(600));
                noMovePause.setOnFinished(e -> {
                    array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                    updateVisualization();
                    Platform.runLater(() -> {
                        this.currentStep = currentStepIndex;
                        stepLabel.setText("Step: " + this.currentStep);
                    });
                });
                sequentialTransition.getChildren().add(noMovePause);
            }
        }

        // Final sorted marking
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
            insertions = 0;
        }

        if (currentStep < steps.size() - 1) {
            currentStep++;
            int[] previous = steps.get(currentStep - 1);
            int[] current = steps.get(currentStep);
            array = Arrays.copyOf(current, current.length);
            updateVisualization();

            // Find which index is the key being inserted
            int keyIndex = -1;
            int insertPosition = -1;
            
            // Find the key index (the element that was moved)
            for (int i = 0; i < previous.length; i++) {
                if (i < current.length && previous[i] != current[i]) {
                    keyIndex = i;
                    break;
                }
            }
            
            // If we found a key, find where it was inserted
            if (keyIndex != -1) {
                int keyValue = current[keyIndex];
                
                // Find where this value was in the previous step
                for (int i = 0; i < previous.length; i++) {
                    if (previous[i] == keyValue && i != keyIndex) {
                        insertPosition = i;
                        break;
                    }
                }
                
                // Reset all bars to DEFAULT_COLOR before highlighting
                for (Rectangle bar : bars) {
                    bar.setFill(DEFAULT_COLOR);
                }
                
                if (keyIndex != -1 && insertPosition != -1 && bars != null && 
                    keyIndex < bars.length && insertPosition < bars.length) {
                    
                    // Highlight the key element in purple
                    bars[keyIndex].setFill(KEY_COLOR);
                    
                    // Highlight comparisons in orange
                    int compareStart = Math.min(keyIndex, insertPosition);
                    int compareEnd = Math.max(keyIndex, insertPosition);
                    
                    for (int i = compareStart; i <= compareEnd; i++) {
                        if (i != keyIndex) {
                            bars[i].setFill(COMPARING_COLOR);
                            comparisons++;
                        }
                    }
                    
                    // Highlight insertion in red
                    bars[keyIndex].setFill(INSERTING_COLOR);
                    insertions++;
                }
            }

            // Update labels
            stepLabel.setText("Step: " + currentStep);
            comparisonsLabel.setText("Comparisons: " + comparisons);
            insertionsLabel.setText("Insertions: " + insertions);

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
        insertions = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        insertionsLabel.setText("Insertions: 0");
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
        insertions = 0;
        currentStep = 0;
        
        comparisonsLabel.setText("Comparisons: 0");
        insertionsLabel.setText("Insertions: 0");
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
        dialog.setHeaderText("Insertion Sort Input");
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
                insertions = 0;
                currentStep = 0;
                
                comparisonsLabel.setText("Comparisons: 0");
                insertionsLabel.setText("Insertions: 0");
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