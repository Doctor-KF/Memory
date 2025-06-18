// GameModel.java - Game State Management
package com.example.memory.model;

import javafx.scene.control.RadioButton;

import java.util.*;

public class GameModel {
    private String playerName;
    private GameDifficulty difficulty;
    private int attempts;
    private int foundPairs;
    private int totalPairs;
    private long startTime;
    private int currentScore;
    private GameState gameState;
    private List<String> gameSymbols;

    // Available symbols for the memory game
    private static final List<String> AVAILABLE_SYMBOLS = Arrays.asList(
            "🎮", "🎯", "🎲", "🎪", "🎨", "🎭", "🎸", "🎵",
            "🚀", "⭐", "🔥", "💎", "🌟", "🎊", "🎈", "🎁",
            "🍕", "🚒"
    );

    public GameModel() {
        reset();
    }

    public void reset() {
        this.attempts = 0;
        this.foundPairs = 0;
        this.currentScore = 0;
        this.gameState = GameState.NOT_STARTED;
        this.gameSymbols = new ArrayList<>();
    }

    public void initializeGame(String playerName, GameDifficulty difficulty) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.totalPairs = difficulty.getTotalPairs();
        this.startTime = System.currentTimeMillis();
        this.gameState = GameState.PLAYING;
        generateSymbols();
    }

    private void generateSymbols() {
        gameSymbols.clear();

        // Create pairs
        for (int i = 0; i < totalPairs; i++) {
            String symbol = AVAILABLE_SYMBOLS.get(i % AVAILABLE_SYMBOLS.size());
            gameSymbols.add(symbol);
            gameSymbols.add(symbol);
        }

        Collections.shuffle(gameSymbols);
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void incrementFoundPairs() {
        foundPairs++;
        calculateScore();

        if (foundPairs == totalPairs) {
            gameState = GameState.COMPLETED;
        }
    }

    private void calculateScore() {
        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        int timeBonus = Math.max(0, 300 - (int)timeElapsed);
        int attemptPenalty = (attempts - foundPairs) * 5;
        int baseScore = foundPairs * 100;

        currentScore = Math.max(0, baseScore + timeBonus - attemptPenalty);
    }

    public long getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public PlayerScore createPlayerScore() {
        return new PlayerScore(playerName, currentScore, attempts, getElapsedTime(), difficulty);
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public GameDifficulty getDifficulty() { return difficulty; }
    public int getAttempts() { return attempts; }
    public int getFoundPairs() { return foundPairs; }
    public int getTotalPairs() { return totalPairs; }
    public int getCurrentScore() { return currentScore; }
    public GameState getGameState() { return gameState; }
    public List<String> getGameSymbols() { return new ArrayList<>(gameSymbols); }
    public boolean isGameComplete() { return gameState == GameState.COMPLETED; }
}

// GameState.java - Game State Enum
enum GameState {
    NOT_STARTED,
    PLAYING,
    COMPLETED
}

