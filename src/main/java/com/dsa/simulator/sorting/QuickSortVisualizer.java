package com.dsa.simulator.sorting;

import com.dsa.algorithms.sorting.QuickSort;
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

public class QuickSortVisualizer extends Application {
    private static final int MAX_BAR_HEIGHT = 300;
    private static final int BAR_WIDTH = 40;
    private static final int SPACING = 10;
    
    private final Sortable algo = new QuickSort();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length);
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
    
    // Color definitions for Quick Sort
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color COMPARING_COLOR = Color.web("#ffa726"); // Orange
    private final Color SWAPPING_COLOR = Color.web("#ef5350"); // Red
    private final Color PIVOT_COLOR = Color.web("#ab47bc"); // Purple
    private final Color PARTITION_COLOR = Color.web("#29b6f6"); // Light Blue
    private final Color SORTED_COLOR = Color.web("#66bb6a"); // Green
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        visualizationBox = new HBox(SPACING);
        visualizationBox.setAlignment(Pos.BOTTOM_CENTER);
        visualizationBox.setPadding(new Insets(20, 0, 40, 0));
        visualizationBox.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        updateVisualization();
        
        ScrollPane scrollPane = new ScrollPane(visualizationBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        root.setCenter(scrollPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Quick Sort Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
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

        steps = algo.sortWithSteps(Arrays.copyOf(array, array.length));
        comparisons = 0;
        swaps = 0;
        currentStep = 0;

        sequentialTransition = new SequentialTransition();

        for (int step = 1; step < steps.size(); step++) {
            final int currentStepIndex = step;
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

            // Find changed indices (for Quick Sort, we need to detect pivot and swaps)
            int changedIndex1 = -1;
            int changedIndex2 = -1;
            boolean isSwap = false;

            // Detect which elements changed
            for (int i = 0; i < previousStep.length; i++) {
                if (previousStep[i] != currentStepArray[i]) {
                    if (changedIndex1 == -1) {
                        changedIndex1 = i;
                    } else {
                        changedIndex2 = i;
                        isSwap = true;
                        break;
                    }
                }
            }

            // Quick Sort specific visualization
            if (changedIndex1 != -1 && bars != null && changedIndex1 < bars.length) {
                // Highlight the pivot element in purple
                int pivotIndex = findPivotIndex(previousStep, currentStepArray);
                if (pivotIndex != -1) {
                    FillTransition pivotHighlight = new FillTransition(Duration.millis(300), bars[pivotIndex]);
                    pivotHighlight.setFromValue((Color) bars[pivotIndex].getFill());
                    pivotHighlight.setToValue(PIVOT_COLOR);
                    sequentialTransition.getChildren().add(pivotHighlight);
                }

                if (isSwap && changedIndex2 != -1 && changedIndex2 < bars.length) {
                    // Highlight comparing elements in orange
                    FillTransition compare1 = new FillTransition(Duration.millis(300), bars[changedIndex1]);
                    compare1.setFromValue((Color) bars[changedIndex1].getFill());
                    compare1.setToValue(COMPARING_COLOR);
                    FillTransition compare2 = new FillTransition(Duration.millis(300), bars[changedIndex2]);
                    compare2.setFromValue((Color) bars[changedIndex2].getFill());
                    compare2.setToValue(COMPARING_COLOR);
                    ParallelTransition compareHighlight = new ParallelTransition(compare1, compare2);
                    sequentialTransition.getChildren().add(compareHighlight);

                    comparisons++;
                    Platform.runLater(() -> comparisonsLabel.setText("Comparisons: " + comparisons));

                    // Swap animation
                    FillTransition swapColor1 = new FillTransition(Duration.millis(200), bars[changedIndex1]);
                    swapColor1.setFromValue(COMPARING_COLOR);
                    swapColor1.setToValue(SWAPPING_COLOR);
                    FillTransition swapColor2 = new FillTransition(Duration.millis(200), bars[changedIndex2]);
                    swapColor2.setFromValue(COMPARING_COLOR);
                    swapColor2.setToValue(SWAPPING_COLOR);
                    ParallelTransition swapColor = new ParallelTransition(swapColor1, swapColor2);

                    TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), bars[changedIndex1]);
                    TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), bars[changedIndex2]);

                    double distance = (changedIndex2 - changedIndex1) * (BAR_WIDTH + SPACING);
                    tt1.setByX(distance);
                    tt2.setByX(-distance);

                    ParallelTransition swapAnimation = new ParallelTransition(tt1, tt2);

                    // Reset after swap
                    PauseTransition pause = new PauseTransition(Duration.millis(100));
                    FillTransition reset1 = new FillTransition(Duration.millis(300), bars[changedIndex1]);
                    reset1.setFromValue(SWAPPING_COLOR);
                    reset1.setToValue(DEFAULT_COLOR);
                    FillTransition reset2 = new FillTransition(Duration.millis(300), bars[changedIndex2]);
                    reset2.setFromValue(SWAPPING_COLOR);
                    reset2.setToValue(DEFAULT_COLOR);
                    ParallelTransition resetColors = new ParallelTransition(reset1, reset2);

                    TranslateTransition resetPos1 = new TranslateTransition(Duration.ZERO, bars[changedIndex1]);
                    TranslateTransition resetPos2 = new TranslateTransition(Duration.ZERO, bars[changedIndex2]);
                    resetPos1.setToX(0);
                    resetPos2.setToX(0);
                    ParallelTransition resetPositions = new ParallelTransition(resetPos1, resetPos2);

                    SequentialTransition fullSwap = new SequentialTransition(
                        swapColor, swapAnimation, pause, resetColors, resetPositions
                    );

                    fullSwap.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();

                        swaps++;
                        Platform.runLater(() -> {
                            swapsLabel.setText("Swaps: " + swaps);
                            currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + currentStep);
                        });
                    });

                    sequentialTransition.getChildren().add(fullSwap);
                } else {
                    // Single element movement (pivot placement)
                    FillTransition elementMove = new FillTransition(Duration.millis(300), bars[changedIndex1]);
                    elementMove.setFromValue((Color) bars[changedIndex1].getFill());
                    elementMove.setToValue(PARTITION_COLOR);
                    
                    SequentialTransition singleMove = new SequentialTransition(elementMove);
                    singleMove.setOnFinished(e -> {
                        array = Arrays.copyOf(currentStepArray, currentStepArray.length);
                        updateVisualization();
                        Platform.runLater(() -> {
                            currentStep = currentStepIndex;
                            stepLabel.setText("Step: " + currentStep);
                        });
                    });
                    sequentialTransition.getChildren().add(singleMove);
                }
            }
        }

        // Final sorted state
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

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private int findPivotIndex(int[] previous, int[] current) {
        // In Quick Sort, the pivot is typically placed in its final position
        // Look for an element that moved to a position where all left are smaller and right are larger
        for (int i = 0; i < current.length; i++) {
            if (previous[i] != current[i]) {
                // This could be the pivot being placed
                return i;
            }
        }
        return -1;
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

            // Find changed indices
            int changedIndex1 = -1;
            int changedIndex2 = -1;
            boolean isSwap = false;

            for (int i = 0; i < previous.length; i++) {
                if (previous[i] != current[i]) {
                    if (changedIndex1 == -1) {
                        changedIndex1 = i;
                    } else {
                        changedIndex2 = i;
                        isSwap = true;
                        break;
                    }
                }
            }

            // Reset all bars to default
            for (Rectangle bar : bars) {
                bar.setFill(DEFAULT_COLOR);
            }

            // Highlight pivot
            int pivotIndex = findPivotIndex(previous, current);
            if (pivotIndex != -1) {
                bars[pivotIndex].setFill(PIVOT_COLOR);
            }

            if (changedIndex1 != -1 && bars != null && changedIndex1 < bars.length) {
                if (isSwap && changedIndex2 != -1 && changedIndex2 < bars.length) {
                    // Highlight comparison and swap
                    bars[changedIndex1].setFill(COMPARING_COLOR);
                    bars[changedIndex2].setFill(COMPARING_COLOR);
                    comparisons++;

                    bars[changedIndex1].setFill(SWAPPING_COLOR);
                    bars[changedIndex2].setFill(SWAPPING_COLOR);
                    swaps++;
                } else {
                    // Single element movement
                    bars[changedIndex1].setFill(PARTITION_COLOR);
                }
            }

            stepLabel.setText("Step: " + currentStep);
            comparisonsLabel.setText("Comparisons: " + comparisons);
            swapsLabel.setText("Swaps: " + swaps);

            if (currentStep == steps.size() - 1) {
                statusLabel.setText("Status: Complete");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");

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
        
        Random rand = new Random();
        array = new int[7];
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(90) + 10;
        }
        
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
        dialog.setHeaderText("Quick Sort Input");
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