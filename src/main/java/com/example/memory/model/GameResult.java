package com.example.memory.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private int attempts;
    private int timeInSeconds;
    private String difficulty;
    private Date date;

    public GameResult(String playerName, int score, int attempts, int timeInSeconds, String difficulty, Date date) {
        this.playerName = playerName;
        this.score = score;
        this.attempts = attempts;
        this.timeInSeconds = timeInSeconds;
        this.difficulty = difficulty;
        this.date = date;
    }

    // Getters
    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public String getTimeFormatted() {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }

    public int getRank() {
        return 0; // Will be set by the leaderboard
    }
}