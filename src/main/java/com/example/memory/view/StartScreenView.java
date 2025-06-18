// StartScreenView.java - Start screen UI
package com.example.memory.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.example.memory.model.GameDifficulty;
import com.example.memory.ui.UIComponents;

import java.util.function.BiConsumer;

public class StartScreenView {

    public Scene createScene(BiConsumer<String, GameDifficulty> onStartGame,
                             Runnable onShowLeaderboard,
                             Runnable onExit) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: " + UIComponents.BACKGROUND_COLOR + ";");

        // Title
        Label title = UIComponents.createTitleLabel("🧠 MEMORY GAME", UIComponents.Colors.CYAN, 48);

        // Subtitle
        Label subtitle = UIComponents.createInfoLabel("Test your memory and find all matching pairs!",
                UIComponents.Colors.LIGHT_GRAY, 18);
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        // Name input
        VBox nameBox = createNameInputBox();
        TextField nameField = (TextField) nameBox.getChildren().get(1);

        // Difficulty selection
        VBox difficultyBox = createDifficultyBox();
        ToggleGroup difficultyGroup = (ToggleGroup) difficultyBox.getUserData();

        // Buttons
        HBox buttonBox = createButtonBox(nameField, difficultyGroup, onStartGame, onShowLeaderboard, onExit);

        root.getChildren().addAll(title, subtitle, nameBox, difficultyBox, buttonBox);

        return new Scene(root, 800, 600);
    }

    private VBox createNameInputBox() {
        VBox nameBox = new VBox(10);
        nameBox.setAlignment(Pos.CENTER);

        Label nameLabel = UIComponents.createInfoLabel("Enter your name:", UIComponents.Colors.WHITE, 16);

        TextField nameField = new TextField();
        nameField.setPromptText("Player name...");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 5;");

        nameBox.getChildren().addAll(nameLabel, nameField);
        return nameBox;
    }

    private VBox createDifficultyBox() {
        VBox difficultyBox = new VBox(10);
        difficultyBox.setAlignment(Pos.CENTER);

        Label difficultyLabel = UIComponents.createInfoLabel("Select difficulty:", UIComponents.Colors.WHITE, 16);

        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton easy = new RadioButton(GameDifficulty.EASY.getDisplayName());
        RadioButton medium = new RadioButton(GameDifficulty.MEDIUM.getDisplayName());
        RadioButton hard = new RadioButton(GameDifficulty.HARD.getDisplayName());

        easy.setToggleGroup(difficultyGroup);
        medium.setToggleGroup(difficultyGroup);
        hard.setToggleGroup(difficultyGroup);
        easy.setSelected(true);

        easy.setTextFill(UIComponents.Colors.LIGHT_GREEN);
        medium.setTextFill(UIComponents.Colors.YELLOW);
        hard.setTextFill(UIComponents.Colors.ORANGE);

        easy.setUserData(GameDifficulty.EASY);
        medium.setUserData(GameDifficulty.MEDIUM);
        hard.setUserData(GameDifficulty.HARD);

        HBox difficultyButtons = new HBox(20);
        difficultyButtons.setAlignment(Pos.CENTER);
        difficultyButtons.getChildren().addAll(easy, medium, hard);

        difficultyBox.getChildren().addAll(difficultyLabel, difficultyButtons);
        difficultyBox.setUserData(difficultyGroup); // Store reference for later use

        return difficultyBox;
    }

    private HBox createButtonBox(TextField nameField,
                                 ToggleGroup difficultyGroup,
                                 BiConsumer<String, GameDifficulty> onStartGame,
                                 Runnable onShowLeaderboard,
                                 Runnable onExit) {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button startButton = UIComponents.createStyledButton("🎮 START GAME", UIComponents.Colors.LIGHT_GREEN);
        Button leaderboardButton = UIComponents.createStyledButton("🏆 LEADERBOARD", UIComponents.Colors.GOLD);
        Button exitButton = UIComponents.createStyledButton("❌ EXIT", UIComponents.Colors.LIGHT_CORAL);

        startButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Please enter your name!");
                return;
            }
            RadioButton selected = (RadioButton) difficultyGroup.getSelectedToggle();
            GameDifficulty difficulty = (GameDifficulty) selected.getUserData();
            onStartGame.accept(name, difficulty);
        });

        leaderboardButton.setOnAction(e -> onShowLeaderboard.run());
        exitButton.setOnAction(e -> onExit.run());

        buttonBox.getChildren().addAll(startButton, leaderboardButton, exitButton);
        return buttonBox;
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

}