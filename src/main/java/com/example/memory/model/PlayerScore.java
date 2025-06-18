package com.example.memory.model;

// PlayerScore.java - Player Score Data
public class PlayerScore {
    private final String name;
    private final int score;
    private final int attempts;
    private final long time;
    private final GameDifficulty difficulty;

    public PlayerScore(String name, int score, int attempts, long time, GameDifficulty difficulty) {
        this.name = name;
        this.score = score;
        this.attempts = attempts;
        this.time = time;
        this.difficulty = difficulty;
    }

    // For loading from file (legacy support)
    public PlayerScore(String name, int score, int attempts, long time, int gridSize) {
        this.name = name;
        this.score = score;
        this.attempts = attempts;
        this.time = time;
        // Convert old gridSize to difficulty
        if (gridSize == 16) {  // Easy: 4x4 = 16 cards
            this.difficulty = GameDifficulty.EASY;
        } else if (gridSize == 24) {  // Medium: 4x6 = 24 cards
            this.difficulty = GameDifficulty.MEDIUM;
        } else {  // Hard: 6x6 = 36 cards (or any other size)
            this.difficulty = GameDifficulty.HARD;
        }
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public int getAttempts() { return attempts; }
    public long getTime() { return time; }
    public GameDifficulty getDifficulty() { return difficulty; }

    public String getFormattedTime() {
        return String.format("%02d:%02d", (int)time/60, (int)time%60);
    }

    public String toFileString() {
        return String.join(",",
                name,
                String.valueOf(score),
                String.valueOf(attempts),
                String.valueOf(time),
                String.valueOf(difficulty.ordinal())
        );
    }

    public static PlayerScore fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 5) {
            try {
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                int attempts = Integer.parseInt(parts[2]);
                long time = Long.parseLong(parts[3]);

                // Handle both old format (gridSize) and new format (difficulty ordinal)
                int value = Integer.parseInt(parts[4]);
                GameDifficulty difficulty;
                if (value < 10) { // New format - difficulty ordinal
                    difficulty = GameDifficulty.values()[value];
                } else { // Old format - gridSize
                    return new PlayerScore(name, score, attempts, time, value);
                }

                return new PlayerScore(name, score, attempts, time, difficulty);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }
        return null;
    }
}