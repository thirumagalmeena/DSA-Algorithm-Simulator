package com.dsa.simulator.dynamicProgramming;

import com.dsa.algorithms.dynamicProgramming.PascalTriangle;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;

public class PascalTriangleVisualizer extends Application {
    private static final int CIRCLE_RADIUS = 25;
    private static final int ROW_SPACING = 60;
    private static final int COL_SPACING = 50;
    
    private final PascalTriangle algo = new PascalTriangle();
    private int numRows = 5;
    private int originalNumRows = numRows;
    
    private List<List<Integer>> steps;
    private int currentStep = 0;
    private boolean isGenerating = false;
    private boolean isPaused = false;
    
    private Circle[][] triangleCircles;
    private Text[][] triangleTexts;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label rowLabel;
    private Label formulaLabel;
    private VBox triangleContainer;
    private Button startGenerateBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateRandomBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField rowsInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for Pascal's Triangle
    private final Color DEFAULT_CIRCLE_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color CURRENT_ROW_COLOR = Color.web("#ffa726"); // Orange - current row being generated
    private final Color COMPUTED_CIRCLE_COLOR = Color.web("#66bb6a"); // Green - computed element
    private final Color REFERENCE_CIRCLE_COLOR = Color.web("#9575cd"); // Purple - referenced elements
    private final Color BORDER_CIRCLE_COLOR = Color.web("#78909c"); // Gray - border elements (1's)
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox infoPanel = createInfoPanel();
        root.setTop(infoPanel);
        
        VBox visualizationArea = createVisualizationArea();
        root.setCenter(visualizationArea);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Pascal's Triangle Visualizer");
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
        rowLabel = createStyledLabel("Rows: " + numRows);
        formulaLabel = createStyledLabel("Formula: C(n,k) = C(n-1,k-1) + C(n-1,k)");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, rowLabel, formulaLabel);
        return infoPanel;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        return label;
    }
    
    private VBox createVisualizationArea() {
        VBox visualizationArea = new VBox(10);
        visualizationArea.setPadding(new Insets(20));
        visualizationArea.setAlignment(Pos.TOP_CENTER);
        visualizationArea.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        
        Label titleLabel = new Label("Pascal's Triangle");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        triangleContainer = new VBox(ROW_SPACING);
        triangleContainer.setAlignment(Pos.CENTER);
        triangleContainer.setPadding(new Insets(20));
        
        ScrollPane scrollPane = new ScrollPane(triangleContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        
        visualizationArea.getChildren().addAll(titleLabel, scrollPane);
        updateTriangleVisualization();
        
        return visualizationArea;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        // Rows input
        VBox rowsControl = new VBox(5);
        rowsControl.setAlignment(Pos.CENTER);
        Label rowsLabel = createStyledLabel("Number of Rows:");
        rowsLabel.setTextFill(Color.LIGHTGRAY);
        
        rowsInput = new TextField(String.valueOf(numRows));
        rowsInput.setPrefWidth(80);
        rowsInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        rowsControl.getChildren().addAll(rowsLabel, rowsInput);
        
        // Buttons
        startGenerateBtn = createStyledButton("Generate");
        startGenerateBtn.setOnAction(e -> startAutoGenerate());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        generateRandomBtn = createStyledButton("Random Rows");
        generateRandomBtn.setOnAction(e -> generateRandomRows());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseGeneration());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeGeneration());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayGeneration());
        
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
            rowsControl, startGenerateBtn, nextStepBtn, resetBtn, 
            generateRandomBtn, pauseBtn, resumeBtn, replayBtn, speedControl
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
    
    private void updateTriangleVisualization() {
        triangleContainer.getChildren().clear();
        
        if (steps == null || steps.isEmpty()) {
            // Show empty triangle structure
            createTriangleStructure(numRows);
            return;
        }
        
        int currentStepIndex = Math.min(currentStep, steps.size() - 1);
        createTriangleStructure(numRows);
        
        // Fill the triangle with values up to current step
        for (int row = 0; row < numRows; row++) {
            if (row <= currentStepIndex) {
                List<Integer> currentRow = steps.get(row);
                for (int col = 0; col < currentRow.size(); col++) {
                    triangleTexts[row][col].setText(String.valueOf(currentRow.get(col)));
                    triangleCircles[row][col].setFill(DEFAULT_CIRCLE_COLOR);
                }
            }
        }
        
        // Highlight current step if we're in the middle of generation
        if (currentStepIndex >= 0 && currentStepIndex < steps.size()) {
            highlightCurrentStep(currentStepIndex);
        }
    }
    
    private void createTriangleStructure(int totalRows) {
        triangleCircles = new Circle[totalRows][];
        triangleTexts = new Text[totalRows][];
        
        for (int row = 0; row < totalRows; row++) {
            HBox rowContainer = new HBox(COL_SPACING);
            rowContainer.setAlignment(Pos.CENTER);
            
            int colsInRow = row + 1;
            triangleCircles[row] = new Circle[colsInRow];
            triangleTexts[row] = new Text[colsInRow];
            
            for (int col = 0; col < colsInRow; col++) {
                // Create circle
                Circle circle = new Circle(CIRCLE_RADIUS);
                circle.setFill(DEFAULT_CIRCLE_COLOR);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(2);
                
                // Create text
                Text text = new Text("");
                text.setFill(Color.WHITE);
                text.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                // Create container for circle and text
                StackPane cellContainer = new StackPane();
                cellContainer.getChildren().addAll(circle, text);
                
                triangleCircles[row][col] = circle;
                triangleTexts[row][col] = text;
                rowContainer.getChildren().add(cellContainer);
            }
            
            triangleContainer.getChildren().add(rowContainer);
        }
    }
    
    private void highlightCurrentStep(int stepIndex) {
        if (stepIndex < 0 || stepIndex >= steps.size()) return;
        
        int currentRow = stepIndex;
        
        // Highlight the entire current row
        if (triangleCircles[currentRow] != null) {
            for (int col = 0; col < triangleCircles[currentRow].length; col++) {
                triangleCircles[currentRow][col].setFill(CURRENT_ROW_COLOR);
            }
        }
        
        // Show row information
        rowLabel.setText("Rows: " + numRows + " | Current Row: " + (currentRow + 1));
        statusLabel.setText("Generating Row " + (currentRow + 1));
        
        // If this is not the first row, highlight the referenced elements from previous row
        if (currentRow > 0) {
            List<Integer> currentRowData = steps.get(currentRow);
            for (int col = 1; col < currentRowData.size() - 1; col++) {
                // Highlight the two parent elements from previous row
                triangleCircles[currentRow - 1][col - 1].setFill(REFERENCE_CIRCLE_COLOR); // left parent
                triangleCircles[currentRow - 1][col].setFill(REFERENCE_CIRCLE_COLOR);     // right parent
                
                // Show the addition formula for this element
                if (col == 1) { // Show formula for first computed element
                    int leftParent = steps.get(currentRow - 1).get(col - 1);
                    int rightParent = steps.get(currentRow - 1).get(col);
                    int currentValue = currentRowData.get(col);
                    formulaLabel.setText("Formula: " + leftParent + " + " + rightParent + " = " + currentValue);
                }
            }
        } else {
            // First row - just ones
            formulaLabel.setText("Formula: Base case - all ones on edges");
        }
    }
    
    private void startAutoGenerate() {
        if (isGenerating) return;
        
        try {
            numRows = Integer.parseInt(rowsInput.getText().trim());
            if (numRows <= 0) {
                showAlert("Invalid Input", "Number of rows must be positive.");
                return;
            }
            if (numRows > 15) {
                showAlert("Input Warning", "For better visualization, please use 15 rows or fewer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of rows.");
            return;
        }

        isGenerating = true;
        isPaused = false;
        statusLabel.setText("Status: Generating Pascal's Triangle...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startGenerateBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateRandomBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        rowsInput.setDisable(true);

        // Generate Pascal's Triangle to get steps
        algo.generate(numRows);
        steps = algo.getSteps();
        currentStep = 0;

        // Update visualization
        updateTriangleVisualization();
        
        rowLabel.setText("Rows: " + numRows);

        sequentialTransition = new SequentialTransition();

        // Animate through each step (each row generation)
        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(1200));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateTriangleVisualization();
                    
                    // Show current generation details
                    int currentRowNum = currentStepIndex + 1;
                    statusLabel.setText("Generated Row " + currentRowNum + " with " + (currentRowNum) + " elements");
                });
            });
            
            sequentialTransition.getChildren().add(stepTransition);
        }

        // Final state
        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete - Generated " + numRows + " rows");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                formulaLabel.setText("Formula: Each element is sum of two elements above it");
                
                isGenerating = false;
                startGenerateBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateRandomBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                rowsInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isGenerating) return;

        try {
            numRows = Integer.parseInt(rowsInput.getText().trim());
            if (numRows <= 0) {
                showAlert("Invalid Input", "Number of rows must be positive.");
                return;
            }
            if (numRows > 15) {
                showAlert("Input Warning", "For better visualization, please use 15 rows or fewer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of rows.");
            return;
        }

        if (steps == null || currentStep == 0) {
            // Initialize generation
            algo.generate(numRows);
            steps = algo.getSteps();
            currentStep = 0;
            rowLabel.setText("Rows: " + numRows);
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateTriangleVisualization();
            
            int currentRowNum = currentStep;
            statusLabel.setText("Generated Row " + currentRowNum + " with " + (currentRowNum) + " elements");
            
            if (currentStep == steps.size()) {
                statusLabel.setText("Status: Complete - Generated " + numRows + " rows");
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                formulaLabel.setText("Formula: Each element is sum of two elements above it");
            }
        }
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        // Reset to original data
        numRows = originalNumRows;
        
        steps = null;
        currentStep = 0;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        rowLabel.setText("Rows: " + numRows);
        formulaLabel.setText("Formula: C(n,k) = C(n-1,k-1) + C(n-1,k)");
        rowsInput.setText(String.valueOf(numRows));
        
        updateTriangleVisualization();
        
        isGenerating = false;
        isPaused = false;
        startGenerateBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateRandomBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        rowsInput.setDisable(false);
    }
    
    private void generateRandomRows() {
        if (isGenerating) return;
        
        numRows = (int) (Math.random() * 8) + 3; // 3-10 rows
        originalNumRows = numRows;
        
        // Reset visualization
        resetVisualization();
    }
    
    private void replayGeneration() {
        resetVisualization();
        startAutoGenerate();
    }
    
    private void pauseGeneration() {
        if (sequentialTransition != null && isGenerating) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeGeneration() {
        if (sequentialTransition != null && isPaused) {
            sequentialTransition.play();
            isPaused = false;
            statusLabel.setText("Status: Generating Pascal's Triangle...");
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