package com.cardgame.blackjack;

import com.cardgame.core.Player;

/**
 * Represents a player in the Blackjack game
 */
public class BlackjackPlayer extends Player {
    private boolean isDealer;
    private int chips;

    /**
     * Creates a new Blackjack player
     * 
     * @param name The player's name
     * @param isDealer Whether this player is the dealer
     */
    public BlackjackPlayer(String name, boolean isDealer) {
        super(name);
        this.isDealer = isDealer;
        this.chips = isDealer ? 0 : 1000; // Dealer starts with 0 chips
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
     * Checks if this player is the dealer
     * 
     * @return true if this player is the dealer, false otherwise
     */
    public boolean isDealer() {
        return isDealer;
    }

    /**
     * Returns a string representation of the player
     * 
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        if (isDealer) {
            return "Dealer's cards: " + getHand();
        }
        return super.getName() + "'s hand: " + getHand();
    }
}