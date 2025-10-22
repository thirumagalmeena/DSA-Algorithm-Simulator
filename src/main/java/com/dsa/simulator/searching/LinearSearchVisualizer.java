package com.dsa.simulator.searching;

import com.dsa.algorithms.searching.LinearSearch;
import com.dsa.algorithms.searching.Searchable;
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
import java.util.Random;

public class LinearSearchVisualizer extends Application {
    private static final int SQUARE_SIZE = 50;
    private static final int SPACING = 10;
    
    private final Searchable algo = new LinearSearch();
    private int[] array = {64, 34, 25, 12, 22, 11, 90};
    private int[] originalArray = Arrays.copyOf(array, array.length);
    private int[] searchSteps;
    private int currentStep = 0;
    private boolean isSearching = false;
    private boolean isPaused = false;
    private int targetValue = 22;
    private int foundIndex = -1;
    
    private Rectangle[] squares;
    private int comparisons = 0;
    
    private Label comparisonsLabel;
    private Label statusLabel;
    private Label stepLabel;
    private Label targetLabel;
    private Label resultLabel;
    private HBox visualizationBox;
    private Button startSearchBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Button inputArrayBtn;
    private Slider speedSlider;
    private TextField targetInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for Linear Search
    private final Color DEFAULT_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color CHECKING_COLOR = Color.web("#ffa726"); // Orange - currently checking
    private final Color FOUND_COLOR = Color.web("#66bb6a"); // Green - target found
    private final Color NOT_FOUND_COLOR = Color.web("#ef5350"); // Red - target not found
    private final Color VISITED_COLOR = Color.web("#9575cd"); // Purple - already checked
    
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
        
        VBox centerContainer = new VBox();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setPadding(new Insets(100, 0, 0, 0)); // Added top padding to lower the squares
        centerContainer.getChildren().add(visualizationBox);
        centerContainer.setStyle("-fx-background-color: #3c3f41;");
        
        ScrollPane scrollPane = new ScrollPane(centerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        root.setCenter(scrollPane);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Linear Search Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setPadding(new Insets(10, 15, 15, 15));
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        comparisonsLabel = createStyledLabel("Comparisons: 0");
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        targetLabel = createStyledLabel("Target: " + targetValue);
        resultLabel = createStyledLabel("Result: Not Searched");
        
        infoPanel.getChildren().addAll(comparisonsLabel, statusLabel, stepLabel, targetLabel, resultLabel);
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
        
        // Target input
        VBox targetControl = new VBox(5);
        targetControl.setAlignment(Pos.CENTER);
        Label targetInputLabel = createStyledLabel("Target Value:");
        targetInputLabel.setTextFill(Color.LIGHTGRAY);
        
        targetInput = new TextField(String.valueOf(targetValue));
        targetInput.setPrefWidth(80);
        targetInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        targetControl.getChildren().addAll(targetInputLabel, targetInput);
        
        // Buttons
        startSearchBtn = createStyledButton("Start Search");
        startSearchBtn.setOnAction(e -> startAutoSearch());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        Button generateArrayBtn = createStyledButton("Generate Array");
        generateArrayBtn.setOnAction(e -> generateNewArray());
        
        inputArrayBtn = createStyledButton("Input Array");
        inputArrayBtn.setOnAction(e -> showArrayInputDialog());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseSearching());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeSearching());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replaySearching());
        
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
            targetControl, startSearchBtn, nextStepBtn, resetBtn, 
            generateArrayBtn, inputArrayBtn, pauseBtn, resumeBtn, replayBtn, speedControl
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
        squares = new Rectangle[array.length];
        
        for (int i = 0; i < array.length; i++) {
            Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
            
            // Set color based on search state
            if (foundIndex != -1 && i == foundIndex) {
                square.setFill(FOUND_COLOR);
            } else if (currentStep > 0 && i < searchSteps[currentStep - 1]) {
                square.setFill(VISITED_COLOR); // Already checked
            } else {
                square.setFill(DEFAULT_COLOR);
            }
            
            square.setArcWidth(5);
            square.setArcHeight(5);
            
            VBox squareContainer = new VBox(5);
            squareContainer.setAlignment(Pos.CENTER);
            
            Label valueLabel = new Label(String.valueOf(array[i]));
            valueLabel.setTextFill(Color.WHITE);
            valueLabel.setStyle("-fx-font-weight: bold;");
            
            // Highlight if this is the target value
            if (array[i] == targetValue) {
                valueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffa726;");
            }
            
            squareContainer.getChildren().addAll(valueLabel, square);
            visualizationBox.getChildren().add(squareContainer);
            squares[i] = square;
        }
    }
    
    private void startAutoSearch() {
        if (isSearching) return;
        
        try {
            targetValue = Integer.parseInt(targetInput.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for target value.");
            return;
        }

        isSearching = true;
        isPaused = false;
        foundIndex = -1;
        statusLabel.setText("Status: Searching...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");
        resultLabel.setText("Result: Searching...");

        startSearchBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        targetInput.setDisable(true);

        // Perform search to get steps
        algo.search(Arrays.copyOf(array, array.length), targetValue);
        searchSteps = algo.getSearchSteps();
        comparisons = 0;
        currentStep = 0;

        sequentialTransition = new SequentialTransition();

        // Animate through each search step
        for (int step = 0; step < searchSteps.length; step++) {
            final int currentStepIndex = step;
            int currentIndex = searchSteps[step];
            
            // Reset previous checking element
            PauseTransition preStepReset = new PauseTransition(Duration.ZERO);
            preStepReset.setOnFinished(e -> {
                for (int i = 0; i < squares.length; i++) {
                    if (i == currentIndex) {
                        // This will be highlighted in the next animation
                        continue;
                    }
                    if (foundIndex == -1 && i < currentIndex) {
                        squares[i].setFill(VISITED_COLOR); // Already checked
                    }
                }
            });
            sequentialTransition.getChildren().add(preStepReset);

            // Highlight current checking element
            if (currentIndex < squares.length) {
                FillTransition highlight = new FillTransition(Duration.millis(300), squares[currentIndex]);
                highlight.setFromValue((Color) squares[currentIndex].getFill());
                highlight.setToValue(CHECKING_COLOR);
                
                SequentialTransition checkStep = new SequentialTransition(highlight);
                checkStep.setOnFinished(e -> {
                    comparisons++;
                    Platform.runLater(() -> {
                        comparisonsLabel.setText("Comparisons: " + comparisons);
                        this.currentStep = currentStepIndex + 1;
                        stepLabel.setText("Step: " + this.currentStep);
                        
                        // Check if target found
                        if (array[currentIndex] == targetValue) {
                            foundIndex = currentIndex;
                            squares[currentIndex].setFill(FOUND_COLOR);
                            resultLabel.setText("Result: Found at index " + foundIndex);
                        }
                    });
                });
                
                sequentialTransition.getChildren().add(checkStep);
                
                // If found, break out of animation loop
                if (array[currentIndex] == targetValue) {
                    break;
                }
            }
        }

        // Final state
        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                if (foundIndex == -1) {
                    statusLabel.setText("Status: Not Found");
                    statusLabel.setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold;");
                    resultLabel.setText("Result: Target not found");
                    
                    // Mark all as visited (red)
                    for (Rectangle square : squares) {
                        square.setFill(NOT_FOUND_COLOR);
                    }
                } else {
                    statusLabel.setText("Status: Found");
                    statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                }
                
                isSearching = false;
                startSearchBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                targetInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isSearching) return;

        try {
            targetValue = Integer.parseInt(targetInput.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for target value.");
            return;
        }

        if (searchSteps == null) {
            algo.search(Arrays.copyOf(array, array.length), targetValue);
            searchSteps = algo.getSearchSteps();
            currentStep = 0;
            comparisons = 0;
            foundIndex = -1;
        }

        if (currentStep < searchSteps.length) {
            int currentIndex = searchSteps[currentStep];
            
            // Reset all squares to appropriate colors
            for (int i = 0; i < squares.length; i++) {
                if (i == currentIndex) {
                    squares[i].setFill(CHECKING_COLOR);
                } else if (i < currentIndex) {
                    squares[i].setFill(VISITED_COLOR);
                } else {
                    squares[i].setFill(DEFAULT_COLOR);
                }
            }
            
            comparisons++;
            currentStep++;
            
            // Check if target found
            if (array[currentIndex] == targetValue) {
                foundIndex = currentIndex;
                squares[currentIndex].setFill(FOUND_COLOR);
                resultLabel.setText("Result: Found at index " + foundIndex);
                statusLabel.setText("Status: Found");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
            }
            
            stepLabel.setText("Step: " + currentStep);
            comparisonsLabel.setText("Comparisons: " + comparisons);
            targetLabel.setText("Target: " + targetValue);
            
            // If reached end without finding
            if (currentStep == searchSteps.length && foundIndex == -1) {
                statusLabel.setText("Status: Not Found");
                statusLabel.setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold;");
                resultLabel.setText("Result: Target not found");
                
                for (Rectangle square : squares) {
                    square.setFill(NOT_FOUND_COLOR);
                }
            }
        }
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        searchSteps = null;
        comparisons = 0;
        currentStep = 0;
        foundIndex = -1;
        
        comparisonsLabel.setText("Comparisons: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        resultLabel.setText("Result: Not Searched");
        targetLabel.setText("Target: " + targetValue);
        
        updateVisualization();
        
        isSearching = false;
        isPaused = false;
        startSearchBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        targetInput.setDisable(false);
    }
    
    private void generateNewArray() {
        Random rand = new Random();
        array = new int[10]; // Slightly larger array for search
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(90) + 10; // Values between 10 and 100
        }
        originalArray = Arrays.copyOf(array, array.length);
        targetValue = array[rand.nextInt(array.length)]; // Set a random value from array as target
        targetInput.setText(String.valueOf(targetValue));
        updateVisualization();
        resetVisualization();
    }
    
    private void showArrayInputDialog() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Input Array");
        dialog.setHeaderText("Enter array values (comma-separated integers)");

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the input field
        TextField inputField = new TextField();
        inputField.setPromptText("e.g., 10,20,30,40,50");
        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Array values:"), inputField);
        dialog.getDialogPane().setContent(content);

        // Convert the result to an int array when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    String[] inputValues = inputField.getText().trim().split("\\s*,\\s*");
                    int[] newArray = new int[inputValues.length];
                    for (int i = 0; i < inputValues.length; i++) {
                        newArray[i] = Integer.parseInt(inputValues[i]);
                    }
                    return newArray;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid comma-separated integers.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newArray -> {
            if (newArray != null && newArray.length > 0) {
                array = newArray;
                originalArray = Arrays.copyOf(array, array.length);
                targetValue = array[new Random().nextInt(array.length)];
                targetInput.setText(String.valueOf(targetValue));
                updateVisualization();
                resetVisualization();
            }
        });
    }
    
    private void replaySearching() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        array = Arrays.copyOf(originalArray, originalArray.length);
        
        searchSteps = null;
        comparisons = 0;
        currentStep = 0;
        foundIndex = -1;
        
        comparisonsLabel.setText("Comparisons: 0");
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        resultLabel.setText("Result: Not Searched");
        targetLabel.setText("Target: " + targetValue);
        
        updateVisualization();
        
        isSearching = false;
        isPaused = false;
        startSearchBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        targetInput.setDisable(false);
        
        startAutoSearch();
    }
    
    private void pauseSearching() {
        if (sequentialTransition != null && isSearching) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeSearching() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Searching...");
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