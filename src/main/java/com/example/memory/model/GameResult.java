package com.example.memory.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the result of a completed Memory Game.
 */
public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private int attempts;
    private int timeInSeconds;
    private String difficulty;
    private Date date;

    /**
     * Creates a new GameResult.
     *
     * @param playerName Name of the player.
     * @param score Achieved score.
     * @param attempts Number of attempts.
     * @param timeInSeconds Time taken in seconds.
     * @param difficulty Difficulty level.
     * @param date Date of the game.
     */
    public GameResult(String playerName, int score, int attempts, int timeInSeconds, String difficulty, Date date) {
        this.playerName = playerName;
        this.score = score;
        this.attempts = attempts;
        this.timeInSeconds = timeInSeconds;
        this.difficulty = difficulty;
        this.date = date;
    }

    /**
     * Returns the player's name.
     *
     * @return Player name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the score.
     *
     * @return Score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the number of attempts.
     *
     * @return Attempts.
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Returns the time taken in seconds.
     *
     * @return Time in seconds.
     */
    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    /**
     * Returns the time formatted as MM:SS.
     *
     * @return Time as a String.
     */
    public String getTimeFormatted() {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Returns the difficulty level.
     *
     * @return Difficulty.
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the date of the game.
     *
     * @return Date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the date formatted as MM/dd/yyyy.
     *
     * @return Date as a String.
     */
    public String getDateFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }

    /**
     * Returns the rank (always 0, may be set externally).
     *
     * @return Rank.
     */
    public int getRank() {
        return 0;
    }
}