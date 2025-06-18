// GameController.java - Main controller coordinating all components
package com.example.memory.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import com.example.memory.model.*;
import com.example.memory.service.*;
import com.example.memory.ui.UIComponents;
import com.example.memory.view.*;

public class GameController {
    private final Stage primaryStage;
    private final GameModel gameModel;
    private final LeaderboardManager leaderboardManager;
    private final StartScreenView startScreenView;
    private final GameBoardView gameBoardView;
    private final LeaderboardView leaderboardView;

    private GameTimer gameTimer;
    private Button firstCard = null;
    private Button secondCard = null;
    private boolean canClick = true;

    // UI labels for updates
    private Label attemptsLabel;
    private Label timeLabel;
    private Label scoreLabel;

    public GameController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameModel = new GameModel();
        this.leaderboardManager = new LeaderboardManager();
        this.startScreenView = new StartScreenView();
        this.gameBoardView = new GameBoardView();
        this.leaderboardView = new LeaderboardView();

        setupStage();
    }

    private void setupStage() {
        primaryStage.setTitle("🧠 Memory Game");
        primaryStage.setOnCloseRequest(e -> {
            if (gameTimer != null) {
                gameTimer.stop();
            }
            Platform.exit();
        });
    }

    public void showStartScreen() {
        stopTimer();

        Scene scene = startScreenView.createScene(
                this::startGame,
                this::showLeaderboard,
                () -> Platform.exit()
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startGame(String playerName, GameDifficulty difficulty) {
        if (playerName.trim().isEmpty()) {
            showAlert("Please enter your name!");
            return;
        }

        gameModel.reset();
        gameModel.initializeGame(playerName, difficulty);

        resetCardState();

        Scene scene = gameBoardView.createGameScene(
                gameModel,
                this::handleCardClick,
                this::startNewGame,
                this::showLeaderboard,
                this::showStartScreen
        );

        // Get references to UI labels
        attemptsLabel = gameBoardView.getAttemptsLabel();
        timeLabel = gameBoardView.getTimeLabel();
        scoreLabel = gameBoardView.getScoreLabel();

        primaryStage.setScene(scene);

        startTimer();
    }

    private void startNewGame() {
        // Restart current game with same settings
        startGame(gameModel.getPlayerName(), gameModel.getDifficulty());
    }

    private void handleCardClick(Button card) {
        if (!canClick || card.getText().equals(card.getUserData().toString())) {
            return;
        }

        // Reveal card
        card.setText(card.getUserData().toString());
        card.setStyle(UIComponents.CardStyles.REVEALED);

        if (firstCard == null) {
            firstCard = card;
        } else if (secondCard == null) {
            secondCard = card;
            gameModel.incrementAttempts();
            updateUI();

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
            gameModel.incrementFoundPairs();
            updateUI();

            // Mark as matched
            firstCard.setStyle(UIComponents.CardStyles.MATCHED);
            secondCard.setStyle(UIComponents.CardStyles.MATCHED);

            resetCardState();

            if (gameModel.isGameComplete()) {
                PauseTransition endPause = new PauseTransition(Duration.seconds(0.5));
                endPause.setOnFinished(e -> gameComplete());
                endPause.play();
            }
        } else {
            // No match - hide cards
            final Button card1 = firstCard;
            final Button card2 = secondCard;

            PauseTransition hideDelay = new PauseTransition(Duration.seconds(0.3));
            hideDelay.setOnFinished(e -> {
                card1.setText("?");
                card2.setText("?");
                card1.setStyle(UIComponents.CardStyles.HIDDEN);
                card2.setStyle(UIComponents.CardStyles.HIDDEN);
            });
            hideDelay.play();

            resetCardState();
        }
    }

    private void resetCardState() {
        firstCard = null;
        secondCard = null;
        canClick = true;
    }

    private void updateUI() {
        if (attemptsLabel != null) {
            attemptsLabel.setText("Attempts: " + gameModel.getAttempts());
        }
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + gameModel.getCurrentScore());
        }
    }

    private void gameComplete() {
        stopTimer();

        PlayerScore playerScore = gameModel.createPlayerScore();
        leaderboardManager.addScore(playerScore);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Congratulations!");
            alert.setHeaderText("Game Complete!");

            long totalTime = gameModel.getElapsedTime();
            alert.setContentText(String.format(
                    "Well done, %s!\n\n" +
                            "⭐ Final Score: %d points\n" +
                            "🎯 Attempts: %d\n" +
                            "⏱️ Time: %02d:%02d\n" +
                            "🏆 Grid: %s",
                    gameModel.getPlayerName(),
                    gameModel.getCurrentScore(),
                    gameModel.getAttempts(),
                    (int)totalTime/60, (int)totalTime%60,
                    gameModel.getDifficulty().getGridDisplay()
            ));

            styleAlert(alert);
            alert.showAndWait();
            showLeaderboard();
        });
    }

    public void showLeaderboard() {
        stopTimer();

        Scene scene = leaderboardView.createScene(
                leaderboardManager.getTopScores(10),
                leaderboardManager.isEmpty(),
                this::showStartScreen
        );

        primaryStage.setScene(scene);
    }

    private void startTimer() {
        if (timeLabel != null) {
            gameTimer = new GameTimer(timeLabel, gameModel);
            gameTimer.start();
        }
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        alert.getDialogPane().setStyle("-fx-background-color: " + UIComponents.PANEL_COLOR + ";");
        alert.getDialogPane().lookup(".content").setStyle("-fx-text-fill: white;");
        alert.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: " + UIComponents.BACKGROUND_COLOR + ";");
        alert.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: cyan;");
    }
}