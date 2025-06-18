package com.example.memory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MemoryGameOneClass extends Application {

    private Stage primaryStage;
    private String playerName;
    private int gridSize = 4;
    private int attempts = 0;
    private int foundPairs = 0;
    private int totalPairs;
    private long startTime;
    private Button firstCard = null;
    private Button secondCard = null;
    private boolean canClick = true;
    private List<String> symbols = Arrays.asList("🎮", "🎯", "🎲", "🎪", "🎨", "🎭", "🎸", "🎵", "🚀", "⭐", "🔥", "💎", "🌟", "🎊", "🎈", "🎁", "🍕", "🚒");
    private Label attemptsLabel;
    private Label timeLabel;
    private Label scoreLabel;
    private int currentScore = 0;
    private Thread timerThread; // Added to control timer thread

    // Leaderboard data
    private List<PlayerScore> leaderboard = new ArrayList<>();
    private static final String LEADERBOARD_FILE = "leaderboard.txt";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("🧠 Memory Game");
        loadLeaderboard();
        showStartScreen();
    }

    private void showStartScreen() {
        // Stop any existing timer thread
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }

        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a1a;");

        // Title
        Label title = new Label("🧠 MEMORY GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setTextFill(Color.CYAN);
        title.setEffect(new DropShadow(10, Color.CYAN));

        // Subtitle
        Label subtitle = new Label("Test your memory and find all matching pairs!");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.LIGHTGRAY);

        // Name input
        VBox nameBox = new VBox(10);
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Enter your name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setPromptText("Player name...");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 5;");

        // Difficulty selection
        VBox difficultyBox = new VBox(10);
        difficultyBox.setAlignment(Pos.CENTER);
        Label difficultyLabel = new Label("Select difficulty:");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        difficultyLabel.setTextFill(Color.WHITE);

        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton easy = new RadioButton("Easy (4x4)");
        RadioButton medium = new RadioButton("Medium (4x6)");
        RadioButton hard = new RadioButton("Hard (6x6)");

        easy.setToggleGroup(difficultyGroup);
        medium.setToggleGroup(difficultyGroup);
        hard.setToggleGroup(difficultyGroup);
        easy.setSelected(true);

        easy.setTextFill(Color.LIGHTGREEN);
        medium.setTextFill(Color.YELLOW);
        hard.setTextFill(Color.ORANGE);

        HBox difficultyButtons = new HBox(20);
        difficultyButtons.setAlignment(Pos.CENTER);
        difficultyButtons.getChildren().addAll(easy, medium, hard);

        difficultyBox.getChildren().addAll(difficultyLabel, difficultyButtons);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button startButton = createStyledButton("🎮 START GAME", Color.LIGHTGREEN);
        Button leaderboardButton = createStyledButton("🏆 LEADERBOARD", Color.GOLD);
        Button exitButton = createStyledButton("❌ EXIT", Color.LIGHTCORAL);

        startButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Please enter your name!");
                return;
            }
            playerName = name;

            RadioButton selected = (RadioButton) difficultyGroup.getSelectedToggle();
            if (selected == easy) {
                gridSize = 4; // Easy: 4x4
            } else if (selected == medium) {
                gridSize = 24; // Medium: 4x6 (use 24 to distinguish from easy)
            } else {
                gridSize = 6; // Hard: 6x6
            }

            startGame();
        });

        leaderboardButton.setOnAction(e -> showLeaderboard());
        exitButton.setOnAction(e -> Platform.exit());

        buttonBox.getChildren().addAll(startButton, leaderboardButton, exitButton);

        nameBox.getChildren().addAll(nameLabel, nameField);
        root.getChildren().addAll(title, subtitle, nameBox, difficultyBox, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle(String.format("-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                toHexString(color.darker())));

        button.setOnMouseEntered(e ->
                button.setStyle(String.format("-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                        toHexString(color))));

        button.setOnMouseExited(e ->
                button.setStyle(String.format("-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                        toHexString(color.darker()))));

        return button;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void startGame() {
        // Reset game state
        attempts = 0;
        foundPairs = 0;
        firstCard = null;
        secondCard = null;
        canClick = true;
        currentScore = 0;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a1a;");

        // Top panel with game info
        HBox topPanel = createTopPanel();
        root.setTop(topPanel);

        // Game grid
        GridPane gameGrid = createGameGrid();
        root.setCenter(gameGrid);

        // Bottom panel with controls
        HBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);

        // Start timer
        startTime = System.currentTimeMillis();
        startTimer();
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(30);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(20));
        topPanel.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #444; -fx-border-width: 0 0 2 0;");

        Label playerLabel = new Label("Player: " + playerName);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerLabel.setTextFill(Color.CYAN);

        attemptsLabel = new Label("Attempts: 0");
        attemptsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        attemptsLabel.setTextFill(Color.YELLOW);

        timeLabel = new Label("Time: 00:00");
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        timeLabel.setTextFill(Color.LIGHTGREEN);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.ORANGE);

        topPanel.getChildren().addAll(playerLabel, attemptsLabel, timeLabel, scoreLabel);
        return topPanel;
    }

    private GridPane createGameGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30));

        // Handle different grid sizes properly
        int rows, cols;
        if (gridSize == 4) {
            rows = cols = 4; // Easy: 4x4 = 16 cards = 8 pairs
        } else if (gridSize == 24) {
            // Medium difficulty: 4x6 = 24 cards = 12 pairs
            rows = 4;
            cols = 6;
        } else {
            rows = cols = 6; // Hard: 6x6 = 36 cards = 18 pairs
        }

        totalPairs = (rows * cols) / 2;
        List<String> gameSymbols = new ArrayList<>();

        // Create pairs
        for (int i = 0; i < totalPairs; i++) {
            String symbol = symbols.get(i % symbols.size());
            gameSymbols.add(symbol);
            gameSymbols.add(symbol);
        }

        Collections.shuffle(gameSymbols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button card = createCard(gameSymbols.get(row * cols + col));
                grid.add(card, col, row);
            }
        }

        return grid;
    }

    private Button createCard(String symbol) {
        Button card = new Button("?");
        card.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        card.setPrefSize(80, 80);
        card.setStyle("-fx-background-color: #333; -fx-text-fill: #888; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;");

        card.setUserData(symbol);

        card.setOnAction(e -> handleCardClick(card));

        // Hover effects
        card.setOnMouseEntered(e -> {
            if (canClick && !card.getText().equals(symbol)) {
                card.setStyle("-fx-background-color: #444; -fx-text-fill: #aaa; -fx-border-color: #777; -fx-border-radius: 10; -fx-background-radius: 10;");
            }
        });

        card.setOnMouseExited(e -> {
            if (canClick && !card.getText().equals(symbol)) {
                card.setStyle("-fx-background-color: #333; -fx-text-fill: #888; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;");
            }
        });

        return card;
    }

    private void handleCardClick(Button card) {
        if (!canClick || card.getText().equals(card.getUserData().toString())) {
            return;
        }

        // Reveal card
        card.setText(card.getUserData().toString());
        card.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #777; -fx-border-radius: 10; -fx-background-radius: 10;");

        if (firstCard == null) {
            firstCard = card;
        } else if (secondCard == null) {
            secondCard = card;
            attempts++;
            attemptsLabel.setText("Attempts: " + attempts);

            canClick = false;

            // Check for match after short delay
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> checkMatch());
            pause.play();
        }
    }

    private void checkMatch() {
        if (firstCard.getUserData().equals(secondCard.getUserData())) {
            // Match found!
            foundPairs++;
            calculateScore();

            // Mark as found
            firstCard.setStyle("-fx-background-color: #2d5a2d; -fx-text-fill: lightgreen; -fx-border-color: lightgreen; -fx-border-radius: 10; -fx-background-radius: 10;");
            secondCard.setStyle("-fx-background-color: #2d5a2d; -fx-text-fill: lightgreen; -fx-border-color: lightgreen; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Reset card references immediately for matches
            firstCard = null;
            secondCard = null;
            canClick = true;

            if (foundPairs == totalPairs) {
                // Game complete with some delay
                PauseTransition endPause = new PauseTransition(Duration.seconds(0.5));
                endPause.setOnFinished(e -> gameComplete());
                endPause.play();
            }
        } else {
            // No match - hide cards again
            // Store references locally to avoid null pointer issues
            final Button card1 = firstCard;
            final Button card2 = secondCard;

            PauseTransition hideDelay = new PauseTransition(Duration.seconds(0.3));
            hideDelay.setOnFinished(e -> {
                // Use local references instead of instance variables
                card1.setText("?");
                card2.setText("?");
                card1.setStyle("-fx-background-color: #333; -fx-text-fill: #888; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;");
                card2.setStyle("-fx-background-color: #333; -fx-text-fill: #888; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;");
            });
            hideDelay.play();

            // Reset instance variables immediately
            firstCard = null;
            secondCard = null;
            canClick = true;
        }
    }

    private void calculateScore() {
        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        int timeBonus = Math.max(0, 300 - (int)timeElapsed); // Bonus for speed
        int attemptPenalty = (attempts - foundPairs) * 5; // Penalty for extra attempts
        int baseScore = foundPairs * 100;

        currentScore = Math.max(0, baseScore + timeBonus - attemptPenalty);
        scoreLabel.setText("Score: " + currentScore);
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20));
        bottomPanel.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #444; -fx-border-width: 2 0 0 0;");

        Button newGameButton = createStyledButton("🔄 NEW GAME", Color.CYAN);
        Button leaderboardButton = createStyledButton("🏆 LEADERBOARD", Color.GOLD);
        Button mainMenuButton = createStyledButton("🏠 MAIN MENU", Color.LIGHTGRAY);

        newGameButton.setOnAction(e -> startGame());
        leaderboardButton.setOnAction(e -> showLeaderboard());
        mainMenuButton.setOnAction(e -> showStartScreen());

        bottomPanel.getChildren().addAll(newGameButton, leaderboardButton, mainMenuButton);
        return bottomPanel;
    }

    private void startTimer() {
        // Stop any existing timer thread
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }

        timerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && foundPairs < totalPairs) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                        int minutes = (int) elapsed / 60;
                        int seconds = (int) elapsed % 60;
                        timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void gameComplete() {
        // Stop the timer
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;

        // Add to leaderboard - use actual grid dimensions for display
        int displayGridSize;
        if (gridSize == 4) {
            displayGridSize = 4; // 4x4
        } else if (gridSize == 24) {
            displayGridSize = 46; // Special code for 4x6
        } else {
            displayGridSize = 6; // 6x6
        }

        PlayerScore playerScore = new PlayerScore(playerName, currentScore, attempts, totalTime, displayGridSize);
        leaderboard.add(playerScore);
        leaderboard.sort((a, b) -> Integer.compare(b.score, a.score)); // Sort by score descending
        saveLeaderboard();

        // Use Platform.runLater to show the dialog after animation processing is complete
        Platform.runLater(() -> {
            // Show completion dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Congratulations!");
            alert.setHeaderText("Game Complete!");

            String gridDisplay = (displayGridSize == 46) ? "4x6" : displayGridSize + "x" + displayGridSize;
            alert.setContentText(String.format(
                    "Well done, %s!\n\n" +
                            "⭐ Final Score: %d points\n" +
                            "🎯 Attempts: %d\n" +
                            "⏱️ Time: %02d:%02d\n" +
                            "🏆 Grid: %s",
                    playerName, currentScore, attempts,
                    (int)totalTime/60, (int)totalTime%60, gridDisplay
            ));

            // Style the alert
            alert.getDialogPane().setStyle("-fx-background-color: #2a2a2a;");
            alert.getDialogPane().lookup(".content").setStyle("-fx-text-fill: white;");
            alert.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #1a1a1a;");
            alert.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: cyan;");

            alert.showAndWait();
            showLeaderboard();
        });
    }

    private void showLeaderboard() {
        // Stop any existing timer thread
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1a1a1a;");

        Label title = new Label("🏆 LEADERBOARD");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.GOLD);
        title.setEffect(new DropShadow(10, Color.GOLD));

        // Leaderboard table
        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setMaxWidth(600);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #333; -fx-padding: 10; -fx-border-radius: 5;");

        Label rankLabel = new Label("Rank");
        Label nameLabel = new Label("Name");
        Label scoreLabel = new Label("Score");
        Label attemptsLabel = new Label("Attempts");
        Label timeLabel = new Label("Time");
        Label difficultyLabel = new Label("Grid");

        rankLabel.setPrefWidth(80);
        nameLabel.setPrefWidth(150);
        scoreLabel.setPrefWidth(100);
        attemptsLabel.setPrefWidth(100);
        timeLabel.setPrefWidth(100);
        difficultyLabel.setPrefWidth(70);

        setLabelStyle(rankLabel, Color.LIGHTGRAY);
        setLabelStyle(nameLabel, Color.LIGHTGRAY);
        setLabelStyle(scoreLabel, Color.LIGHTGRAY);
        setLabelStyle(attemptsLabel, Color.LIGHTGRAY);
        setLabelStyle(timeLabel, Color.LIGHTGRAY);
        setLabelStyle(difficultyLabel, Color.LIGHTGRAY);

        header.getChildren().addAll(rankLabel, nameLabel, scoreLabel, attemptsLabel, timeLabel, difficultyLabel);
        leaderboardBox.getChildren().add(header);

        // Leaderboard entries
        for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
            PlayerScore player = leaderboard.get(i);
            HBox entry = createLeaderboardEntry(i + 1, player);
            leaderboardBox.getChildren().add(entry);
        }

        if (leaderboard.isEmpty()) {
            Label noScores = new Label("No scores yet! Be the first to play!");
            noScores.setTextFill(Color.LIGHTGRAY);
            noScores.setFont(Font.font("Arial", 16));
            leaderboardBox.getChildren().add(noScores);
        }

        // Back button
        Button backButton = createStyledButton("🏠 BACK TO MENU", Color.CYAN);
        backButton.setOnAction(e -> showStartScreen());

        root.getChildren().addAll(title, leaderboardBox, backButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private HBox createLeaderboardEntry(int rank, PlayerScore player) {
        HBox entry = new HBox();
        entry.setAlignment(Pos.CENTER);
        entry.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 8; -fx-border-radius: 3;");

        Color rankColor = rank <= 3 ? (rank == 1 ? Color.GOLD : rank == 2 ? Color.SILVER : Color.web("#CD7F32")) : Color.WHITE;
        String rankIcon = rank <= 3 ? (rank == 1 ? "🥇" : rank == 2 ? "🥈" : "🥉") : String.valueOf(rank);

        Label rankLabel = new Label(rankIcon);
        Label nameLabel = new Label(player.name);
        Label scoreLabel = new Label(String.valueOf(player.score));
        Label attemptsLabel = new Label(String.valueOf(player.attempts));
        Label timeLabel = new Label(String.format("%02d:%02d", (int)player.time/60, (int)player.time%60));

        // Handle grid display properly
        String gridDisplay = (player.gridSize == 46) ? "4x6" : player.gridSize + "x" + player.gridSize;
        Label difficultyLabel = new Label(gridDisplay);

        rankLabel.setPrefWidth(80);
        nameLabel.setPrefWidth(150);
        scoreLabel.setPrefWidth(100);
        attemptsLabel.setPrefWidth(100);
        timeLabel.setPrefWidth(100);
        difficultyLabel.setPrefWidth(70);

        setLabelStyle(rankLabel, rankColor);
        setLabelStyle(nameLabel, Color.CYAN);
        setLabelStyle(scoreLabel, Color.LIGHTGREEN);
        setLabelStyle(attemptsLabel, Color.YELLOW);
        setLabelStyle(timeLabel, Color.ORANGE);
        setLabelStyle(difficultyLabel, Color.LIGHTGRAY);

        entry.getChildren().addAll(rankLabel, nameLabel, scoreLabel, attemptsLabel, timeLabel, difficultyLabel);
        return entry;
    }

    private void setLabelStyle(Label label, Color color) {
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setAlignment(Pos.CENTER);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #2a2a2a;");
        alert.getDialogPane().lookup(".content").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

    private void loadLeaderboard() {
        try {
            if (Files.exists(Paths.get(LEADERBOARD_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(LEADERBOARD_FILE));
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        PlayerScore score = new PlayerScore(
                                parts[0],
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2]),
                                Long.parseLong(parts[3]),
                                Integer.parseInt(parts[4])
                        );
                        leaderboard.add(score);
                    }
                }
                leaderboard.sort((a, b) -> Integer.compare(b.score, a.score));
            }
        } catch (IOException e) {
            System.err.println("Could not load leaderboard: " + e.getMessage());
        }
    }

    private void saveLeaderboard() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE));
            for (PlayerScore score : leaderboard) {
                writer.println(score.name + "," + score.score + "," + score.attempts + "," + score.time + "," + score.gridSize);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not save leaderboard: " + e.getMessage());
        }
    }

    // Player score class
    private static class PlayerScore {
        String name;
        int score;
        int attempts;
        long time;
        int gridSize;

        PlayerScore(String name, int score, int attempts, long time, int gridSize) {
            this.name = name;
            this.score = score;
            this.attempts = attempts;
            this.time = time;
            this.gridSize = gridSize;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}