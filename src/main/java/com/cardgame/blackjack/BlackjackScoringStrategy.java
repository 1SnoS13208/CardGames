package com.cardgame.blackjack;

import com.cardgame.core.Card;
import com.cardgame.core.ScoringStrategy;
import java.util.List;

/**
 * Scoring strategy for Blackjack
 */
public class BlackjackScoringStrategy implements ScoringStrategy {
    
    /**
     * Calculates the score of a Blackjack hand, accounting for Aces
     * 
     * @param hand The hand to calculate the score for
     * @return The score of the hand
     */
    @Override
    public int calculateScore(List<Card> hand) {
        int score = 0;
        int aces = 0;
        for (Card card : hand) {
            String rank = card.getRank();
            if (rank.equals("A")) {
                score += 11;
                aces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                score += 10;
            } else {
                try {
                    score += card.getValue();
                } catch (NumberFormatException e) {
                }
            }
        }
        
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }
    
    /**
     * Compares two Blackjack hands
     * 
     * @param hand1 The first hand
     * @param hand2 The second hand
     * @return A positive number if hand1 is better, a negative number if hand2 is better, or 0 if they are equal
     */
    @Override
    public int compareHands(List<Card> hand1, List<Card> hand2) {
        int score1 = calculateScore(hand1);
        int score2 = calculateScore(hand2);
        
        if (score1 > 21) return -1; // hand1 is busted
        if (score2 > 21) return 1;  // hand2 is busted
        return Integer.compare(score1, score2);
    }
    
    /**
     * Checks if a Blackjack hand is busted (over 21)
     * 
     * @param hand The hand to check
     * @return true if the hand is busted, false otherwise
     */
    @Override
    public boolean isBusted(List<Card> hand) {
        return calculateScore(hand) > 21;
    }
    
    /**
     * Checks if a hand is a Blackjack (an Ace and a 10-value card)
     * 
     * @param hand The hand to check
     * @return true if the hand is a Blackjack, false otherwise
     */
    public boolean isBlackjack(List<Card> hand) {
        if (hand.size() != 2) return false;
        
        return calculateScore(hand) == 21;
    }
}
