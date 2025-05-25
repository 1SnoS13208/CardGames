package com.cardgame.poker;

import com.cardgame.core.Card;
import com.cardgame.core.ScoringStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scoring strategy for Poker
 */
public class PokerScoringStrategy implements ScoringStrategy {
    // Hand rankings
    private static final int HIGH_CARD = 1;
    private static final int PAIR = 2;
    private static final int TWO_PAIR = 3;
    private static final int THREE_OF_A_KIND = 4;
    private static final int STRAIGHT = 5;
    private static final int FLUSH = 6;
    private static final int FULL_HOUSE = 7;
    private static final int FOUR_OF_A_KIND = 8;
    private static final int STRAIGHT_FLUSH = 9;
    private static final int ROYAL_FLUSH = 10;
    
    /**
     * Calculates the score of a Poker hand
     * 
     * @param hand The hand to calculate the score for
     * @return The score of the hand based on poker hand rankings
     */
    @Override
    public int calculateScore(List<Card> hand) {
        if (hand == null || hand.size() != 5) {
            return 0; // Invalid hand
        }
        
        if (isRoyalFlush(hand)) return ROYAL_FLUSH * 100;
        if (isStraightFlush(hand)) return STRAIGHT_FLUSH * 100;
        if (isFourOfAKind(hand)) return FOUR_OF_A_KIND * 100;
        if (isFullHouse(hand)) return FULL_HOUSE * 100;
        if (isFlush(hand)) return FLUSH * 100;
        if (isStraight(hand)) return STRAIGHT * 100;
        if (isThreeOfAKind(hand)) return THREE_OF_A_KIND * 100;
        if (isTwoPair(hand)) return TWO_PAIR * 100;
        if (isPair(hand)) return PAIR * 100;
        
        // High card
        return HIGH_CARD * 100 + getHighCardValue(hand);
    }
    
    /**
     * Compares two Poker hands
     * 
     * @param hand1 The first hand
     * @param hand2 The second hand
     * @return A positive number if hand1 is better, a negative number if hand2 is better, or 0 if they are equal
     */
    @Override
    public int compareHands(List<Card> hand1, List<Card> hand2) {
        int score1 = calculateScore(hand1);
        int score2 = calculateScore(hand2);
        
        return Integer.compare(score1, score2);
    }
    
    /**
     * In Poker, a hand is never "busted"
     * 
     * @param hand The hand to check
     * @return Always false in Poker
     */
    @Override
    public boolean isBusted(List<Card> hand) {
        return false; // In Poker, you can't "bust"
    }
    
    // Helper methods for hand evaluation
    
    private boolean isRoyalFlush(List<Card> hand) {
        if (!isFlush(hand) || !isStraight(hand)) return false;
        
        // Check if the highest card is an Ace
        List<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort(Comparator.comparing(this::getCardValue).reversed());
        
        return getCardValue(sortedHand.get(0)) == 14; // Ace value
    }
    
    private boolean isStraightFlush(List<Card> hand) {
        return isFlush(hand) && isStraight(hand);
    }
    
    private boolean isFourOfAKind(List<Card> hand) {
        Map<String, Integer> rankCount = countRanks(hand);
        
        for (Integer count : rankCount.values()) {
            if (count == 4) return true;
        }
        
        return false;
    }
    
    private boolean isFullHouse(List<Card> hand) {
        Map<String, Integer> rankCount = countRanks(hand);
        
        boolean hasThree = false;
        boolean hasPair = false;
        
        for (Integer count : rankCount.values()) {
            if (count == 3) hasThree = true;
            if (count == 2) hasPair = true;
        }
        
        return hasThree && hasPair;
    }
    
    private boolean isFlush(List<Card> hand) {
        String suit = hand.get(0).getSuit();
        
        for (Card card : hand) {
            if (!card.getSuit().equals(suit)) return false;
        }
        
        return true;
    }
    
    private boolean isStraight(List<Card> hand) {
        List<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort(Comparator.comparing(this::getCardValue));
        
        int prevValue = getCardValue(sortedHand.get(0));
        
        for (int i = 1; i < sortedHand.size(); i++) {
            int currValue = getCardValue(sortedHand.get(i));
            if (currValue != prevValue + 1) return false;
            prevValue = currValue;
        }
        
        return true;
    }
    
    private boolean isThreeOfAKind(List<Card> hand) {
        Map<String, Integer> rankCount = countRanks(hand);
        
        for (Integer count : rankCount.values()) {
            if (count == 3) return true;
        }
        
        return false;
    }
    
    private boolean isTwoPair(List<Card> hand) {
        Map<String, Integer> rankCount = countRanks(hand);
        
        int pairCount = 0;
        for (Integer count : rankCount.values()) {
            if (count == 2) pairCount++;
        }
        
        return pairCount == 2;
    }
    
    private boolean isPair(List<Card> hand) {
        Map<String, Integer> rankCount = countRanks(hand);
        
        for (Integer count : rankCount.values()) {
            if (count == 2) return true;
        }
        
        return false;
    }
    
    private Map<String, Integer> countRanks(List<Card> hand) {
        Map<String, Integer> rankCount = new HashMap<>();
        
        for (Card card : hand) {
            String rank = card.getRank();
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }
        
        return rankCount;
    }
    
    private int getHighCardValue(List<Card> hand) {
        List<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort(Comparator.comparing(this::getCardValue).reversed());
        
        return getCardValue(sortedHand.get(0));
    }
    
    private int getCardValue(Card card) {
        String rank = card.getRank();
        
        if (rank.equals("A")) return 14;
        if (rank.equals("K")) return 13;
        if (rank.equals("Q")) return 12;
        if (rank.equals("J")) return 11;
        
        return Integer.parseInt(rank);
    }
}
