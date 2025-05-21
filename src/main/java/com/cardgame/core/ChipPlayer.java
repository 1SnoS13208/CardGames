package com.cardgame.core;

/**
 * Abstract class for players that use chips (betting games)
 */
public abstract class ChipPlayer extends Player {
    protected int chips;

    /**
     * Creates a new ChipPlayer with the given name and starting chips
     * @param name Player's name
     * @param startingChips Initial chip count
     */
    public ChipPlayer(String name, int startingChips) {
        super(name);
        this.chips = startingChips;
    }

    /**
     * Gets the player's current chip count
     * @return Number of chips
     */
    public int getChips() { 
        return chips; 
    }

    /**
     * Adds chips to the player's chip count
     * @param amount Chips to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void addChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative chips");
        }
        chips += amount;
    }

    /**
     * Removes chips from the player's chip count
     * @param amount Chips to remove
     * @return true if chips were removed, false if not enough chips
     * @throws IllegalArgumentException if amount is negative
     */
    public boolean removeChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot remove negative chips");
        }
        if (chips >= amount) {
            chips -= amount;
            return true;
        }
        return false;
    }
}
