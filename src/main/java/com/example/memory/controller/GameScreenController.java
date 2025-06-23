package com.example.memory.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import com.example.memory.model.GameResult;
import com.example.memory.model.MemoryCard;
import com.example.memory.service.LeaderboardManager;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class GameScreenController {

    @FXML private Text playerNameText;
    @FXML private Text scoreText;
    @FXML private Text attemptsText;
    @FXML private Text timeText;
    @FXML private GridPane gameGrid;
    @FXML private Button newGameBtn;
    @FXML private Button backBtn;

    private String playerName;
    private int gridSize;
    private int score;
    private int attempts;
    private int seconds;
    private Timeline timer;

    private List<MemoryCard> cards;
    private MemoryCard firstCard;
    private MemoryCard secondCard;
    private boolean isProcessing;
    private int matchedPairs;
    private int totalPairs;
    private double originalWidth;
    private double originalHeight;

    public void initializeGame(String playerName, int gridSize) {
        this.playerName = playerName;
        this.gridSize = gridSize;
        this.score = 0;
        this.attempts = 0;
        this.seconds = 0;
        this.matchedPairs = 0;
        this.totalPairs = (gridSize * gridSize) / 2;

        playerNameText.setText("Player: " + playerName);
        updateUI();
        setupGame();
        startTimer();

        // Store original window size before any resizing
        javafx.application.Platform.runLater(() -> {
            if (gameGrid.getScene() != null && gameGrid.getScene().getWindow() != null) {
                Stage stage = (Stage) gameGrid.getScene().getWindow();
                originalWidth = stage.getWidth();
                originalHeight = stage.getHeight();
                resizeWindow();
            }
        });
    }

    private void resizeWindow() {
        // Add null check to prevent NullPointerException
        if (gameGrid.getScene() == null || gameGrid.getScene().getWindow() == null) {
            return;
        }

        Stage stage = (Stage) gameGrid.getScene().getWindow();

        // Calculate window size based on grid size
        double windowWidth, windowHeight;

        switch (gridSize) {
            case 4:
                // Keep same size as start screen - no resizing needed for easy mode
                return; // Exit early, don't resize for easy mode
            case 6:
                windowWidth = 1000;
                windowHeight = 750;
                break;
            case 8:
                windowWidth = 1200;
                windowHeight = 900;
                break;
            default:
                return; // Don't resize for unknown grid sizes
        }

        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
        stage.centerOnScreen();
    }

    private void setupGame() {
        gameGrid.getChildren().clear();
        cards = new ArrayList<>();

        // Create card symbols (emojis)
        List<String> symbols = Arrays.asList(
                "🎮", "🎯", "🎲", "🎪", "🎨", "🎭", "🎸", "🎹",
                "⚽", "🍔", "🎾", "🏐", "🏈", "⚾", "🎳", "🏓",
                "🚀", "🏆", "🌟", "⭐", "🌙", "🍕", "🌈", "⚡",
                "🦄", "🐉", "🦋", "🐛", "🐝", "🐞", "🚒", "🎷"
        );

        // Create pairs of cards
        List<String> gameSymbols = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            gameSymbols.add(symbols.get(i));
            gameSymbols.add(symbols.get(i));
        }
        Collections.shuffle(gameSymbols);

        // Create and place cards
        int symbolIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                MemoryCard card = new MemoryCard(gameSymbols.get(symbolIndex));
                card.setOnAction(e -> handleCardClick(card));

                // Improved card sizing - larger cards for better visibility
                double cardSize;
                switch (gridSize) {
                    case 4:
                        cardSize = 100;
                        break;
                    case 6:
                        cardSize = 80;
                        break;
                    case 8:
                        cardSize = 70;
                        break;
                    default:
                        cardSize = 80;
                }

                card.setPrefSize(cardSize, cardSize);
                card.setMaxSize(cardSize, cardSize);
                card.setMinSize(cardSize, cardSize);

                cards.add(card);
                gameGrid.add(card, col, row);
                symbolIndex++;
            }
        }

        // Set gap between cards for better visual separation
        gameGrid.setHgap(5);
        gameGrid.setVgap(5);
    }

    private void handleCardClick(MemoryCard card) {
        if (isProcessing || card.isFlipped() || card.isMatched()) {
            return;
        }

        card.flip();

        if (firstCard == null) {
            firstCard = card;
        } else if (secondCard == null) {
            secondCard = card;
            attempts++;
            updateUI();
            checkMatch();
        }
    }

    private void checkMatch() {
        isProcessing = true;

        // Store references to the cards before they might be nullified
        final MemoryCard card1 = firstCard;
        final MemoryCard card2 = secondCard;

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            // Use the stored references instead of the instance variables
            if (card1.getSymbol().equals(card2.getSymbol())) {
                // Match found
                card1.setMatched(true);
                card2.setMatched(true);
                matchedPairs++;

                // Calculate score: base points + time bonus - attempt penalty
                int basePoints = gridSize * 10;
                int timeBonus = Math.max(0, 300 - seconds);
                int attemptPenalty = attempts * 2;
                score += Math.max(basePoints, basePoints + timeBonus - attemptPenalty);

                checkGameComplete();
            } else {
                // No match - show wrong briefly
                card1.showWrong();
                card2.showWrong();

                PauseTransition wrongPause = new PauseTransition(Duration.seconds(0.5));
                wrongPause.setOnFinished(evt -> {
                    // Use stored references here too
                    card1.flip();
                    card2.flip();
                });
                wrongPause.play();
            }

            firstCard = null;
            secondCard = null;
            isProcessing = false;
            updateUI();
        });
        pause.play();
    }

    private void checkGameComplete() {
        if (matchedPairs == totalPairs) {
            timer.stop();

            // Bonus for completing the game
            int completionBonus = gridSize * 50;
            score += completionBonus;

            // Save to leaderboard
            LeaderboardManager.addScore(new GameResult(
                    playerName, score, attempts, seconds,
                    getDifficultyName(), new Date()
            ));

            updateUI();

            // Show completion message
            showCompletionAlert();
        }
    }

    private String getDifficultyName() {
        switch (gridSize) {
            case 4: return "Easy";
            case 6: return "Medium";
            case 8: return "Hard";
            default: return "Unknown";
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            updateUI();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateUI() {
        scoreText.setText("Score: " + score);
        attemptsText.setText("Attempts: " + attempts);

        int minutes = seconds / 60;
        int secs = seconds % 60;
        timeText.setText(String.format("Time: %02d:%02d", minutes, secs));
    }

    private void showCompletionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("Game Complete!");
        alert.setContentText(String.format(
                "Well done, %s!\n\n" +
                        "Final Score: %d\n" +
                        "Attempts: %d\n" +
                        "Time: %02d:%02d\n" +
                        "Difficulty: %s\n\n" +
                        "Your score has been added to the leaderboard!",
                playerName, score, attempts, seconds / 60, seconds % 60, getDifficultyName()
        ));

        alert.setOnHidden(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(evt -> backToMenu());
            pause.play();
        });

        alert.show();
    }

    @FXML
    private void newGame() {
        if (timer != null) {
            timer.stop();
        }
        // Reset card references to prevent null pointer exceptions
        firstCard = null;
        secondCard = null;
        isProcessing = false;

        initializeGame(playerName, gridSize);
    }

    @FXML
    private void backToMenu() {
        try {
            // Stop the timer if it's running
            if (timer != null) {
                timer.stop();
            }

            // Use absolute path from resources root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/memory/StartScreen.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            // Add CSS if needed
            String cssResource = getClass().getResource("/com/example/memory/styles.css").toExternalForm();
            scene.getStylesheets().add(cssResource);

            // Get current stage and switch to start screen
            Stage stage = (Stage) gameGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Memory Game - Main Menu");

            // Reset window size to original if it was resized
            if (originalWidth > 0 && originalHeight > 0) {
                stage.setWidth(originalWidth);
                stage.setHeight(originalHeight);
                stage.centerOnScreen();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error returning to menu: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }

    // Overloaded method for ActionEvent (when called from FXML button)
    @FXML
    private void backToMenu(ActionEvent event) {
        backToMenu();
    }

    // Helper method to show error alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}