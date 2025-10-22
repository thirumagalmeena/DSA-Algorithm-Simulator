package com.dsa.simulator.greedy;

import com.dsa.algorithms.greedy.JobSchedulingAlgorithm;
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

import java.lang.reflect.Method;
import java.util.*;

public class JobSchedulingVisualizer extends Application {
    private static final int SLOT_WIDTH = 80;
    private static final int SLOT_HEIGHT = 60;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 400;
    
    private final JobSchedulingAlgorithm algo = new JobSchedulingAlgorithm();
    
    // Use wrapper class instead of direct JobSchedulingAlgorithm.Job
    private JobWrapper[] jobs;
    private int numJobs = 6;
    
    private List<JobSchedulingAlgorithm.Step> steps;
    private int currentStep = 0;
    private boolean isComputing = false;
    private boolean isPaused = false;
    private int[] finalSchedule;
    private int totalProfit = 0;
    
    private Rectangle[] timeSlots;
    private Text[] slotLabels;
    private Text[] jobProfitLabels;
    private Text[] jobDeadlineLabels;
    
    private Label statusLabel;
    private Label stepLabel;
    private Label jobsLabel;
    private Label profitLabel;
    private Pane schedulingCanvas;
    private VBox infoPanel;
    private TextArea jobsDisplay;
    private TextArea scheduleDisplay;
    private TextArea stepInfoDisplay;
    
    private Button startComputeBtn;
    private Button nextStepBtn;
    private Button resetBtn;
    private Button generateJobsBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button replayBtn;
    private Slider speedSlider;
    private TextField jobsInput;
    
    private SequentialTransition sequentialTransition;
    
    // Color definitions
    private final Color UNSCHEDULED_SLOT_COLOR = Color.web("#78909c");
    private final Color SCHEDULED_SLOT_COLOR = Color.web("#66bb6a");
    private final Color CURRENT_JOB_COLOR = Color.web("#ffa726");
    private final Color REJECTED_JOB_COLOR = Color.web("#ef5350");
    private final Color JOB_INFO_COLOR = Color.web("#4fc3f7");
    
    // Job wrapper class to handle job creation
    public static class JobWrapper {
        public int id;
        public int deadline;
        public int profit;
        
        public JobWrapper(int id, int deadline, int profit) {
            this.id = id;
            this.deadline = deadline;
            this.profit = profit;
        }
        
        @Override
        public String toString() {
            return String.format("Job %d: Deadline=%d, Profit=%d", id, deadline, profit);
        }
    }
    
    // Convert wrapper jobs to algorithm jobs
    private JobSchedulingAlgorithm.Job[] convertToAlgorithmJobs(JobWrapper[] wrapperJobs) {
        JobSchedulingAlgorithm.Job[] algoJobs = new JobSchedulingAlgorithm.Job[wrapperJobs.length];
        
        try {
            // Attempt 1: Check for a static factory method (e.g., create)
            try {
                Method factoryMethod = JobSchedulingAlgorithm.Job.class.getDeclaredMethod("create", int.class, int.class, int.class);
                factoryMethod.setAccessible(true);
                for (int i = 0; i < wrapperJobs.length; i++) {
                    JobWrapper wrapper = wrapperJobs[i];
                    algoJobs[i] = (JobSchedulingAlgorithm.Job) factoryMethod.invoke(null, wrapper.id, wrapper.deadline, wrapper.profit);
                }
                return algoJobs;
            } catch (NoSuchMethodException e) {
                // Factory method not found, try next approach
            }

            // Attempt 2: Check if algorithm has an addJob method to populate jobs
            try {
                Method addJobMethod = algo.getClass().getDeclaredMethod("addJob", int.class, int.class, int.class);
                addJobMethod.setAccessible(true);
                for (JobWrapper wrapper : wrapperJobs) {
                    addJobMethod.invoke(algo, wrapper.id, wrapper.deadline, wrapper.profit);
                }
                Method getJobsMethod = algo.getClass().getDeclaredMethod("getJobs");
                getJobsMethod.setAccessible(true);
                algoJobs = (JobSchedulingAlgorithm.Job[]) getJobsMethod.invoke(algo);
                return algoJobs;
            } catch (NoSuchMethodException e) {
                // addJob method not found, try next approach
            }

            // Attempt 3: Use constructor via reflection (last resort)
            try {
                java.lang.reflect.Constructor<JobSchedulingAlgorithm.Job> constructor = 
                    (java.lang.reflect.Constructor<JobSchedulingAlgorithm.Job>) 
                    JobSchedulingAlgorithm.Job.class.getDeclaredConstructor(int.class, int.class, int.class);
                constructor.setAccessible(true);
                for (int i = 0; i < wrapperJobs.length; i++) {
                    JobWrapper wrapper = wrapperJobs[i];
                    algoJobs[i] = constructor.newInstance(wrapper.id, wrapper.deadline, wrapper.profit);
                }
                return algoJobs;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No suitable constructor or factory method found for JobSchedulingAlgorithm.Job. Please ensure it has a public constructor (int, int, int) or a static create method.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create algorithm jobs: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2b2b2b;");
        
        initializeDefaultJobs();
        
        HBox topInfoPanel = createTopInfoPanel();
        root.setTop(topInfoPanel);
        
        HBox visualizationArea = createVisualizationArea();
        root.setCenter(visualizationArea);
        
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Job Scheduling Algorithm - Deadline with Profit Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Platform.runLater(() -> updateVisualization());
    }
    
    private void initializeDefaultJobs() {
        numJobs = 6;
        jobs = new JobWrapper[] {
            new JobWrapper(1, 2, 100),
            new JobWrapper(2, 1, 19),
            new JobWrapper(3, 2, 27),
            new JobWrapper(4, 1, 25),
            new JobWrapper(5, 3, 15),
            new JobWrapper(6, 4, 40)
        };
    }
    
    private HBox createTopInfoPanel() {
        HBox topInfoPanel = new HBox(20);
        topInfoPanel.setPadding(new Insets(10, 15, 15, 15));
        topInfoPanel.setAlignment(Pos.CENTER_LEFT);
        topInfoPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        statusLabel = createStyledLabel("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        stepLabel = createStyledLabel("Step: 0");
        jobsLabel = createStyledLabel("Jobs: " + numJobs);
        profitLabel = createStyledLabel("Total Profit: 0");
        
        topInfoPanel.getChildren().addAll(statusLabel, stepLabel, jobsLabel, profitLabel);
        return topInfoPanel;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        return label;
    }
    
    private HBox createVisualizationArea() {
        HBox visualizationArea = new HBox(20);
        visualizationArea.setPadding(new Insets(20));
        visualizationArea.setAlignment(Pos.CENTER);
        visualizationArea.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-radius: 5;");
        
        VBox scheduleArea = new VBox(10);
        scheduleArea.setAlignment(Pos.TOP_CENTER);
        
        Label scheduleLabel = new Label("Job Scheduling Timeline");
        scheduleLabel.setTextFill(Color.WHITE);
        scheduleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        schedulingCanvas = new Pane();
        schedulingCanvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        schedulingCanvas.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        
        scheduleArea.getChildren().addAll(scheduleLabel, schedulingCanvas);
        
        VBox infoArea = new VBox(10);
        infoArea.setAlignment(Pos.TOP_CENTER);
        
        Label infoLabel = new Label("Algorithm Information");
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        infoPanel = createInfoPanel();
        
        infoArea.getChildren().addAll(infoLabel, infoPanel);
        
        visualizationArea.getChildren().addAll(scheduleArea, infoArea);
        
        return visualizationArea;
    }
    
    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(15);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #555; -fx-border-radius: 5;");
        infoPanel.setPrefWidth(350);
        
        VBox jobsBox = new VBox(5);
        Label jobsTitle = createStyledLabel("Jobs (ID, Deadline, Profit):");
        jobsDisplay = new TextArea();
        jobsDisplay.setPrefHeight(120);
        jobsDisplay.setEditable(false);
        jobsDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        jobsBox.getChildren().addAll(jobsTitle, jobsDisplay);
        
        VBox scheduleBox = new VBox(5);
        Label scheduleTitle = createStyledLabel("Current Schedule:");
        scheduleDisplay = new TextArea();
        scheduleDisplay.setPrefHeight(100);
        scheduleDisplay.setEditable(false);
        scheduleDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        scheduleBox.getChildren().addAll(scheduleTitle, scheduleDisplay);
        
        VBox stepInfoBox = new VBox(5);
        Label stepInfoTitle = createStyledLabel("Step Information:");
        stepInfoDisplay = new TextArea();
        stepInfoDisplay.setPrefHeight(80);
        stepInfoDisplay.setEditable(false);
        stepInfoDisplay.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-family: monospace;");
        stepInfoBox.getChildren().addAll(stepInfoTitle, stepInfoDisplay);
        
        infoPanel.getChildren().addAll(jobsBox, scheduleBox, stepInfoBox);
        return infoPanel;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(20, 15, 15, 15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 5;");
        
        VBox jobsControl = new VBox(5);
        jobsControl.setAlignment(Pos.CENTER);
        Label jobsInputLabel = createStyledLabel("Jobs:");
        jobsInputLabel.setTextFill(Color.LIGHTGRAY);
        
        jobsInput = new TextField(String.valueOf(numJobs));
        jobsInput.setPrefWidth(80);
        jobsInput.setStyle("-fx-control-inner-background: #555; -fx-text-fill: white;");
        
        jobsControl.getChildren().addAll(jobsInputLabel, jobsInput);
        
        startComputeBtn = createStyledButton("Start Scheduling");
        startComputeBtn.setOnAction(e -> startAutoCompute());
        
        nextStepBtn = createStyledButton("Next Step");
        nextStepBtn.setOnAction(e -> performNextStep());
        
        resetBtn = createStyledButton("Reset");
        resetBtn.setOnAction(e -> resetVisualization());
        
        generateJobsBtn = createStyledButton("Generate Jobs");
        generateJobsBtn.setOnAction(e -> generateNewJobs());
        
        pauseBtn = createStyledButton("Pause");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseComputation());
        
        resumeBtn = createStyledButton("Resume");
        resumeBtn.setDisable(true);
        resumeBtn.setOnAction(e -> resumeComputation());
        
        replayBtn = createStyledButton("Replay");
        replayBtn.setOnAction(e -> replayComputation());
        
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
            jobsControl, startComputeBtn, nextStepBtn, resetBtn, 
            generateJobsBtn, pauseBtn, resumeBtn, replayBtn, speedControl
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
        schedulingCanvas.getChildren().clear();
        updateInfoDisplays();
        
        if (jobs == null) return;
        
        createTimelineVisualization();
        
        if (steps != null && currentStep > 0 && currentStep <= steps.size()) {
            highlightCurrentStep(currentStep - 1);
        }
    }
    
    private void createTimelineVisualization() {
        // Calculate max deadline from jobs
        int maxDeadline = 0;
        for (JobWrapper job : jobs) {
            maxDeadline = Math.max(maxDeadline, job.deadline);
        }
        
        timeSlots = new Rectangle[maxDeadline];
        slotLabels = new Text[maxDeadline];
        
        double startX = CANVAS_WIDTH * 0.5; // Start timeline at the right half
        double yPos = 50; // Start timeline from top, adjusted for height
        
        for (int i = 0; i < maxDeadline; i++) {
            double x = startX + i * (SLOT_WIDTH + 10);
            if (x + SLOT_WIDTH > CANVAS_WIDTH) break; // Prevent overflow
            
            Rectangle slot = new Rectangle(x, yPos, SLOT_WIDTH, SLOT_HEIGHT);
            slot.setFill(UNSCHEDULED_SLOT_COLOR);
            slot.setStroke(Color.WHITE);
            slot.setStrokeWidth(2);
            
            Text timeLabel = new Text(x + SLOT_WIDTH / 2 - 10, yPos - 20, "Time " + (i + 1));
            timeLabel.setFill(Color.WHITE);
            timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            Text slotLabel = new Text(x + SLOT_WIDTH / 2 - 5, yPos + SLOT_HEIGHT / 2 + 5, "Free");
            slotLabel.setFill(Color.WHITE);
            slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            timeSlots[i] = slot;
            slotLabels[i] = slotLabel;
            
            schedulingCanvas.getChildren().addAll(slot, timeLabel, slotLabel);
        }
        
        createJobInfoPanel();
    }
    
    private void createJobInfoPanel() {
        double startX = 20; // Left side for job details
        double startY = 50; // Start from top
        
        Text jobsHeader = new Text(startX, startY, "Jobs (Sorted by Profit):");
        jobsHeader.setFill(Color.WHITE);
        jobsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        schedulingCanvas.getChildren().add(jobsHeader);
        
        jobProfitLabels = new Text[jobs.length];
        jobDeadlineLabels = new Text[jobs.length];
        
        // Sort jobs by profit for display (same as algorithm)
        JobWrapper[] sortedJobs = Arrays.copyOf(jobs, jobs.length);
        Arrays.sort(sortedJobs, (a, b) -> b.profit - a.profit);
        
        for (int i = 0; i < sortedJobs.length; i++) {
            double y = startY + 30 + i * 25;
            if (y + 25 > CANVAS_HEIGHT) { // Limit to canvas height
                break; // Prevent overflow
            }
            
            Text profitLabel = new Text(startX, y, 
                String.format("Job %d: Profit=%d", sortedJobs[i].id, sortedJobs[i].profit));
            profitLabel.setFill(JOB_INFO_COLOR);
            profitLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            
            Text deadlineLabel = new Text(startX + 150, y, 
                String.format("Deadline=%d", sortedJobs[i].deadline));
            deadlineLabel.setFill(Color.LIGHTGRAY);
            deadlineLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            
            jobProfitLabels[i] = profitLabel;
            jobDeadlineLabels[i] = deadlineLabel;
            
            schedulingCanvas.getChildren().addAll(profitLabel, deadlineLabel);
        }
    }
    
    private void updateInfoDisplays() {
        if (jobs == null) return;
        
        StringBuilder jobsText = new StringBuilder();
        JobWrapper[] sortedJobs = Arrays.copyOf(jobs, jobs.length);
        Arrays.sort(sortedJobs, (a, b) -> b.profit - a.profit);
        
        for (JobWrapper job : sortedJobs) {
            jobsText.append(String.format("Job %d: Deadline=%d, Profit=%d\n", 
                job.id, job.deadline, job.profit));
        }
        jobsDisplay.setText(jobsText.toString());
        
        if (steps != null && currentStep > 0) {
            JobSchedulingAlgorithm.Step currentStepData = steps.get(currentStep - 1);
            updateScheduleDisplay(currentStepData.schedule);
        } else {
            int maxDeadline = 0;
            for (JobWrapper job : jobs) {
                maxDeadline = Math.max(maxDeadline, job.deadline);
            }
            int[] emptySchedule = new int[maxDeadline];
            Arrays.fill(emptySchedule, -1);
            updateScheduleDisplay(emptySchedule);
        }
    }
    
    private void updateScheduleDisplay(int[] schedule) {
        StringBuilder scheduleText = new StringBuilder();
        int currentProfit = 0;
        
        for (int i = 0; i < schedule.length; i++) {
            if (schedule[i] != -1) {
                int jobProfit = 0;
                for (JobWrapper job : jobs) {
                    if (job.id == schedule[i]) {
                        jobProfit = job.profit;
                        break;
                    }
                }
                scheduleText.append(String.format("Time %d: Job %d (Profit=%d)\n", 
                    i + 1, schedule[i], jobProfit));
                currentProfit += jobProfit;
            } else {
                scheduleText.append(String.format("Time %d: Free\n", i + 1));
            }
        }
        
        scheduleText.append("\nTotal Profit: ").append(currentProfit);
        scheduleDisplay.setText(scheduleText.toString());
        profitLabel.setText("Total Profit: " + currentProfit);
    }
    
    private void highlightCurrentStep(int stepIndex) {
        if (stepIndex < 0 || stepIndex >= steps.size()) return;
        
        JobSchedulingAlgorithm.Step step = steps.get(stepIndex);
        
        // Reset all job labels to default color
        for (int i = 0; i < jobProfitLabels.length; i++) {
            if (jobProfitLabels[i] != null) {
                jobProfitLabels[i].setFill(JOB_INFO_COLOR);
            }
        }
        
        // Highlight current job being processed
        for (int i = 0; i < jobProfitLabels.length; i++) {
            if (jobProfitLabels[i] != null) {
                String labelText = jobProfitLabels[i].getText();
                if (labelText.contains("Job " + step.jobId + ":")) {
                    if (step.scheduled) {
                        jobProfitLabels[i].setFill(SCHEDULED_SLOT_COLOR);
                    } else {
                        jobProfitLabels[i].setFill(REJECTED_JOB_COLOR);
                    }
                    break;
                }
            }
        }
        
        // Update timeline slots
        updateTimelineSlots(step.schedule, step.jobId, step.scheduled);
        
        // Update step information
        updateStepInfo(step);
    }
    
    private void updateTimelineSlots(int[] schedule, int currentJobId, boolean scheduled) {
        if (timeSlots == null || schedule == null) return;
        
        // Ensure schedule length matches timeSlots
        int maxSlots = Math.min(timeSlots.length, schedule.length);
        
        // Reset all slots
        for (int i = 0; i < maxSlots; i++) {
            if (timeSlots[i] != null) {
                timeSlots[i].setFill(UNSCHEDULED_SLOT_COLOR);
                if (slotLabels[i] != null) {
                    slotLabels[i].setText("Free");
                    slotLabels[i].setFill(Color.WHITE);
                }
            }
        }
        
        // Update scheduled jobs
        for (int i = 0; i < maxSlots; i++) {
            if (schedule[i] != -1 && timeSlots[i] != null && slotLabels[i] != null) {
                timeSlots[i].setFill(SCHEDULED_SLOT_COLOR);
                slotLabels[i].setText("Job " + schedule[i]);
                
                // Highlight the slot where current job was just scheduled
                if (schedule[i] == currentJobId && scheduled) {
                    timeSlots[i].setFill(CURRENT_JOB_COLOR);
                }
            }
        }
    }
    
    private void updateStepInfo(JobSchedulingAlgorithm.Step step) {
        JobWrapper currentJob = null;
        for (JobWrapper job : jobs) {
            if (job.id == step.jobId) {
                currentJob = job;
                break;
            }
        }
        
        if (currentJob != null) {
            StringBuilder stepInfo = new StringBuilder();
            stepInfo.append("Processing: Job ").append(step.jobId).append("\n");
            stepInfo.append("Profit: ").append(currentJob.profit).append("\n");
            stepInfo.append("Deadline: ").append(currentJob.deadline).append("\n");
            
            if (step.scheduled) {
                stepInfo.append("Status: SCHEDULED at Time ").append(step.slot + 1);
            } else {
                stepInfo.append("Status: REJECTED - No free slot before deadline");
            }
            
            stepInfoDisplay.setText(stepInfo.toString());
        }
        
        statusLabel.setText(step.scheduled ? 
            "Job " + step.jobId + " scheduled successfully" : 
            "Job " + step.jobId + " rejected - no available slot");
    }
    
    private void startAutoCompute() {
        if (isComputing) return;
        
        try {
            numJobs = Integer.parseInt(jobsInput.getText().trim());
            if (numJobs <= 0 || numJobs > 15) {
                showAlert("Invalid Input", "Number of jobs must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of jobs.");
            return;
        }

        isComputing = true;
        isPaused = false;
        statusLabel.setText("Status: Scheduling jobs by profit...");
        statusLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");

        startComputeBtn.setDisable(true);
        nextStepBtn.setDisable(true);
        generateJobsBtn.setDisable(true);
        pauseBtn.setDisable(false);
        resumeBtn.setDisable(true);
        jobsInput.setDisable(true);

        if (jobs == null || jobs.length != numJobs) {
            generateNewJobs();
        }

        JobSchedulingAlgorithm.Job[] algoJobs = convertToAlgorithmJobs(jobs);
        if (algoJobs == null || algoJobs.length == 0) {
            showAlert("Error", "Failed to create algorithm jobs. Please check the JobSchedulingAlgorithm implementation.");
            resetVisualization();
            return;
        }

        finalSchedule = algo.scheduleJobs(algoJobs);
        steps = algo.getSteps();
        totalProfit = algo.getTotalProfit(algoJobs, finalSchedule);
        currentStep = 0;

        updateVisualization();
        
        jobsLabel.setText("Jobs: " + numJobs);

        sequentialTransition = new SequentialTransition();

        for (int step = 0; step < steps.size(); step++) {
            final int currentStepIndex = step;
            
            PauseTransition stepTransition = new PauseTransition(Duration.millis(speedSlider.getValue()));
            stepTransition.setOnFinished(e -> {
                Platform.runLater(() -> {
                    this.currentStep = currentStepIndex + 1;
                    stepLabel.setText("Step: " + this.currentStep);
                    updateVisualization();
                });
            });
            
            sequentialTransition.getChildren().add(stepTransition);
        }

        PauseTransition finalState = new PauseTransition(Duration.millis(500));
        finalState.setOnFinished(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: Complete - Total Profit: " + totalProfit);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
                
                isComputing = false;
                startComputeBtn.setDisable(false);
                nextStepBtn.setDisable(false);
                generateJobsBtn.setDisable(false);
                pauseBtn.setDisable(true);
                resumeBtn.setDisable(true);
                jobsInput.setDisable(false);
            });
        });
        sequentialTransition.getChildren().add(finalState);

        sequentialTransition.play();
    }
    
    private void performNextStep() {
        if (isComputing) return;

        try {
            numJobs = Integer.parseInt(jobsInput.getText().trim());
            if (numJobs <= 0 || numJobs > 15) {
                showAlert("Invalid Input", "Number of jobs must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of jobs.");
            return;
        }

        if (steps == null || currentStep == 0) {
            if (jobs == null || jobs.length != numJobs) {
                generateNewJobs();
            }
            
            JobSchedulingAlgorithm.Job[] algoJobs = convertToAlgorithmJobs(jobs);
            if (algoJobs == null || algoJobs.length == 0) {
                showAlert("Error", "Failed to create algorithm jobs. Please check the JobSchedulingAlgorithm implementation.");
                return;
            }
            
            finalSchedule = algo.scheduleJobs(algoJobs);
            steps = algo.getSteps();
            totalProfit = algo.getTotalProfit(algoJobs, finalSchedule);
            currentStep = 0;
            jobsLabel.setText("Jobs: " + numJobs);
        }

        if (currentStep < steps.size()) {
            currentStep++;
            stepLabel.setText("Step: " + currentStep);
            updateVisualization();
            
            if (currentStep == steps.size()) {
                statusLabel.setText("Status: Complete - Total Profit: " + totalProfit);
                statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-weight: bold;");
            }
        }
    }
    
    private void generateNewJobs() {
        if (isComputing) return;
        
        try {
            numJobs = Integer.parseInt(jobsInput.getText().trim());
            if (numJobs <= 0 || numJobs > 15) {
                showAlert("Invalid Input", "Number of jobs must be between 1 and 15.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for number of jobs.");
            return;
        }
        
        Random rand = new Random();
        jobs = new JobWrapper[numJobs];
        
        for (int i = 0; i < numJobs; i++) {
            int id = i + 1;
            int deadline = rand.nextInt(4) + 1; // Deadline between 1-4
            int profit = rand.nextInt(90) + 10; // Profit between 10-99
            
            jobs[i] = new JobWrapper(id, deadline, profit);
        }
        
        resetVisualization();
    }
    
    private void resetVisualization() {
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
        
        steps = null;
        currentStep = 0;
        finalSchedule = null;
        totalProfit = 0;
        
        stepLabel.setText("Step: 0");
        statusLabel.setText("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: #6aab73; -fx-font-weight: bold;");
        jobsLabel.setText("Jobs: " + numJobs);
        profitLabel.setText("Total Profit: 0");
        jobsInput.setText(String.valueOf(numJobs));
        
        updateVisualization();
        
        isComputing = false;
        isPaused = false;
        startComputeBtn.setDisable(false);
        nextStepBtn.setDisable(false);
        generateJobsBtn.setDisable(false);
        pauseBtn.setDisable(true);
        resumeBtn.setDisable(true);
        jobsInput.setDisable(false);
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
            statusLabel.setText("Status: Scheduling jobs by profit...");
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