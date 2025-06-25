package com.example.memory.manager;

import com.example.memory.model.GameResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the leaderboard for the Memory Game.
 * Stores, loads, and filters game results.
 */
public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static List<GameResult> allResults = new ArrayList<>();

    static {
        loadLeaderboard();
    }

    /**
     * Adds a new game result to the leaderboard and saves it.
     *
     * @param result The game result.
     */
    public static void addScore(GameResult result) {
        allResults.add(result);
        saveLeaderboard();
    }

    /**
     * Returns the leaderboard for a specific difficulty.
     *
     * @param difficulty Difficulty ("Easy", "Medium", "Hard").
     * @return List of top game results.
     */
    public static List<GameResult> getLeaderboard(String difficulty) {
        return allResults.stream()
                .filter(result -> result.getDifficulty().equals(difficulty))
                .sorted((a, b) -> {
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

    /**
     * Clears the leaderboard for a specific difficulty.
     *
     * @param difficulty Difficulty.
     */
    public static void clearLeaderboard(String difficulty) {
        allResults.removeIf(result -> result.getDifficulty().equals(difficulty));
        saveLeaderboard();
    }

    /**
     * Clears all leaderboards.
     */
    public static void clearAllLeaderboards() {
        allResults.clear();
        saveLeaderboard();
    }

    /**
     * Saves the leaderboard to a file.
     */
    private static void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(allResults);
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    /**
     * Loads the leaderboard from the file.
     * If an error occurs, a new empty leaderboard is created.
     */
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

            if (file.delete()) {
                System.out.println("Corrupted leaderboard file deleted successfully.");
            } else {
                System.err.println("Failed to delete corrupted leaderboard file.");
            }

            allResults = new ArrayList<>();
        }
    }
}