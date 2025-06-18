// LeaderboardView.java - Leaderboard UI
package com.example.memory.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.example.memory.model.PlayerScore;
import com.example.memory.ui.UIComponents;

import java.util.List;

public class LeaderboardView {

    public Scene createScene(List<PlayerScore> topScores, boolean isEmpty, Runnable onBackToMenu) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + UIComponents.BACKGROUND_COLOR + ";");

        Label title = UIComponents.createTitleLabel("🏆 LEADERBOARD", UIComponents.Colors.GOLD, 36);

        // Leaderboard table
        VBox leaderboardBox = createLeaderboardBox(topScores, isEmpty);

        // Back button
        Button backButton = UIComponents.createStyledButton("🏠 BACK TO MENU", UIComponents.Colors.CYAN);
        backButton.setOnAction(e -> onBackToMenu.run());

        root.getChildren().addAll(title, leaderboardBox, backButton);

        return new Scene(root, 800, 600);
    }

    private VBox createLeaderboardBox(List<PlayerScore> topScores, boolean isEmpty) {
        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setMaxWidth(600);

        // Header
        HBox header = createHeader();
        leaderboardBox.getChildren().add(header);

        if (isEmpty) {
            Label noScores = UIComponents.createInfoLabel("No scores yet! Be the first to play!",
                    UIComponents.Colors.LIGHT_GRAY, 16);
            leaderboardBox.getChildren().add(noScores);
        } else {
            // Leaderboard entries
            for (int i = 0; i < topScores.size(); i++) {
                PlayerScore player = topScores.get(i);
                HBox entry = createLeaderboardEntry(i + 1, player);
                leaderboardBox.getChildren().add(entry);
            }
        }

        return leaderboardBox;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + UIComponents.CARD_COLOR + "; -fx-padding: 10; -fx-border-radius: 5;");

        Label rankLabel = UIComponents.createLeaderboardLabel("Rank", UIComponents.Colors.LIGHT_GRAY, 80);
        Label nameLabel = UIComponents.createLeaderboardLabel("Name", UIComponents.Colors.LIGHT_GRAY, 150);
        Label scoreLabel = UIComponents.createLeaderboardLabel("Score", UIComponents.Colors.LIGHT_GRAY, 100);
        Label attemptsLabel = UIComponents.createLeaderboardLabel("Attempts", UIComponents.Colors.LIGHT_GRAY, 100);
        Label timeLabel = UIComponents.createLeaderboardLabel("Time", UIComponents.Colors.LIGHT_GRAY, 100);
        Label difficultyLabel = UIComponents.createLeaderboardLabel("Grid", UIComponents.Colors.LIGHT_GRAY, 70);

        header.getChildren().addAll(rankLabel, nameLabel, scoreLabel, attemptsLabel, timeLabel, difficultyLabel);
        return header;
    }

    private HBox createLeaderboardEntry(int rank, PlayerScore player) {
        HBox entry = new HBox();
        entry.setAlignment(Pos.CENTER);
        entry.setStyle("-fx-background-color: " + UIComponents.PANEL_COLOR + "; -fx-padding: 8; -fx-border-radius: 3;");

        Color rankColor = getRankColor(rank);
        String rankIcon = getRankIcon(rank);

        Label rankLabel = UIComponents.createLeaderboardLabel(rankIcon, rankColor, 80);
        Label nameLabel = UIComponents.createLeaderboardLabel(player.getName(), UIComponents.Colors.CYAN, 150);
        Label scoreLabel = UIComponents.createLeaderboardLabel(String.valueOf(player.getScore()),
                UIComponents.Colors.LIGHT_GREEN, 100);
        Label attemptsLabel = UIComponents.createLeaderboardLabel(String.valueOf(player.getAttempts()),
                UIComponents.Colors.YELLOW, 100);
        Label timeLabel = UIComponents.createLeaderboardLabel(player.getFormattedTime(),
                UIComponents.Colors.ORANGE, 100);
        Label difficultyLabel = UIComponents.createLeaderboardLabel(player.getDifficulty().getGridDisplay(),
                UIComponents.Colors.LIGHT_GRAY, 70);

        entry.getChildren().addAll(rankLabel, nameLabel, scoreLabel, attemptsLabel, timeLabel, difficultyLabel);
        return entry;
    }

    private Color getRankColor(int rank) {
        return switch (rank) {
            case 1 -> UIComponents.Colors.GOLD;
            case 2 -> UIComponents.Colors.SILVER;
            case 3 -> UIComponents.Colors.BRONZE;
            default -> UIComponents.Colors.WHITE;
        };
    }

    private String getRankIcon(int rank) {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> String.valueOf(rank);
        };
    }
}