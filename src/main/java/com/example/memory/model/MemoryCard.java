package com.example.memory.model;

import javafx.scene.control.Button;

public class MemoryCard extends Button {
    private String symbol;
    private boolean flipped;
    private boolean matched;

    public MemoryCard(String symbol) {
        this.symbol = symbol;
        this.flipped = false;
        this.matched = false;

        // Set initial appearance
        setText("");
        getStyleClass().add("memory-card");
    }

    public void flip() {
        flipped = !flipped;
        updateAppearance();
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
        updateAppearance();
    }

    private void updateAppearance() {
        getStyleClass().removeAll("flipped", "matched", "wrong");

        if (matched) {
            setText(symbol);
            getStyleClass().add("matched");
        } else if (flipped) {
            setText(symbol);
            getStyleClass().add("flipped");
        } else {
            setText("");
        }
    }

    public void showWrong() {
        getStyleClass().removeAll("flipped", "matched");
        getStyleClass().add("wrong");
        setText(symbol);
    }

    // Getters
    public String getSymbol() {
        return symbol;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public boolean isMatched() {
        return matched;
    }
}