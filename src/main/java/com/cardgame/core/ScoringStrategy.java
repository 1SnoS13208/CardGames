package com.cardgame.core;

import java.util.List;

/**
 * Interface for defining how to score and compare hands in a card game
 */
public interface ScoringStrategy {
    /**
     * Calculates the score of a hand
     * 
     * @param hand The hand to calculate the score for
     * @return The score of the hand
     */
    int calculateScore(List<Card> hand);
    
    /**
     * Compares two hands to determine which is better
     * 
     * @param hand1 The first hand
     * @param hand2 The second hand
     * @return A positive number if hand1 is better, a negative number if hand2 is better, or 0 if they are equal
     */
    int compareHands(List<Card> hand1, List<Card> hand2);
    
    /**
     * Checks if a hand is busted (exceeded the maximum allowed score)
     * 
     * @param hand The hand to check
     * @return true if the hand is busted, false otherwise
     */
    boolean isBusted(List<Card> hand);
}
