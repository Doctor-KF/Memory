package com.example.memory.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.memory.MemoryGameApp;

/**
 * Controller for the start screen of the Memory Game.
 * Handles starting games and showing the leaderboard.
 */
public class StartScreenController {

    @FXML private TextField nameField;
    @FXML private Button easyBtn;
    @FXML private Button mediumBtn;
    @FXML private Button hardBtn;
    @FXML private Button leaderboardBtn;

    /**
     * Starts a game with "Easy" difficulty.
     */
    @FXML
    private void startEasyGame() {
        startGame(4);
    }

    /**
     * Starts a game with "Medium" difficulty.
     */
    @FXML
    private void startMediumGame() {
        startGame(6);
    }

    /**
     * Starts a game with "Hard" difficulty.
     */
    @FXML
    private void startHardGame() {
        startGame(8);
    }

    /**
     * Starts the game with the specified grid size.
     *
     * @param gridSize Size of the game grid (e.g., 4, 6, 8).
     */
    private void startGame(int gridSize) {
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            showAlert("Please enter your name before starting the game!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/memory/GameScreen.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/memory/styles.css").toExternalForm());

            GameScreenController controller = loader.getController();
            controller.initializeGame(playerName, gridSize);

            Stage stage = (Stage) nameField.getScene().getWindow();

            MemoryGameApp.setWindowIcon(stage);

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading game screen: " + e.getMessage());
        }
    }

    /**
     * Zeigt die Bestenliste an.
     */
    @FXML
    private void showLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/memory/Leaderboard.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/memory/styles.css").toExternalForm());

            Stage stage = (Stage) nameField.getScene().getWindow();

            MemoryGameApp.setWindowIcon(stage);

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading leaderboard: " + e.getMessage());
        }
    }

    /**
     * Shows a warning message.
     *
     * @param message The message to display.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}