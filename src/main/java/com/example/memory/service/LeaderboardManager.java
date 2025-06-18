// LeaderboardManager.java - Handles leaderboard operations
package com.example.memory.service;

import com.example.memory.model.PlayerScore;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private final List<PlayerScore> leaderboard;

    public LeaderboardManager() {
        this.leaderboard = new ArrayList<>();
        loadLeaderboard();
    }

    public void addScore(PlayerScore score) {
        leaderboard.add(score);
        sortLeaderboard();
        saveLeaderboard();
    }

    public List<PlayerScore> getTopScores(int limit) {
        return leaderboard.stream()
                .limit(limit)
                .toList();
    }

    public List<PlayerScore> getAllScores() {
        return new ArrayList<>(leaderboard);
    }

    public boolean isEmpty() {
        return leaderboard.isEmpty();
    }

    private void sortLeaderboard() {
        leaderboard.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
    }

    private void loadLeaderboard() {
        try {
            if (Files.exists(Paths.get(LEADERBOARD_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(LEADERBOARD_FILE));
                for (String line : lines) {
                    PlayerScore score = PlayerScore.fromFileString(line);
                    if (score != null) {
                        leaderboard.add(score);
                    }
                }
                sortLeaderboard();
            }
        } catch (IOException e) {
            System.err.println("Could not load leaderboard: " + e.getMessage());
        }
    }

    private void saveLeaderboard() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (PlayerScore score : leaderboard) {
                writer.println(score.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Could not save leaderboard: " + e.getMessage());
        }
    }
}
