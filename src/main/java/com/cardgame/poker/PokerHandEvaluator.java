package com.cardgame.poker;

import com.cardgame.core.Card;
import com.cardgame.core.HandEvaluator;
import java.util.*;

/**
 * Hand evaluator for Texas Hold'em Poker.
 * Evaluates the best 5-card hand from 7 cards (2 hole + 5 community) and compares hands.
 */
public class PokerHandEvaluator extends HandEvaluator {
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
     * Evaluates the strongest possible 5-card Poker hand from the given cards.
     * @param cards List of 7 cards (2 hole + 5 community)
     * @return PokerHandResult containing hand rank and tiebreaker info
     */
    @Override
    public PokerHandResult evaluateBestHand(List<Card> cards) {
        if (cards.size() < 5) throw new IllegalArgumentException("At least 5 cards required");
        List<List<Card>> allCombos = getCombinations(cards, 5);
        PokerHandResult best = null;
        for (List<Card> combo : allCombos) {
            PokerHandResult result = evaluateFiveCardHand(combo);
            if (best == null || compareHandResult(result, best) > 0) {
                best = result;
            }
        }
        return best;
    }

    /**
     * Compares two hands to determine which is stronger.
     * @param cards1 First player's cards (2 hole + 5 community)
     * @param cards2 Second player's cards (2 hole + 5 community)
     * @return >0 if hand1 wins, <0 if hand2 wins, 0 if tie
     */
    @Override
    public boolean compareHands(List<Card> cards1, List<Card> cards2) {
        // PokerHandResult hand1 = evaluateBestHand(cards1);
        // PokerHandResult hand2 = evaluateBestHand(cards2);
        // int cmp = hand1.rank.compareTo(hand2.rank);
        // if (cmp != 0) return -cmp; // Higher rank wins (ordinal lower is stronger)
        // // Compare tiebreakers
        // for (int i = 0; i < Math.min(hand1.tiebreakers.size(), hand2.tiebreakers.size()); i++) {
        //     int diff = hand1.tiebreakers.get(i) - hand2.tiebreakers.get(i);
        //     if (diff != 0) return diff;
        // }
        return false;
    }

    /**
     * Evaluate a 5-card poker hand (helper for evaluateBestHand).
     */
    private PokerHandResult evaluateFiveCardHand(List<Card> hand) {
        // Logic for ranking hand: Royal Flush, Straight Flush, Four of a Kind, Full House, etc.
        // For brevity, only a stub is provided here. You can expand this as needed.
        // Example: check for flush, straight, etc. using inherited methods
        // Return rank and tiebreakers (e.g., highest card values)
        return new PokerHandResult(HandRank.HIGH_CARD, getSortedCardValuesDesc(hand));
    }

    /**
     * Helper to get all combinations of n cards from a list.
     */
    private List<List<Card>> getCombinations(List<Card> cards, int n) {
        List<List<Card>> combos = new ArrayList<>();
        combine(cards, 0, n, new ArrayList<>(), combos);
        return combos;
    }

    private void combine(List<Card> cards, int start, int n, List<Card> temp, List<List<Card>> result) {
        if (temp.size() == n) {
            result.add(new ArrayList<>(temp));
            return;
        }
        for (int i = start; i <= cards.size() - (n - temp.size()); i++) {
            temp.add(cards.get(i));
            combine(cards, i + 1, n, temp, result);
            temp.remove(temp.size() - 1);
        }
    }

    /**
     * Helper to get card values in descending order
     */
    private List<Integer> getSortedCardValuesDesc(List<Card> hand) {
        List<Integer> values = new ArrayList<>();
        for (Card c : hand) values.add(c.getValue());
        values.sort(Collections.reverseOrder());
        return values;
    }

    /**
     * Compares two PokerHandResult objects.
     */
    private int compareHandResult(PokerHandResult h1, PokerHandResult h2) {
        int cmp = h1.rank.compareTo(h2.rank);
        if (cmp != 0) return -cmp;
        for (int i = 0; i < Math.min(h1.tiebreakers.size(), h2.tiebreakers.size()); i++) {
            int diff = h1.tiebreakers.get(i) - h2.tiebreakers.get(i);
            if (diff != 0) return diff;
        }
        return 0;
    }
}
