package com.example.memory.model;

import javafx.scene.control.Button;

/**
 * Represents a memory card in the game.
 * Inherits from Button and contains symbol, state, and display methods.
 */
public class MemoryCard extends Button {
    private String symbol;
    private boolean flipped;
    private boolean matched;

    /**
     * Creates a new MemoryCard with the given symbol.
     *
     * @param symbol The symbol of the card.
     */
    public MemoryCard(String symbol) {
        this.symbol = symbol;
        this.flipped = false;
        this.matched = false;

        setText("");
        getStyleClass().add("memory-card");
    }

    /**
     * Flips the card (shows/hides the symbol).
     */
    public void flip() {
        flipped = !flipped;
        updateAppearance();
    }

    /**
     * Sets the matched state of the card.
     *
     * @param matched true if the card has been matched.
     */
    public void setMatched(boolean matched) {
        this.matched = matched;
        updateAppearance();
    }

    /**
     * Updates the appearance of the card based on its state.
     */
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

    /**
     * Shows the card as incorrectly matched.
     */
    public void showWrong() {
        getStyleClass().removeAll("flipped", "matched");
        getStyleClass().add("wrong");
        setText(symbol);
    }

    /**
     * Returns the symbol of the card.
     *
     * @return Symbol as a String.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns whether the card is currently flipped.
     *
     * @return true if flipped.
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Returns whether the card has already been matched.
     *
     * @return true if matched.
     */
    public boolean isMatched() {
        return matched;
    }
}