package com.dsa.simulator.dynamicProgramming;

import com.dsa.algorithms.dynamicProgramming.CoinChange;
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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CoinChangeVisualizer extends Application {
    private static final int CELL_SIZE = 40;
    private static final int CELL_SPACING = 2;
    
    private final CoinChange algo = new CoinChange();
    private int[] coins = {1, 2, 5};
    private int amount = 5;
    private int[] originalCoins = Arrays.copyOf(coins, coins.length);
    private int originalAmount = amount;
    
    private List<int[]> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int result = 0;
    private boolean isMinCoinsMode = true; // true for min coins, false for count ways
    
    private Rectangle[] dpTableCells;
    private Text[] dpTableTexts;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label amountLabel;
    private Label resultLabel;
    private Label coinsLabel;
    private Label modeLabel;
    private Label currentCoinLabel;
    private HBox dpTableContainer;
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateDataBtn;
    private Button toggleModeBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField amountInput;
    private TextField coinsInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions for Coin Change DP Table
    private final Color DEFAULT_CELL_COLOR = Color.web("#4fc3f7"); // Blue
    private final Color CURRENT_CELL_COLOR = Color.web("#ffa726"); // Orange - current cell being computed
    private final Color UPDATED_CELL_COLOR = Color.web("#66bb6a"); // Green - cell that was updated
    private final Color REFERENCE_CELL_COLOR = Color.web("#9575cd"); // Purple - referenced cell
    private final Color HEADER_COLOR = Color.web("#78909c"); // Gray - headers
    private final Color INFINITY_COLOR = Color.web("#ef5350"); // Red - infinity/unreachable
    
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
        
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Coin Change Problem Visualizer");
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
        amountLabel = createStyledLabel("Amount: " + amount);
        resultLabel = createStyledLabel("Result: 0");
        coinsLabel = createStyledLabel("Coins: " + Arrays.toString(coins));
        modeLabel = createStyledLabel("Mode: Minimum Coins");
        currentCoinLabel = createStyledLabel("Current: -");
        
        infoPanel.getChildren().addAll(statusLabel, stepLabel, amountLabel, resultLabel, coinsLabel, modeLabel, currentCoinLabel);
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
        
        Label tableLabel = new Label("Dynamic Programming Table");
        tableLabel.setTextFill(Color.WHITE);
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        dpTableContainer = new HBox(CELL_SPACING);
        dpTableContainer.setAlignment(Pos.CENTER);
        dpTableContainer.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(dpTableContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(150);
        scrollPane.setStyle("-fx-background: #3c3f41; -fx-border-color: #3c3f41;");
        
        visualizationArea.getChildren().addAll(tableLabel, scrollPane);
        updateDPTable();
        
        return visualizationArea;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        // Amount input
        VBox amountControl = new VBox(5);
        amountControl.setAlignment(Pos.CENTER);
        Label amountLabel = createStyledLabel("Amount:");
        amountLabel.setTextFill(Color.LIGHTGRAY);
        
        amountInput = new TextField(String.valueOf(amount));
        amountInput.setPrefWidth(80);
        amountInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        amountControl.getChildren().addAll(amountLabel, amountInput);
        
        // Coins input
        VBox coinsControl = new VBox(5);
        coinsControl.setAlignment(Pos.CENTER);
        Label coinsInputLabel = createStyledLabel("Coins (comma-separated):");
        coinsInputLabel.setTextFill(Color.LIGHTGRAY);
        
        coinsInput = new TextField(Arrays.toString(coins).replaceAll("[\\[\\]]", ""));
        coinsInput.setPrefWidth(120);
        coinsInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        coinsControl.getChildren().addAll(coinsInputLabel, coinsInput);
        
        // Buttons
        startComputeBtn = createStyledButton("Start Compute");
        startComputeBtn.setOnAction(e -> startAutoCompute());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        generateDataBtn = createStyledButton("Generate Data");
        generateDataBtn.setOnAction(e -> generateNewData());
        
        toggleModeBtn = createStyledButton("Switch to Count Ways");
        toggleModeBtn.setOnAction(e -> toggleMode());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseComputation());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeComputation());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayComputation());
        
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
            amountControl, coinsControl, startComputeBtn, nextStepBtn, resetBtn, 
            generateDataBtn, toggleModeBtn, pauseBtn, resumeBtn, replayBtn, speedControl
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
    
    private void updateDPTable() {
        dpTableContainer.getChildren().clear();
        
        if (steps == null || steps.isEmpty()) {
            // Show empty table structure
            createTableStructure(amount);
            return;
        }
        
        int currentStepIndex = Math.min(currentStep, steps.size() - 1);
        createTableStructure(amount);
        
        // Fill the table with values up to current step
        int[] currentStepData = steps.get(currentStepIndex);
        for (int i = 0; i <= amount; i++) {
            if (i < currentStepData.length) {
                int value = currentStepData[i];
                if (isMinCoinsMode && value > amount && value < Integer.MAX_VALUE) {
                    dpTableTexts[i].setText("∞");
                    dpTableCells[i].setFill(INFINITY_COLOR);
                } else {
                    dpTableTexts[i].setText(String.valueOf(value));
                    dpTableCells[i].setFill(DEFAULT_CELL_COLOR);
                }
            }
        }
        
        // Highlight current step if we're in the middle of computation
        if (currentStepIndex > 0 && currentStepIndex < steps.size()) {
            highlightCurrentStep(currentStepIndex);
        }
    }
    
    private void createTableStructure(int amount) {
        dpTableCells = new Rectangle[amount + 1];
        dpTableTexts = new Text[amount + 1];
        
        // Create header
        Rectangle headerCell = new Rectangle(CELL_SIZE, CELL_SIZE);
        headerCell.setFill(HEADER_COLOR);
        headerCell.setStroke(Color.WHITE);
        headerCell.setStrokeWidth(1);
        
        Text headerText = new Text("Amount");
        headerText.setFill(Color.WHITE);
        headerText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        
        StackPane headerContainer = new StackPane();
        headerContainer.getChildren().addAll(headerCell, headerText);
        dpTableContainer.getChildren().add(headerContainer);
        
        // Create data cells
        for (int i = 0; i <= amount; i++) {
            Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
            cell.setFill(DEFAULT_CELL_COLOR);
            cell.setStroke(Color.WHITE);
            cell.setStrokeWidth(1);
            
            Text text = new Text("");
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            
            // Create amount label
            Rectangle amountCell = new Rectangle(CELL_SIZE, CELL_SIZE);
            amountCell.setFill(HEADER_COLOR);
            amountCell.setStroke(Color.WHITE);
            amountCell.setStrokeWidth(1);
            
            Text amountText = new Text(String.valueOf(i));
            amountText.setFill(Color.WHITE);
            amountText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            VBox cellGroup = new VBox(1);
            cellGroup.setAlignment(Pos.CENTER);
            
            StackPane amountContainer = new StackPane();
            amountContainer.getChildren().addAll(amountCell, amountText);
            
            StackPane valueContainer = new StackPane();
            valueContainer.getChildren().addAll(cell, text);
            
            cellGroup.getChildren().addAll(amountContainer, valueContainer);
            
            dpTableCells[i] = cell;
            dpTableTexts[i] = text;
            dpTableContainer.getChildren().add(cellGroup);
        }
    }
    
    private void highlightCurrentStep(int stepIndex) {
        if (isMinCoinsMode) {
            // Minimum Coins mode: each step processes one amount
            if (stepIndex <= amount) {
                int currentAmount = stepIndex;
                dpTableCells[currentAmount].setFill(CURRENT_CELL_COLOR);
                
                // Highlight referenced amounts
                for (int coin : coins) {
                    if (currentAmount - coin >= 0) {
                        dpTableCells[currentAmount - coin].setFill(REFERENCE_CELL_COLOR);
                    }
                }
                
                currentCoinLabel.setText("Processing amount: " + currentAmount);
                statusLabel.setText("Computing min coins for amount " + currentAmount);
            }
        } else {
            // Count Ways mode: each step processes one coin
            if (stepIndex > 0 && stepIndex <= coins.length) {
                int currentCoinIndex = stepIndex - 1;
                int currentCoin = coins[currentCoinIndex];
                currentCoinLabel.setText("Processing coin: " + currentCoin);
                statusLabel.setText("Updating ways using coin: " + currentCoin);
                
                // Highlight amounts that were updated by this coin
                for (int i = currentCoin; i <= amount; i++) {
                    dpTableCells[i].setFill(UPDATED_CELL_COLOR);
                    if (i - currentCoin >= 0) {
                        dpTableCells[i - currentCoin].setFill(REFERENCE_CELL_COLOR);
                    }
                }
            }
        }
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            amount = Integer.parseInt(amountInput.getText().trim());
            if (amount <= 0) {
                showAlert("Invalid Input", "Amount must be positive.");
                return;
            }
            
            String coinsText = coinsInput.getText().trim();
            String[] coinStrs = coinsText.split(",");
            coins = new int[coinStrs.length];
            for (int i = 0; i < coinStrs.length; i++) {
                coins[i] = Integer.parseInt(coinStrs[i].trim());
                if (coins[i] <= 0) {
                    showAlert("Invalid Input", "Coins must be positive integers.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for amount and coins.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Computing...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateDataBtn.setDisable(true);
        toggleModeBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        amountInput.setDisable(true);
        coinsInput.setDisable(true);

        // Perform computation to get steps
        if (isMinCoinsMode) {
            result = algo.minCoins(coins, amount);
            steps = algo.getMinCoinsSteps();
        } else {
            result = algo.countWays(coins, amount);
            steps = algo.getWaysSteps();
        }
        currentStep = 0;

        // Update visualization
        updateDPTable();
        
        amountLabel.setText("Amount: " + amount);
        coinsLabel.setText("Coins: " + Arrays.toString(coins));
        resultLabel.setText("Result: " + result);

        sequentialTransition = new SequentialTransition();

        // Animate through each step
        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(800));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateDPTable();
                    
                    // Show current computation details
                    if (isMinCoinsMode) {
                        if (currentStepIndex <= amount) {
                            statusLabel.setText("Processing amount " + currentStepIndex);
                        }
                    } else {
                        if (currentStepIndex > 0 && currentStepIndex <= coins.length) {
                            int coin = coins[currentStepIndex - 1];
                            statusLabel.setText("Processing coin " + coin);
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
                String modeText = isMinCoinsMode ? "Minimum Coins" : "Number of Ways";
                statusLabel.setText("Status: Complete - " + modeText + ": " + result);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateDataBtn.setDisable(false);
                toggleModeBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                amountInput.setDisable(false);
                coinsInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.setRate(3000 / speedSlider.getValue());
        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            amount = Integer.parseInt(amountInput.getText().trim());
            if (amount <= 0) {
                showAlert("Invalid Input", "Amount must be positive.");
                return;
            }
            
            String coinsText = coinsInput.getText().trim();
            String[] coinStrs = coinsText.split(",");
            coins = new int[coinStrs.length];
            for (int i = 0; i < coinStrs.length; i++) {
                coins[i] = Integer.parseInt(coinStrs[i].trim());
                if (coins[i] <= 0) {
                    showAlert("Invalid Input", "Coins must be positive integers.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for amount and coins.");
            return;
        }

        if (steps == null || currentStep == 0) {
            // Initialize computation
            if (isMinCoinsMode) {
                result = algo.minCoins(coins, amount);
                steps = algo.getMinCoinsSteps();
            } else {
                result = algo.countWays(coins, amount);
                steps = algo.getWaysSteps();
            }
            currentStep = 0;
            resultLabel.setText("Result: " + result);
            amountLabel.setText("Amount: " + amount);
            coinsLabel.setText("Coins: " + Arrays.toString(coins));
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateDPTable();
            
            if (isMinCoinsMode) {
                if (currentStep <= amount) {
                    statusLabel.setText("Processing amount " + currentStep);
                }
            } else {
                if (currentStep > 0 && currentStep <= coins.length) {
                    int coin = coins[currentStep - 1];
                    statusLabel.setText("Processing coin " + coin);
                }
            }
            
            if (currentStep == steps.size()) {
                String modeText = isMinCoinsMode ? "Minimum Coins" : "Number of Ways";
                statusLabel.setText("Status: Complete - " + modeText + ": " + result);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
            }
        }
    }
    
    private void toggleMode() {
        isMinCoinsMode = !isMinCoinsMode;
        if (isMinCoinsMode) {
            modeLabel.setText("Mode: Minimum Coins");
            toggleModeBtn.setText("Switch to Count Ways");
        } else {
            modeLabel.setText("Mode: Count Ways");
            toggleModeBtn.setText("Switch to Min Coins");
        }
        resetVisualization();
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        // Reset to original data
        coins = Arrays.copyOf(originalCoins, originalCoins.length);
        amount = originalAmount;
        
        steps = null;
        currentStep = 0;
        result = 0;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        amountLabel.setText("Amount: " + amount);
        resultLabel.setText("Result: 0");
        coinsLabel.setText("Coins: " + Arrays.toString(coins));
        currentCoinLabel.setText("Current: -");
        amountInput.setText(String.valueOf(amount));
        coinsInput.setText(Arrays.toString(coins).replaceAll("[\\[\\]]", ""));
        
        updateDPTable();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateDataBtn.setDisable(false);
        toggleModeBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        amountInput.setDisable(false);
        coinsInput.setDisable(false);
    }
    
    private void generateNewData() {
        if (isComputing) return;
        
        Random rand = new Random();
        amount = rand.nextInt(15) + 5; // 5-19 amount
        
        int coinCount = rand.nextInt(3) + 2; // 2-4 coins
        coins = new int[coinCount];
        for (int i = 0; i < coinCount; i++) {
            coins[i] = rand.nextInt(5) + 1; // 1-5 value
        }
        
        originalCoins = Arrays.copyOf(coins, coins.length);
        originalAmount = amount;
        
        // Reset visualization
        resetVisualization();
    }
    
    private void replayComputation() {
        resetVisualization();
        startAutoCompute();
    }
    
    private void pauseComputation() {
        if (sequentialTransition != null && isComputing) {
            sequentialTransition.pause();
            isPaused = true;
            statusLabel.setText("Status: Paused");
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(false);
        }
    }
    
    private void resumeComputation() {
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