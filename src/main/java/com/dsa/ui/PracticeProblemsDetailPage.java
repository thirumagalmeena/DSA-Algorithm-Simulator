package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PracticeProblemsDetailPage {

    private final Stage stage;
    private final String algorithmId;
    private final String algorithmName;
    private MongoCollection<Document> practiceCollection;
    private List<QuizQuestion> quizQuestions;
    private List<HackerRankProblem> hackerrankProblems;
    private Map<String, String> userAnswers;
    private Map<String, Boolean> completionStatus;
    private int currentQuestionIndex;
    private VBox quizContent;
    private Label questionLabel;
    private ToggleGroup optionsGroup;
    private VBox optionsContainer;
    private Button nextButton;
    private Button prevButton;
    private Label progressLabel;
    private Label scoreLabel;
    private VBox resultsContainer;
    private CheckBox completionCheckbox;
    private ProgressTracker progressTracker;

    public PracticeProblemsDetailPage(Stage stage, String algorithmId, String algorithmName) {
        this.stage = stage;
        this.algorithmId = algorithmId;
        this.algorithmName = algorithmName;
        this.userAnswers = new HashMap<>();
        this.completionStatus = new HashMap<>();
        this.currentQuestionIndex = 0;
        this.progressTracker = new ProgressTracker(); // Initialize progress tracker
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://23pd03_db_user:ldn2saUWgoBBINVw@cluster0.gwvt6zu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            MongoDatabase database = mongoClient.getDatabase("algodb");
            practiceCollection = database.getCollection("practice");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public void show() {
        // Main container with gradient background
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Header section
        VBox headerBox = createHeaderBox();
        
        // Load practice problems
        loadPracticeProblems();

        // Create tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0.3, 0, 5);");

        // Create tabs
        Tab quizTab = createQuizTab();
        Tab hackerrankTab = createHackerrankTab();
        Tab progressTab = createProgressTab();

        tabPane.getTabs().addAll(quizTab, hackerrankTab, progressTab);

        // Back button
        Button backBtn = createBackButton();

        VBox contentBox = new VBox(25, headerBox, tabPane, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(900);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 1000, 750);
        stage.setScene(scene);
        stage.setTitle("Practice - " + algorithmName + " - DSA Simulator");
    }

    private VBox createHeaderBox() {
        Label title = new Label("Practice: " + algorithmName);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.5, 3, 3);");

        Label subtitle = new Label("Test your knowledge with quizzes and coding challenges");
        subtitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        subtitle.setStyle("-fx-text-fill: #e8f4f8;");

        VBox headerBox = new VBox(8, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        
        return headerBox;
    }

    private void loadPracticeProblems() {
        quizQuestions = new ArrayList<>();
        hackerrankProblems = new ArrayList<>();

        if (practiceCollection == null) {
            System.err.println("MongoDB collection is null - connection failed");
            return;
        }

        // Use topic instead of algorithm_id for lookup - match your JSON structure
        Document practiceDoc = practiceCollection.find(Filters.eq("topic", algorithmName)).first();
        
        if (practiceDoc != null) {
            System.out.println("DEBUG: Found document for topic: " + algorithmName);
            System.out.println("DEBUG: Document content: " + practiceDoc.toJson());
            
            // Load quiz questions - use "quiz" instead of "quizzes" (matches your JSON)
            List<Document> quizzes = practiceDoc.getList("quiz", Document.class);
            if (quizzes != null) {
                System.out.println("DEBUG: Found " + quizzes.size() + " quiz questions");
                for (int i = 0; i < quizzes.size(); i++) {
                    Document quizDoc = quizzes.get(i);
                    // Generate ID since your JSON doesn't have one
                    String questionId = algorithmName + "_q_" + (i + 1);
                    String questionText = quizDoc.getString("question");
                    List<String> options = quizDoc.getList("options", String.class);
                    String answer = quizDoc.getString("answer");
                    String explanation = "No explanation provided."; // Your JSON doesn't have explanation field
                    
                    System.out.println("DEBUG: Loading question " + (i + 1) + ": " + questionText);
                    
                    QuizQuestion question = new QuizQuestion(
                        questionId,
                        questionText,
                        options,
                        answer,
                        explanation
                    );
                    quizQuestions.add(question);
                    completionStatus.put("quiz_" + questionId, false);
                }
            } else {
                System.out.println("DEBUG: No quiz questions found in document");
            }

            // Load HackerRank problems - use "hackerrank_problems" instead of "hackerrank_questions" (matches your JSON)
            List<Document> hackerrank = practiceDoc.getList("hackerrank_problems", Document.class);
            if (hackerrank != null) {
                System.out.println("DEBUG: Found " + hackerrank.size() + " HackerRank problems");
                for (Document problemDoc : hackerrank) {
                    String title = problemDoc.getString("title");
                    String link = problemDoc.getString("link");
                    String difficulty = problemDoc.getString("difficulty");
                    String description = problemDoc.getString("description");
                    
                    System.out.println("DEBUG: Loading problem: " + title);
                    
                    HackerRankProblem problem = new HackerRankProblem(
                        title,
                        link,
                        difficulty,
                        description
                    );
                    hackerrankProblems.add(problem);
                    completionStatus.put("problem_" + title, false);
                }
            } else {
                System.out.println("DEBUG: No HackerRank problems found in document");
            }
        } else {
            System.out.println("DEBUG: No document found for topic: " + algorithmName);
            // Debug: print all available topics
            System.out.println("DEBUG: Available topics in database:");
            for (Document doc : practiceCollection.find()) {
                String topic = doc.getString("topic");
                System.out.println("  - " + topic);
            }
        }
        
        System.out.println("DEBUG: Final loaded - " + quizQuestions.size() + " quiz questions, " + 
                          hackerrankProblems.size() + " HackerRank problems");
    }

    private Tab createQuizTab() {
        Tab tab = new Tab("🧠 Quiz");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        quizContent = new VBox(20);
        quizContent.setPadding(new Insets(25));
        quizContent.setStyle("-fx-background-color: white;");

        if (quizQuestions != null && !quizQuestions.isEmpty()) {
            initializeQuiz();
        } else {
            Label noQuiz = new Label("No quiz questions available for " + algorithmName + ".");
            noQuiz.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 16;");
            quizContent.getChildren().add(noQuiz);
            
            // Add debug info
            Label debugLabel = new Label("(DEBUG: Found " + quizQuestions.size() + " questions)");
            debugLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12;");
            quizContent.getChildren().add(debugLabel);
        }

        scrollPane.setContent(quizContent);
        tab.setContent(scrollPane);
        return tab;
    }

    private Tab createHackerrankTab() {
        Tab tab = new Tab("💻 Coding Challenges");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white;");

        if (hackerrankProblems != null && !hackerrankProblems.isEmpty()) {
            Label title = new Label("HackerRank Problems");
            title.setFont(Font.font("System", FontWeight.BOLD, 24));
            title.setStyle("-fx-text-fill: #2c3e50;");

            content.getChildren().add(title);

            for (HackerRankProblem problem : hackerrankProblems) {
                VBox problemCard = createProblemCard(problem);
                content.getChildren().add(problemCard);
            }
        } else {
            Label noProblems = new Label("No coding challenges available for " + algorithmName + ".");
            noProblems.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 16;");
            content.getChildren().add(noProblems);
            
            // Add debug info
            Label debugLabel = new Label("(DEBUG: Found " + hackerrankProblems.size() + " problems)");
            debugLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12;");
            content.getChildren().add(debugLabel);
        }

        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    private Tab createProgressTab() {
        Tab tab = new Tab("📊 Progress Tracker");
        tab.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ScrollPane scrollPane = createStyledScrollPane();
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Your Progress - " + algorithmName);
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        // Get current progress from MongoDB
        Map<String, Object> userProgress = progressTracker.getUserProgress();
        Map<String, Object> algoProgress = getAlgorithmProgress(userProgress);

        // Progress summary
        VBox progressSummary = createProgressSummary(algoProgress);
        
        // Completion checklist
        VBox checklist = createChecklist();

        // Learning suggestions
        VBox suggestionsSection = createSuggestionsSection();

        content.getChildren().addAll(title, progressSummary, checklist, suggestionsSection);
        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    private Map<String, Object> getAlgorithmProgress(Map<String, Object> userProgress) {
        Map<String, Object> algoProgress = new HashMap<>();
        List<Map<String, Object>> algorithmDetails = (List<Map<String, Object>>) userProgress.get("algorithm_details");
        
        if (algorithmDetails != null) {
            for (Map<String, Object> progress : algorithmDetails) {
                if (algorithmName.equals(progress.get("algorithm_name"))) {
                    return progress;
                }
            }
        }
        
        // Return default progress if not found
        algoProgress.put("quiz_progress", 0.0);
        algoProgress.put("problems_progress", 0.0);
        algoProgress.put("best_score", 0);
        algoProgress.put("last_attempt", null);
        return algoProgress;
    }

    private VBox createProgressSummary(Map<String, Object> algoProgress) {
        VBox summary = new VBox(15);
        summary.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10; -fx-padding: 20;");

        double quizProgress = (double) algoProgress.get("quiz_progress");
        double problemsProgress = (double) algoProgress.get("problems_progress");
        int bestScore = (int) algoProgress.get("best_score");

        Label progressTitle = new Label("Current Progress");
        progressTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        progressTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Quiz Progress
        VBox quizProgressBox = new VBox(5);
        Label quizLabel = new Label("Quiz Completion: " + String.format("%.1f%%", quizProgress));
        quizLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        quizLabel.setStyle("-fx-text-fill: #28a745;");
        
        ProgressBar quizProgressBar = new ProgressBar(quizProgress / 100);
        quizProgressBar.setPrefWidth(300);
        quizProgressBar.setStyle("-fx-accent: #28a745;");
        quizProgressBox.getChildren().addAll(quizLabel, quizProgressBar);

        // Problems Progress
        VBox problemsProgressBox = new VBox(5);
        Label problemsLabel = new Label("Problems Solved: " + String.format("%.1f%%", problemsProgress));
        problemsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        problemsLabel.setStyle("-fx-text-fill: #007bff;");
        
        ProgressBar problemsProgressBar = new ProgressBar(problemsProgress / 100);
        problemsProgressBar.setPrefWidth(300);
        problemsProgressBar.setStyle("-fx-accent: #007bff;");
        problemsProgressBox.getChildren().addAll(problemsLabel, problemsProgressBar);

        // Best Score
        Label bestScoreLabel = new Label("Best Quiz Score: " + bestScore + "%");
        bestScoreLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        bestScoreLabel.setStyle("-fx-text-fill: #dc3545;");

        summary.getChildren().addAll(progressTitle, quizProgressBox, problemsProgressBox, bestScoreLabel);
        return summary;
    }

    private VBox createChecklist() {
        VBox checklist = new VBox(15);
        checklist.setStyle("-fx-background-color: white; -fx-padding: 20;");

        Label checklistTitle = new Label("Completion Checklist");
        checklistTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        checklistTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox itemsContainer = new VBox(10);

        // Add quiz items
        if (quizQuestions != null && !quizQuestions.isEmpty()) {
            Label quizSection = new Label("Quiz Questions:");
            quizSection.setFont(Font.font("System", FontWeight.BOLD, 14));
            quizSection.setStyle("-fx-text-fill: #495057;");
            itemsContainer.getChildren().add(quizSection);

            for (QuizQuestion question : quizQuestions) {
                CheckBox checkBox = new CheckBox(question.getQuestion());
                boolean isCompleted = completionStatus.getOrDefault("quiz_" + question.getId(), false);
                checkBox.setSelected(isCompleted);
                checkBox.setStyle("-fx-text-fill: " + (isCompleted ? "#28a745" : "#34495e") + ";");
                checkBox.setDisable(true); // Read-only for display
                itemsContainer.getChildren().add(checkBox);
            }
        }

        // Add problem items
        if (hackerrankProblems != null && !hackerrankProblems.isEmpty()) {
            Label problemSection = new Label("Coding Problems:");
            problemSection.setFont(Font.font("System", FontWeight.BOLD, 14));
            problemSection.setStyle("-fx-text-fill: #495057;");
            itemsContainer.getChildren().add(problemSection);

            for (HackerRankProblem problem : hackerrankProblems) {
                CheckBox checkBox = new CheckBox(problem.getTitle() + " (" + problem.getDifficulty() + ")");
                boolean isCompleted = completionStatus.getOrDefault("problem_" + problem.getTitle(), false);
                checkBox.setSelected(isCompleted);
                checkBox.setStyle("-fx-text-fill: " + (isCompleted ? "#28a745" : "#34495e") + ";");
                checkBox.setDisable(true);
                itemsContainer.getChildren().add(checkBox);
            }
        }

        checklist.getChildren().addAll(checklistTitle, itemsContainer);
        return checklist;
    }

    private VBox createSuggestionsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #e8f5e8; -fx-border-radius: 10; -fx-padding: 20;");

        Label sectionTitle = new Label("💡 Learning Suggestions");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox suggestionsContainer = new VBox(10);

        // Get suggestions from progress tracker
        List<Map<String, Object>> suggestions = progressTracker.getLearningSuggestions();
        
        if (suggestions.isEmpty()) {
            Label noSuggestions = new Label("Keep practicing! Complete more algorithms to get personalized suggestions.");
            noSuggestions.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
            suggestionsContainer.getChildren().add(noSuggestions);
        } else {
            for (Map<String, Object> suggestion : suggestions) {
                if (suggestionsContainer.getChildren().size() < 2) { // Show max 2 suggestions
                    HBox suggestionCard = createSuggestionCard(suggestion);
                    suggestionsContainer.getChildren().add(suggestionCard);
                }
            }
        }

        Button viewFullProgressBtn = new Button("View Full Progress Dashboard");
        viewFullProgressBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 6;");
        viewFullProgressBtn.setOnAction(e -> {
            ProgressDashboard progressDashboard = new ProgressDashboard(stage);
            progressDashboard.show();
        });

        section.getChildren().addAll(sectionTitle, suggestionsContainer, viewFullProgressBtn);
        return section;
    }

    private HBox createSuggestionCard(Map<String, Object> suggestion) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER_LEFT);

        String algoName = (String) suggestion.get("algorithm_name");
        String category = (String) suggestion.get("category");
        String reason = (String) suggestion.get("reason");

        VBox infoBox = new VBox(5);
        
        Label nameLabel = new Label(algoName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label categoryLabel = new Label("Category: " + category);
        categoryLabel.setFont(Font.font("System", 12));
        categoryLabel.setStyle("-fx-text-fill: #6c757d;");

        Label reasonLabel = new Label("💡 " + reason);
        reasonLabel.setFont(Font.font("System", 11));
        reasonLabel.setStyle("-fx-text-fill: #007bff; -fx-font-style: italic;");

        infoBox.getChildren().addAll(nameLabel, categoryLabel, reasonLabel);
        card.getChildren().add(infoBox);

        return card;
    }

    private ScrollPane createStyledScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: white; -fx-background-color: white;");
        scrollPane.setPadding(new Insets(5));
        return scrollPane;
    }

    private void initializeQuiz() {
        quizContent.getChildren().clear();

        progressLabel = new Label();
        progressLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        progressLabel.setStyle("-fx-text-fill: #495057;");
        updateProgressLabel();

        questionLabel = new Label();
        questionLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        questionLabel.setStyle("-fx-text-fill: #2c3e50;");
        questionLabel.setWrapText(true);

        optionsContainer = new VBox(10);
        optionsGroup = new ToggleGroup();

        HBox navButtons = new HBox(15);
        navButtons.setAlignment(Pos.CENTER);

        prevButton = new Button("← Previous");
        prevButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6;");
        prevButton.setOnAction(e -> showPreviousQuestion());

        nextButton = new Button("Next →");
        nextButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 6;");
        nextButton.setOnAction(e -> showNextQuestion());

        Button submitButton = new Button("Submit Quiz");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 6;");
        submitButton.setOnAction(e -> showResults());

        navButtons.getChildren().addAll(prevButton, nextButton, submitButton);

        resultsContainer = new VBox(15);
        resultsContainer.setVisible(false);

        quizContent.getChildren().addAll(progressLabel, questionLabel, optionsContainer, navButtons, resultsContainer);
        showQuestion(0);
        updateNavigationButtons();
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= quizQuestions.size()) return;

        currentQuestionIndex = index;
        QuizQuestion question = quizQuestions.get(index);

        questionLabel.setText((index + 1) + ". " + question.getQuestion());
        optionsContainer.getChildren().clear();
        optionsGroup = new ToggleGroup();

        List<String> shuffledOptions = new ArrayList<>(question.getOptions());
        Collections.shuffle(shuffledOptions);

        for (String option : shuffledOptions) {
            RadioButton radioButton = new RadioButton(option);
            radioButton.setToggleGroup(optionsGroup);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setStyle("-fx-text-fill: #34495e;");
            radioButton.setWrapText(true);
            radioButton.setMaxWidth(800);

            if (option.equals(userAnswers.get(question.getId()))) {
                radioButton.setSelected(true);
            }

            optionsContainer.getChildren().add(radioButton);
        }

        updateProgressLabel();
        updateNavigationButtons();
    }

    private void showNextQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex < quizQuestions.size() - 1) {
            showQuestion(currentQuestionIndex + 1);
        }
    }

    private void showPreviousQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            showQuestion(currentQuestionIndex - 1);
        }
    }

    private void saveCurrentAnswer() {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected != null) {
            userAnswers.put(currentQuestion.getId(), selected.getText());
        }
    }

    private void showResults() {
        saveCurrentAnswer();

        int correct = 0;
        List<QuizResult> results = new ArrayList<>();

        for (QuizQuestion question : quizQuestions) {
            String userAnswer = userAnswers.get(question.getId());
            boolean isCorrect = question.getAnswer().equals(userAnswer);
            if (isCorrect) {
                correct++;
                // Mark quiz question as completed
                completionStatus.put("quiz_" + question.getId(), true);
            }
            
            // Update progress for each question in MongoDB
            progressTracker.updateQuizProgress(algorithmName, question.getId(), isCorrect, correct);
            results.add(new QuizResult(question, userAnswer, isCorrect));
        }

        double score = (double) correct / quizQuestions.size() * 100;

        // Update final score in MongoDB
        progressTracker.updateQuizProgress(algorithmName, "final_score", true, (int)score);

        resultsContainer.getChildren().clear();
        resultsContainer.setVisible(true);

        scoreLabel = new Label(String.format("Your Score: %.1f%% (%d/%d)", score, correct, quizQuestions.size()));
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        scoreLabel.setStyle(score >= 70 ? "-fx-text-fill: #28a745;" : 
                           score >= 50 ? "-fx-text-fill: #ffc107;" : 
                           "-fx-text-fill: #dc3545;");

        VBox detailedResults = new VBox(15);
        for (int i = 0; i < results.size(); i++) {
            QuizResult result = results.get(i);
            VBox resultCard = createResultCard(result, i + 1);
            detailedResults.getChildren().add(resultCard);
        }

        HBox resultButtons = new HBox(15);
        resultButtons.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Restart Quiz");
        restartButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 6;");
        restartButton.setOnAction(e -> restartQuiz());

        Button progressButton = new Button("View Progress");
        progressButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 6;");
        progressButton.setOnAction(e -> {
            // Refresh progress tab
            Tab progressTab = createProgressTab();
            TabPane tabPane = (TabPane) stage.getScene().lookup(".tab-pane");
            if (tabPane != null) {
                tabPane.getTabs().set(2, progressTab); // Update progress tab
                tabPane.getSelectionModel().select(2); // Switch to progress tab
            }
        });

        resultButtons.getChildren().addAll(restartButton, progressButton);

        resultsContainer.getChildren().addAll(scoreLabel, detailedResults, resultButtons);
    }

    private VBox createResultCard(QuizResult result, int questionNumber) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + (result.isCorrect() ? "#d4edda" : "#f8d7da") + 
                     "; -fx-border-radius: 8; -fx-padding: 15; -fx-border-color: " + 
                     (result.isCorrect() ? "#c3e6cb" : "#f5c6cb") + ";");

        Label questionLabel = new Label(questionNumber + ". " + result.getQuestion().getQuestion());
        questionLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        questionLabel.setStyle("-fx-text-fill: #2c3e50;");
        questionLabel.setWrapText(true);

        Label userAnswerLabel = new Label("Your answer: " + 
            (result.getUserAnswer() != null ? result.getUserAnswer() : "Not answered"));
        userAnswerLabel.setFont(Font.font("System", 12));
        userAnswerLabel.setStyle("-fx-text-fill: " + (result.isCorrect() ? "#155724" : "#721c24") + ";");

        Label correctAnswerLabel = new Label("Correct answer: " + result.getQuestion().getAnswer());
        correctAnswerLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        correctAnswerLabel.setStyle("-fx-text-fill: #155724;");

        Label explanationLabel = new Label("Explanation: " + result.getQuestion().getExplanation());
        explanationLabel.setFont(Font.font("System", 12));
        explanationLabel.setStyle("-fx-text-fill: #6c757d;");
        explanationLabel.setWrapText(true);

        card.getChildren().addAll(questionLabel, userAnswerLabel, correctAnswerLabel, explanationLabel);
        return card;
    }

    private void restartQuiz() {
        userAnswers.clear();
        currentQuestionIndex = 0;
        resultsContainer.setVisible(false);
        showQuestion(0);
    }

    private void updateProgressLabel() {
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + quizQuestions.size());
    }

    private void updateNavigationButtons() {
        prevButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == quizQuestions.size() - 1);
    }

    private VBox createProblemCard(HackerRankProblem problem) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 12; -fx-padding: 20; " +
                     "-fx-border-color: #e9ecef; -fx-border-width: 1;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(problem.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label difficultyLabel = new Label(problem.getDifficulty());
        difficultyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        difficultyLabel.setStyle("-fx-background-color: " + getDifficultyColor(problem.getDifficulty()) + 
                               "; -fx-text-fill: white; -fx-padding: 4 8 4 8; -fx-background-radius: 10;");

        header.getChildren().addAll(titleLabel, difficultyLabel);

        Label descriptionLabel = new Label(problem.getDescription());
        descriptionLabel.setFont(Font.font("System", 14));
        descriptionLabel.setStyle("-fx-text-fill: #34495e;");
        descriptionLabel.setWrapText(true);

        HBox buttonBox = new HBox(10);
        
        Button solveButton = new Button("Solve on HackerRank");
        solveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 6;");
        solveButton.setOnAction(e -> {
            openHackerRankProblem(problem.getLink());
            // Mark as completed when user clicks solve
            completionStatus.put("problem_" + problem.getTitle(), true);
            progressTracker.updateProblemCompletion(algorithmName, problem.getTitle(), true);
            
            // Show confirmation
            showCompletionAlert(problem.getTitle());
        });

        CheckBox completedCheckbox = new CheckBox("Completed");
        boolean isCompleted = completionStatus.getOrDefault("problem_" + problem.getTitle(), false);
        completedCheckbox.setSelected(isCompleted);
        completedCheckbox.setOnAction(e -> {
            boolean completed = completedCheckbox.isSelected();
            completionStatus.put("problem_" + problem.getTitle(), completed);
            progressTracker.updateProblemCompletion(algorithmName, problem.getTitle(), completed);
        });

        buttonBox.getChildren().addAll(solveButton, completedCheckbox);

        card.getChildren().addAll(header, descriptionLabel, buttonBox);
        return card;
    }

    private void showCompletionAlert(String problemTitle) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Problem Started");
        alert.setHeaderText("Opening HackerRank");
        alert.setContentText("Marked '" + problemTitle + "' as in progress. Don't forget to check the completion box when you finish!");
        alert.showAndWait();
    }

    private void openHackerRankProblem(String link) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(link));
        } catch (Exception e) {
            Stage webStage = new Stage();
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load(link);
            
            Scene webScene = new Scene(webView, 1200, 800);
            webStage.setScene(webScene);
            webStage.setTitle("HackerRank Problem");
            webStage.show();
        }
    }

    private String getDifficultyColor(String difficulty) {
        if (difficulty == null) return "#6c757d";
        switch (difficulty.toLowerCase()) {
            case "easy": return "#28a745";
            case "medium": return "#ffc107";
            case "hard": return "#dc3545";
            default: return "#6c757d";
        }
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Practice List");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(40);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        String normalStyle = "-fx-background-color: linear-gradient(to right, #6c757d, #5a6268); " +
                           "-fx-text-fill: white; -fx-background-radius: 8; " +
                           "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 2, 2);";
        
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #5a6268, #495057); " +
                          "-fx-text-fill: white; -fx-background-radius: 8; " +
                          "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 3, 3);";
        
        backBtn.setStyle(normalStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));
        
        backBtn.setOnAction(e -> {
            PracticeProblemsPage listPage = new PracticeProblemsPage(stage);
            listPage.show();
        });
        
        return backBtn;
    }

    // Data classes
    private static class QuizQuestion {
        private final String id;
        private final String question;
        private final List<String> options;
        private final String answer;
        private final String explanation;

        public QuizQuestion(String id, String question, List<String> options, String answer, String explanation) {
            this.id = id;
            this.question = question;
            this.options = options;
            this.answer = answer;
            this.explanation = explanation;
        }

        public String getId() { return id; }
        public String getQuestion() { return question; }
        public List<String> getOptions() { return options; }
        public String getAnswer() { return answer; }
        public String getExplanation() { return explanation; }
    }

    private static class HackerRankProblem {
        private final String title;
        private final String link;
        private final String difficulty;
        private final String description;

        public HackerRankProblem(String title, String link, String difficulty, String description) {
            this.title = title;
            this.link = link;
            this.difficulty = difficulty;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getLink() { return link; }
        public String getDifficulty() { return difficulty; }
        public String getDescription() { return description; }
    }

    private static class QuizResult {
        private final QuizQuestion question;
        private final String userAnswer;
        private final boolean correct;

        public QuizResult(QuizQuestion question, String userAnswer, boolean correct) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.correct = correct;
        }

        public QuizQuestion getQuestion() { return question; }
        public String getUserAnswer() { return userAnswer; }
        public boolean isCorrect() { return correct; }
    }
}