package com.example.memory.model;

// GameDifficulty.java - Difficulty Settings
public enum GameDifficulty {
    EASY(4, 4, "Easy (4x4)"),
    MEDIUM(4, 6, "Medium (4x6)"),
    HARD(6, 6, "Hard (6x6)");

    private final int rows;
    private final int cols;
    private final String displayName;

    GameDifficulty(int rows, int cols, String displayName) {
        this.rows = rows;
        this.cols = cols;
        this.displayName = displayName;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTotalCards() { return rows * cols; }
    public int getTotalPairs() { return getTotalCards() / 2; }
    public String getDisplayName() { return displayName; }
    public String getGridDisplay() {
        return rows + "x" + cols;
    }
}
