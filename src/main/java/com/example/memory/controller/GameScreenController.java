package com.example.memory.controller;

import com.example.memory.MemoryGameApp;
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

/**
 * Controller for the game screen of the Memory Game.
 * Handles game logic, UI updates, and user interactions.
 */
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

    /**
     * Initializes the game with the player's name and grid size.
     *
     * @param playerName Name of the player.
     * @param gridSize Size of the game grid (e.g., 4, 6, 8).
     */
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

        javafx.application.Platform.runLater(() -> {
            if (gameGrid.getScene() != null && gameGrid.getScene().getWindow() != null) {
                Stage stage = (Stage) gameGrid.getScene().getWindow();
                originalWidth = stage.getWidth();
                originalHeight = stage.getHeight();
                resizeWindow();
            }
        });
    }

    /**
     * Adjusts the window size based on the grid size.
     */
    private void resizeWindow() {
        if (gameGrid.getScene() == null || gameGrid.getScene().getWindow() == null) {
            return;
        }

        Stage stage = (Stage) gameGrid.getScene().getWindow();

        double windowWidth, windowHeight;

        switch (gridSize) {
            case 4:

                return;
            case 6:
                windowWidth = 1000;
                windowHeight = 750;
                break;
            case 8:
                windowWidth = 1200;
                windowHeight = 900;
                break;
            default:
                return;
        }

        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
        stage.centerOnScreen();
    }

    /**
     * Sets up the game grid and cards.
     */
    private void setupGame() {
        gameGrid.getChildren().clear();
        cards = new ArrayList<>();

        List<String> symbols = Arrays.asList(
                "🎮", "🎯", "🎲", "🎪", "🎨", "🎭", "🎸", "🎹",
                "⚽", "🍔", "🎾", "🏐", "🏈", "⚾", "🎳", "🏓",
                "🚀", "🏆", "🌟", "⭐", "🌙", "🍕", "🌈", "⚡",
                "🦄", "🐉", "🦋", "🐛", "🐝", "🐞", "🚒", "🎷"
        );

        List<String> gameSymbols = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            gameSymbols.add(symbols.get(i));
            gameSymbols.add(symbols.get(i));
        }
        Collections.shuffle(gameSymbols);

        int symbolIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                MemoryCard card = new MemoryCard(gameSymbols.get(symbolIndex));
                card.setOnAction(e -> handleCardClick(card));

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

        gameGrid.setHgap(5);
        gameGrid.setVgap(5);
    }

    /**
     * Handles a click on a memory card.
     *
     * @param card The clicked MemoryCard.
     */
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

    /**
     * Checks if two cards match and updates the game state.
     */
    private void checkMatch() {
        isProcessing = true;

        final MemoryCard card1 = firstCard;
        final MemoryCard card2 = secondCard;

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (card1.getSymbol().equals(card2.getSymbol())) {
                card1.setMatched(true);
                card2.setMatched(true);
                matchedPairs++;

                int basePoints = gridSize * 10;
                int timeBonus = Math.max(0, 300 - seconds);
                int attemptPenalty = attempts * 2;
                score += Math.max(basePoints, basePoints + timeBonus - attemptPenalty);

                checkGameComplete();
            } else {
                card1.showWrong();
                card2.showWrong();

                PauseTransition wrongPause = new PauseTransition(Duration.seconds(0.5));
                wrongPause.setOnFinished(evt -> {
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

    /**
     * Checks if the game is complete and shows a completion message if so.
     */
    private void checkGameComplete() {
        if (matchedPairs == totalPairs) {
            timer.stop();

            int completionBonus = gridSize * 50;
            score += completionBonus;

            LeaderboardManager.addScore(new GameResult(
                    playerName, score, attempts, seconds,
                    getDifficultyName(), new Date()
            ));

            updateUI();

            showCompletionAlert();
        }
    }

    /**
     * Returns the difficulty name based on the grid size.
     *
     * @return Difficulty name as a String.
     */
    private String getDifficultyName() {
        switch (gridSize) {
            case 4: return "Easy";
            case 6: return "Medium";
            case 8: return "Hard";
            default: return "Unknown";
        }
    }

    /**
     * Starts the game timer.
     */
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            updateUI();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Updates the UI elements (score, attempts, time).
     */
    private void updateUI() {
        scoreText.setText("Score: " + score);
        attemptsText.setText("Attempts: " + attempts);

        int minutes = seconds / 60;
        int secs = seconds % 60;
        timeText.setText(String.format("Time: %02d:%02d", minutes, secs));
    }

    /**
     * Shows a completion alert after the game ends.
     */
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

        if (backBtn != null) {
            backBtn.setDisable(true);
        }

        alert.setOnHidden(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(evt -> backToMenu());
            pause.play();
        });

        alert.show();
    }

    /**
     * Starts a new game with the current settings.
     */
    @FXML
    private void newGame() {
        if (timer != null) {
            timer.stop();
        }

        firstCard = null;
        secondCard = null;
        isProcessing = false;

        initializeGame(playerName, gridSize);
    }

    /**
     * Returns to the main menu.
     */
    @FXML
    private void backToMenu() {
        try {
            if (timer != null) {
                timer.stop();
            }

            Stage stage = null;
            if (gameGrid != null && gameGrid.getScene() != null && gameGrid.getScene().getWindow() != null) {
                stage = (Stage) gameGrid.getScene().getWindow();
            }

            if (stage == null) {
                javafx.application.Platform.runLater(this::backToMenu);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/memory/StartScreen.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            String cssResource = getClass().getResource("/com/example/memory/styles.css").toExternalForm();
            scene.getStylesheets().add(cssResource);

            MemoryGameApp.setWindowIcon(stage);

            stage.setScene(scene);

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

    /**
     * Returns to the main menu (via ActionEvent).
     *
     * @param event The ActionEvent.
     */
    @FXML
    private void backToMenu(ActionEvent event) {
        backToMenu();
    }

    /**
     * Shows an error dialog.
     *
     * @param title Title of the dialog.
     * @param message Error message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        try {
            alert.show();
        } catch (IllegalStateException ex) {
            javafx.application.Platform.runLater(alert::show);
        }
    }
}

