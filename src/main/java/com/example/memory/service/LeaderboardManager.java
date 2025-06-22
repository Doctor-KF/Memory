package com.example.memory.service;

import com.example.memory.model.GameResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static List<GameResult> allResults = new ArrayList<>();

    static {
        loadLeaderboard();
    }

    public static void addScore(GameResult result) {
        allResults.add(result);
        saveLeaderboard();
    }

    public static List<GameResult> getLeaderboard(String difficulty) {
        return allResults.stream()
                .filter(result -> result.getDifficulty().equals(difficulty))
                .sorted((a, b) -> {
                    // Sort by score (descending), then by time (ascending), then by attempts (ascending)
                    if (b.getScore() != a.getScore()) {
                        return Integer.compare(b.getScore(), a.getScore());
                    }
                    if (a.getTimeInSeconds() != b.getTimeInSeconds()) {
                        return Integer.compare(a.getTimeInSeconds(), b.getTimeInSeconds());
                    }
                    return Integer.compare(a.getAttempts(), b.getAttempts());
                })
                .limit(10) // Top 10 scores
                .collect(Collectors.toList());
    }

    public static void clearLeaderboard(String difficulty) {
        allResults.removeIf(result -> result.getDifficulty().equals(difficulty));
        saveLeaderboard();
    }

    public static void clearAllLeaderboards() {
        allResults.clear();
        saveLeaderboard();
    }

    private static void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(allResults);
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            allResults = (List<GameResult>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading leaderboard (corrupted file detected): " + e.getMessage());
            System.out.println("Creating fresh leaderboard...");

            // Delete the corrupted file and start fresh
            if (file.delete()) {
                System.out.println("Corrupted leaderboard file deleted successfully.");
            } else {
                System.err.println("Failed to delete corrupted leaderboard file.");
            }

            allResults = new ArrayList<>();
        }
    }
}