// GameBoardView.java - Game board UI
package com.example.memory.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.example.memory.model.GameModel;
import com.example.memory.model.GameDifficulty;
import com.example.memory.ui.UIComponents;

import java.util.List;
import java.util.function.Consumer;

public class GameBoardView {
    private Label attemptsLabel;
    private Label timeLabel;
    private Label scoreLabel;

    public Scene createGameScene(GameModel gameModel,
                                 Consumer<Button> onCardClick,
                                 Runnable onNewGame,
                                 Runnable onShowLeaderboard,
                                 Runnable onMainMenu) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIComponents.BACKGROUND_COLOR + ";");

        // Top panel with game info
        HBox topPanel = createTopPanel(gameModel);
        root.setTop(topPanel);

        // Game grid
        GridPane gameGrid = createGameGrid(gameModel, onCardClick);
        root.setCenter(gameGrid);

        // Bottom panel with controls
        HBox bottomPanel = createBottomPanel(onNewGame, onShowLeaderboard, onMainMenu);
        root.setBottom(bottomPanel);

        return new Scene(root, 1000, 700);
    }

    private HBox createTopPanel(GameModel gameModel) {
        HBox topPanel = new HBox(30);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(20));
        topPanel.setStyle("-fx-background-color: " + UIComponents.PANEL_COLOR + "; -fx-border-color: " +
                UIComponents.BORDER_COLOR + "; -fx-border-width: 0 0 2 0;");

        Label playerLabel = UIComponents.createInfoLabel("Player: " + gameModel.getPlayerName(),
                UIComponents.Colors.CYAN, 16);

        attemptsLabel = UIComponents.createInfoLabel("Attempts: " + gameModel.getAttempts(),
                UIComponents.Colors.YELLOW, 16);

        timeLabel = UIComponents.createInfoLabel("Time: 00:00",
                UIComponents.Colors.LIGHT_GREEN, 16);

        scoreLabel = UIComponents.createInfoLabel("Score: " + gameModel.getCurrentScore(),
                UIComponents.Colors.ORANGE, 16);

        topPanel.getChildren().addAll(playerLabel, attemptsLabel, timeLabel, scoreLabel);
        return topPanel;
    }

    private GridPane createGameGrid(GameModel gameModel, Consumer<Button> onCardClick) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30));

        GameDifficulty difficulty = gameModel.getDifficulty();
        int rows = difficulty.getRows();
        int cols = difficulty.getCols();

        List<String> gameSymbols = gameModel.getGameSymbols();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String symbol = gameSymbols.get(row * cols + col);
                Button card = createCard(symbol, onCardClick);
                grid.add(card, col, row);
            }
        }

        return grid;
    }

    private Button createCard(String symbol, Consumer<Button> onCardClick) {
        Button card = new Button("?");
        card.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        card.setPrefSize(80, 80);
        card.setStyle(UIComponents.CardStyles.HIDDEN);
        card.setUserData(symbol);

        card.setOnAction(e -> onCardClick.accept(card));

        // Hover effects
        card.setOnMouseEntered(e -> {
            if (!card.getText().equals(symbol)) {
                card.setStyle(UIComponents.CardStyles.HOVER);
            }
        });

        card.setOnMouseExited(e -> {
            if (!card.getText().equals(symbol)) {
                card.setStyle(UIComponents.CardStyles.HIDDEN);
            }
        });

        return card;
    }

    private HBox createBottomPanel(Runnable onNewGame, Runnable onShowLeaderboard, Runnable onMainMenu) {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20));
        bottomPanel.setStyle("-fx-background-color: " + UIComponents.PANEL_COLOR + "; -fx-border-color: " +
                UIComponents.BORDER_COLOR + "; -fx-border-width: 2 0 0 0;");

        Button newGameButton = UIComponents.createStyledButton("🔄 NEW GAME", UIComponents.Colors.CYAN);
        Button leaderboardButton = UIComponents.createStyledButton("🏆 LEADERBOARD", UIComponents.Colors.GOLD);
        Button mainMenuButton = UIComponents.createStyledButton("🏠 MAIN MENU", UIComponents.Colors.LIGHT_GRAY);

        newGameButton.setOnAction(e -> onNewGame.run());
        leaderboardButton.setOnAction(e -> onShowLeaderboard.run());
        mainMenuButton.setOnAction(e -> onMainMenu.run());

        bottomPanel.getChildren().addAll(newGameButton, leaderboardButton, mainMenuButton);
        return bottomPanel;
    }

    // Getters for UI components that need to be updated
    public Label getAttemptsLabel() {
        return attemptsLabel;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }
}