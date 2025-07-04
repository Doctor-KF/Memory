package com.example.memory.controller;

import com.example.memory.MemoryGameApp;
import com.example.memory.model.GameResult;
import com.example.memory.manager.LeaderboardManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for displaying and managing the leaderboard.
 */
public class LeaderboardController implements Initializable {

    @FXML private Button easyTabBtn;
    @FXML private Button mediumTabBtn;
    @FXML private Button hardTabBtn;
    @FXML private TableView<GameResultWrapper> leaderboardTable;
    @FXML private TableColumn<GameResultWrapper, Integer> rankColumn;
    @FXML private TableColumn<GameResultWrapper, String> nameColumn;
    @FXML private TableColumn<GameResultWrapper, Integer> scoreColumn;
    @FXML private TableColumn<GameResultWrapper, Integer> attemptsColumn;
    @FXML private TableColumn<GameResultWrapper, String> timeColumn;
    @FXML private TableColumn<GameResultWrapper, String> dateColumn;
    @FXML private Button clearBtn;
    @FXML private Button backBtn;

    private String currentDifficulty = "Easy";

    /**
     * Initializes the leaderboard when the controller is loaded.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        showEasyLeaderboard();
    }

    /**
     * Configures the table columns for the leaderboard.
     */
    private void setupTable() {
        rankColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRank()).asObject());
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPlayerName()));
        scoreColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getScore()).asObject());
        attemptsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getAttempts()).asObject());
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTimeFormatted()));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateFormatted()));
    }

    /**
     * Shows the leaderboard for the "Easy" difficulty.
     */
    @FXML
    private void showEasyLeaderboard() {
        updateActiveButton(easyTabBtn);
        currentDifficulty = "Easy";
        loadLeaderboard();
    }

    /**
     * Shows the leaderboard for the "Medium" difficulty.
     */
    @FXML
    private void showMediumLeaderboard() {
        updateActiveButton(mediumTabBtn);
        currentDifficulty = "Medium";
        loadLeaderboard();
    }

    /**
     * Shows the leaderboard for the "Hard" difficulty.
     */
    @FXML
    private void showHardLeaderboard() {
        updateActiveButton(hardTabBtn);
        currentDifficulty = "Hard";
        loadLeaderboard();
    }

    /**
     * Highlights the active difficulty button.
     *
     * @param activeBtn The active button.
     */
    private void updateActiveButton(Button activeBtn) {
        easyTabBtn.getStyleClass().remove("active");
        mediumTabBtn.getStyleClass().remove("active");
        hardTabBtn.getStyleClass().remove("active");

        activeBtn.getStyleClass().add("active");
    }

    /**
     * Loads the leaderboard for the current difficulty.
     */
    private void loadLeaderboard() {
        List<GameResult> results = LeaderboardManager.getLeaderboard(currentDifficulty);
        ObservableList<GameResultWrapper> wrappedResults = FXCollections.observableArrayList();

        for (int i = 0; i < results.size(); i++) {
            wrappedResults.add(new GameResultWrapper(results.get(i), i + 1));
        }

        leaderboardTable.setItems(wrappedResults);
    }

    /**
     * Clears the leaderboard for the current difficulty after confirmation.
     */
    @FXML
    private void clearLeaderboard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Leaderboard");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will permanently delete all " + currentDifficulty + " leaderboard entries.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LeaderboardManager.clearLeaderboard(currentDifficulty);
                loadLeaderboard();
            }
        });
    }

    /**
     * Returns to the main menu.
     */
    @FXML
    private void backToMenu() {
        try {
            URL fxmlResource = getClass().getResource("/com/example/memory/StartScreen.fxml");
            if (fxmlResource == null) {
                throw new IOException("Could not find StartScreen.fxml resource at /com/example/memory/StartScreen.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Scene scene = new Scene(loader.load(), 800, 600);

            URL cssResource = getClass().getResource("/com/example/memory/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.out.println("Warning: Could not find styles.css");
            }

            Stage stage = (Stage) backBtn.getScene().getWindow();

            MemoryGameApp.setWindowIcon(stage);

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error loading start screen: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
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
        alert.showAndWait();
    }

    /**
     * Wrapper class for GameResult for displaying in the table.
     */
    public static class GameResultWrapper {
        private final GameResult gameResult;
        private final int rank;

        public GameResultWrapper(GameResult gameResult, int rank) {
            this.gameResult = gameResult;
            this.rank = rank;
        }

        public int getRank() { return rank; }
        public String getPlayerName() { return gameResult.getPlayerName(); }
        public int getScore() { return gameResult.getScore(); }
        public int getAttempts() { return gameResult.getAttempts(); }
        public String getTimeFormatted() { return gameResult.getTimeFormatted(); }
        public String getDateFormatted() { return gameResult.getDateFormatted(); }
    }
}