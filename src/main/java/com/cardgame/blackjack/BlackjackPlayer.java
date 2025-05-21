package com.cardgame.blackjack;

import com.cardgame.core.ChipPlayer;

/**
 * Represents a player in the Blackjack game
 */
public class BlackjackPlayer extends ChipPlayer {
    private boolean isDealer;

    /**
     * Creates a new Blackjack player
     * 
     * @param name The player's name
     * @param isDealer Whether this player is the dealer
     */
    public BlackjackPlayer(String name, boolean isDealer) {
        super(name, isDealer ? 0 : 1000);
        this.isDealer = isDealer;
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