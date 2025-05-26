package com.cardgame.poker;

import com.cardgame.core.Card;
import com.cardgame.core.HandEvaluator;
import java.util.*;

/**
 * Hand evaluator for Texas Hold'em Poker.
 * Evaluates the best 5-card hand from 5-7 cards (2 hole + 3-5 community cards) and compares hands.
 * 
 * <p>This implementation handles all standard poker hand rankings and includes tie-breaking logic.
 * It's optimized for performance by checking hand strengths from highest to lowest and
 * returning early when a hand is determined to be of a certain rank.</p>
 * 
 * <p>Example usage:
 * <pre>
 * List<Card> cards = // player's hole cards + community cards
 * PokerHandEvaluator evaluator = new PokerHandEvaluator();
 * PokerHandEvaluator.PokerHandResult result = evaluator.evaluateBestHand(cards);
 * System.out.println("Best hand: " + result.rank);
 * </pre>
 * </p>
 */
public class PokerHandEvaluator extends HandEvaluator {
    
    /**
     * Validates that the provided cards list is valid for poker hand evaluation.
     * @param cards List of cards to validate
     * @throws IllegalArgumentException if cards is null, empty, or contains duplicates
     */
    private void validateCards(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Cards list cannot be null");
        }
        if (cards.size() < 5 || cards.size() > 7) {
            throw new IllegalArgumentException("Poker hand evaluation requires 5-7 cards, got " + cards.size());
        }
        
        // Check for duplicate cards
        Set<Card> cardSet = new HashSet<>(cards);
        if (cardSet.size() != cards.size()) {
            throw new IllegalArgumentException("Duplicate cards found in the hand");
        }
        
        // Check for null cards
        if (cards.contains(null)) {
            throw new IllegalArgumentException("Cards list cannot contain null elements");
        }
    }
    
    /**
     * Represents the result of hand evaluation, including hand rank and tie-breaker info.
     */
    public static class PokerHandResult {
        public final HandRank rank;
        public final List<Integer> tiebreakers; // e.g. kicker values

        public PokerHandResult(HandRank rank, List<Integer> tiebreakers) {
            this.rank = rank;
            this.tiebreakers = tiebreakers;
        }
    }

    /**
     * Enum for Poker hand rankings (from strongest to weakest)
     */
    public enum HandRank {
        ROYAL_FLUSH,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }
    

    
    /**
     * Evaluates a 5-card poker hand and determines its rank and tiebreakers.
     * This is an internal method that assumes the input is exactly 5 valid cards.
     *
     * @param hand A list of exactly 5 cards to evaluate
     * @return A PokerHandResult containing the hand's rank and tiebreaker information
     * @throws IllegalArgumentException if hand does not contain exactly 5 cards
     * @throws NullPointerException if hand or any card is null
     */
    private PokerHandResult evaluateFiveCardHand(List<Card> hand) {
        if (hand == null || hand.size() != 5) {
            throw new IllegalArgumentException("Exactly 5 cards are required");
        }
        if (hand.size() != 5) {
            throw new IllegalArgumentException("Exactly 5 cards are required");
        }
        
        // Sort cards by value (descending)
        List<Card> sorted = new ArrayList<>(hand);
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        // Get card values and suits
        List<Integer> values = new ArrayList<>();
        Set<String> suits = new HashSet<>();
        Map<Integer, Integer> valueCounts = new HashMap<>();
        
        for (Card card : sorted) {
            values.add(card.getValue());
            suits.add(card.getSuit());
            valueCounts.put(card.getValue(), valueCounts.getOrDefault(card.getValue(), 0) + 1);
        }
        
        boolean isFlush = suits.size() == 1;
        boolean isStraight = isStraight(hand);
        
        // Check for Royal Flush
        if (isFlush && isStraight && values.get(0) == 14) {
            return new PokerHandResult(HandRank.ROYAL_FLUSH, values);
        }
        
        // Check for Straight Flush
        if (isFlush && isStraight) {
            return new PokerHandResult(HandRank.STRAIGHT_FLUSH, values);
        }
        
        // Check for Four of a Kind
        for (Map.Entry<Integer, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() == 4) {
                List<Integer> tiebreakers = new ArrayList<>();
                tiebreakers.add(entry.getKey());
                // Add the kicker
                for (int val : values) {
                    if (val != entry.getKey()) {
                        tiebreakers.add(val);
                        break;
                    }
                }
                return new PokerHandResult(HandRank.FOUR_OF_A_KIND, tiebreakers);
            }
        }
        
        // Check for Full House
        boolean hasThree = false;
        int threeValue = 0;
        boolean hasTwo = false;
        int twoValue = 0;
        
        for (Map.Entry<Integer, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() == 3) {
                hasThree = true;
                threeValue = entry.getKey();
            } else if (entry.getValue() == 2) {
                hasTwo = true;
                twoValue = entry.getKey();
            }
        }
        
        if (hasThree && hasTwo) {
            return new PokerHandResult(HandRank.FULL_HOUSE, Arrays.asList(threeValue, twoValue));
        }
        
        // Check for Flush
        if (isFlush) {
            return new PokerHandResult(HandRank.FLUSH, values);
        }
        
        // Check for Straight
        if (isStraight) {
            // Handle Ace-low straight (A-2-3-4-5)
            if (values.get(0) == 14 && values.get(1) == 5) {
                List<Integer> aceLowValues = new ArrayList<>(values);
                aceLowValues.remove(0); // Remove Ace (14)
                aceLowValues.add(1);    // Add Ace as 1
                return new PokerHandResult(HandRank.STRAIGHT, aceLowValues);
            }
            return new PokerHandResult(HandRank.STRAIGHT, values);
        }
        
        // Check for Three of a Kind
        if (hasThree) {
            List<Integer> tiebreakers = new ArrayList<>();
            tiebreakers.add(threeValue);
            // Add kickers
            for (int val : values) {
                if (val != threeValue) {
                    tiebreakers.add(val);
                }
                if (tiebreakers.size() >= 3) break; // Only need 2 kickers
            }
            return new PokerHandResult(HandRank.THREE_OF_A_KIND, tiebreakers);
        }
        
        // Check for Two Pair
        List<Integer> pairs = new ArrayList<>();
        List<Integer> kickers = new ArrayList<>();
        
        for (Map.Entry<Integer, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() == 2) {
                pairs.add(entry.getKey());
            } else {
                kickers.add(entry.getKey());
            }
        }
        
        if (pairs.size() >= 2) {
            // Sort pairs in descending order
            pairs.sort(Collections.reverseOrder());
            // Sort kickers in descending order
            kickers.sort(Collections.reverseOrder());
            // Combine pairs and kickers (only need 1 kicker)
            List<Integer> tiebreakers = new ArrayList<>();
            tiebreakers.addAll(pairs);
            if (!kickers.isEmpty()) {
                tiebreakers.add(kickers.get(0));
            }
            return new PokerHandResult(HandRank.TWO_PAIR, tiebreakers);
        }
        
        // Check for One Pair
        if (pairs.size() == 1) {
            List<Integer> tiebreakers = new ArrayList<>();
            tiebreakers.add(pairs.get(0));
            // Add kickers (3 highest cards not in the pair)
            for (int val : values) {
                if (val != pairs.get(0)) {
                    tiebreakers.add(val);
                }
                if (tiebreakers.size() >= 4) break; // Only need 3 kickers
            }
            return new PokerHandResult(HandRank.ONE_PAIR, tiebreakers);
        }
        
        // High Card
        return new PokerHandResult(HandRank.HIGH_CARD, values);
    }
    

    
    /**
     * Compares two PokerHandResult objects to determine which represents a stronger hand.
     * Used internally for sorting and finding the best hand from multiple combinations.
     *
     * @param result1 First hand result to compare
     * @param result2 Second hand result to compare
     * @return Positive if result1 is stronger, negative if result2 is stronger, 0 if equal
     * @throws NullPointerException if either result is null
     */
    public int compareHandResults(PokerHandResult result1, PokerHandResult result2) {
        if (result1 == null || result2 == null) {
            throw new NullPointerException("Cannot compare null PokerHandResult objects");
        }
        
        // First compare hand ranks (lower ordinal = stronger hand)
        int rankCompare = result1.rank.compareTo(result2.rank);
        if (rankCompare != 0) {
            return -rankCompare;
        }
        
        // If same rank, compare tiebreakers in order
        List<Integer> t1 = result1.tiebreakers;
        List<Integer> t2 = result2.tiebreakers;
        
        for (int i = 0; i < Math.min(t1.size(), t2.size()); i++) {
            int cmp = Integer.compare(t1.get(i), t2.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        
        // If all tiebreakers are equal, the hands are of equal strength
        return 0;
    }
    
    /**
     * Compares two poker hands to determine which one is stronger.
     * @param hand1 First player's cards (2 hole + 5 community)
     * @param hand2 Second player's cards (2 hole + 5 community)
     * @return true if hand1 is stronger than hand2, false otherwise
     */
    @Override
    public boolean compareHands(List<Card> hand1, List<Card> hand2) {
        PokerHandResult result1 = evaluateBestHand(hand1);
        PokerHandResult result2 = evaluateBestHand(hand2);
        
        // First compare hand ranks
        int rankCompare = result1.rank.compareTo(result2.rank);
        if (rankCompare < 0) {
            return true;  // hand1 is stronger (lower ordinal = higher rank)
        } else if (rankCompare > 0) {
            return false; // hand2 is stronger
        }
        
        // If same rank, compare tiebreakers
        for (int i = 0; i < Math.min(result1.tiebreakers.size(), result2.tiebreakers.size()); i++) {
            int cmp = Integer.compare(result1.tiebreakers.get(i), result2.tiebreakers.get(i));
            if (cmp > 0) {
                return true;  // hand1 has higher tiebreaker
            } else if (cmp < 0) {
                return false; // hand2 has higher tiebreaker
            }
        }
        
        // If all tiebreakers are equal, it's a tie
        return false;
    }
    
    /**
     * Evaluates the strongest possible 5-card hand from the given cards according to Poker rules.
     * This method considers all possible 5-card combinations when more than 5 cards are provided.
     *
     * @param cards The list of 5-7 cards to evaluate (typically 2 hole cards + 3-5 community cards)
     * @return A PokerHandResult object containing the hand's rank and tiebreaker information
     * @throws IllegalArgumentException if cards is null, has less than 5 or more than 7 cards,
     *         contains duplicates, or contains null elements
     */
    @Override
    public PokerHandResult evaluateBestHand(List<Card> cards) {
        validateCards(cards);
        
        // If exactly 5 cards, just evaluate them directly
        if (cards.size() == 5) {
            return evaluateFiveCardHand(cards);
        }
        
        // For 6-7 cards, find the best 5-card combination
        PokerHandResult bestResult = null;
        
        // Generate all possible 5-card combinations
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                for (int k = j + 1; k < cards.size(); k++) {
                    for (int l = k + 1; l < cards.size(); l++) {
                        for (int m = l + 1; m < cards.size(); m++) {
                            List<Card> combo = Arrays.asList(
                                cards.get(i), cards.get(j), cards.get(k), 
                                cards.get(l), cards.get(m));
                                
                            PokerHandResult result = evaluateFiveCardHand(combo);
                            if (bestResult == null || compareHandResults(result, bestResult) > 0) {
                                bestResult = result;
                                
                                // Early exit if we find the best possible hand
                                if (bestResult.rank == HandRank.ROYAL_FLUSH) {
                                    return bestResult;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return bestResult;
    }

}
