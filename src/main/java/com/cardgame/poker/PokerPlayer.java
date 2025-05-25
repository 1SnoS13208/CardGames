package com.cardgame.poker;


import com.cardgame.core.ChipPlayer;
import com.cardgame.core.Card;

/**
 * Represents a player in the Poker game
 */
public class PokerPlayer extends ChipPlayer {
    private int currentBet;
    private boolean hasFolded;

    /**
     * Creates a new Poker player
     * 
     * @param name The player's name
     */
    public PokerPlayer(String name) {
        super(name, 1000);
        this.currentBet = 0;
        this.hasFolded = false;
    }

    /**
     * Gets the player's current chip count
     * 
     * @return The player's chip count
     */
    public int getChips() { 
        return chips; 
    }
    
    /**
     * Adds chips to the player's chip count
     * 
     * @param amount The amount of chips to add
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
     * 
     * @param amount The amount of chips to remove
     * @return true if the chips were successfully removed, false if the player doesn't have enough chips
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

    /**
     * Places a bet
     * 
     * @param amount The amount to bet
     * @return true if the bet was placed successfully, false otherwise
     */
    public boolean placeBet(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }
        
        if (chips >= amount) {
            chips -= amount;
            currentBet += amount;
            return true;
        }
        return false;
    }
    
    /**
     * Gets the player's current bet
     * 
     * @return The player's current bet
     */
    public int getCurrentBet() {
        return currentBet;
    }
    
    /**
     * Resets the player's current bet to 0
     */
    public void resetBet() {
        currentBet = 0;
    }
    
    /**
     * Marks the player as folded
     */
    public void fold() {
        hasFolded = true;
    }
    
    /**
     * Checks if the player has folded
     * 
     * @return true if the player has folded, false otherwise
     */
    public boolean hasFolded() {
        return hasFolded;
    }
    
    /**
     * Resets the player's folded status
     */
    public void resetFold() {
        hasFolded = false;
    }
    
    /**
     * Clears the player's hand and resets their bet and fold status
     */
    @Override
    public void clearHand() {
        super.clearHand();
        resetBet();
        resetFold();
    }

    /**
     * Returns a string representation of the player
     * 
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        return getName() + " (Chips: " + chips + ", Bet: " + currentBet + "): " + getHand();
    }
}
