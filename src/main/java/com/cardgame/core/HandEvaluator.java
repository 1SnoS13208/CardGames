package com.cardgame.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Generic abstract class for evaluating and comparing card combinations (hands) in card games.
 * <p>
 * Subclasses should define how to determine the strongest combination from a list of cards (e.g., best 5 out of 7 in Texas Hold'em),
 * and provide a way to compare two hands according to game-specific rules (Poker, Tien Len, etc).
 * <p>
 * The return type of evaluateBestHand can be an enum or class representing the hand type and tie-breaker information.
 */
public abstract class HandEvaluator {
    /**
     * Checks if the hand contains a pair (two cards of the same rank).
     *
     * @param cards The list of cards to check
     * @return true if the hand contains exactly one pair, false otherwise
     */
    public boolean isPair(List<Card> cards) {
        return getMaxOfAKind(cards) == 2;
    }

    /**
     * Checks if the hand contains three of a kind (three cards of the same rank).
     *
     * @param cards The list of cards to check
     * @return true if the hand contains exactly three of a kind, false otherwise
     */
    public boolean isThreeOfAKind(List<Card> cards) {
        return getMaxOfAKind(cards) == 3;
    }

    /**
     * Checks if the hand contains four of a kind (four cards of the same rank).
     *
     * @param cards The list of cards to check
     * @return true if the hand contains exactly four of a kind, false otherwise
     */
    public boolean isFourOfAKind(List<Card> cards) {
        return getMaxOfAKind(cards) == 4;
    }

    /**
     * Checks if the hand is a straight (consecutive values, e.g. 5-6-7-8-9).
     * Supports both Ace-high and Ace-low straights.
     *
     * @param cards The list of cards to check
     * @return true if the hand is a straight, false otherwise
     */
    public boolean isStraight(List<Card> cards) {
        if (cards.size() < 2) return false;
        List<Integer> values = new ArrayList<>();
        for (Card card : cards) {
            values.add(card.getValue());
        }
        Collections.sort(values);
        // Check normal straight
        boolean normal = true;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) != values.get(i-1) + 1) {
                normal = false;
                break;
            }
        }
        // Check Ace-low straight (A-2-3-4-5)
        boolean aceLow = false;
        if (values.contains(14)) { // Ace as 14
            List<Integer> aceLowList = new ArrayList<>(values);
            aceLowList.remove((Integer)14);
            aceLowList.add(0, 1); // Ace as 1
            Collections.sort(aceLowList);
            aceLow = true;
            for (int i = 1; i < aceLowList.size(); i++) {
                if (aceLowList.get(i) != aceLowList.get(i-1) + 1) {
                    aceLow = false;
                    break;
                }
            }
        }
        return normal || aceLow;
    }

    /**
     * Checks if the hand is a flush (all cards have the same suit).
     *
     * @param cards The list of cards to check
     * @return true if all cards have the same suit, false otherwise
     */
    public boolean isFlush(List<Card> cards) {
        if (cards.isEmpty()) return false;
        String suit = cards.get(0).getSuit();
        for (Card card : cards) {
            if (!card.getSuit().equals(suit)) return false;
        }
        return true;
    }

    /**
     * Utility: Finds the maximum number of cards with the same rank in the hand.
     *
     * @param cards The list of cards to check
     * @return The highest count of any rank in the hand
     */
    protected int getMaxOfAKind(List<Card> cards) {
        Map<String, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            rankCount.put(card.getRank(), rankCount.getOrDefault(card.getRank(), 0) + 1);
        }
        int max = 0;
        for (int count : rankCount.values()) {
            if (count > max) max = count;
        }
        return max;
    }

    /**
     * Evaluates the strongest possible hand from a list of cards according to the game rules.
     * For example, in Texas Hold'em, this would select the best 5-card combination from 7 cards.
     *
     * @param cards The list of cards to evaluate
     * @return An object representing the hand strength (e.g., enum or class with rank and kicker info)
     */
    public abstract Object evaluateBestHand(List<Card> cards);

    /**
     * Compares two hands (using the result of evaluateBestHand) to determine which is stronger.
     *
     * @param cards1 The first hand (list of cards)
     * @param cards2 The second hand (list of cards)
     * @return >0 if hand1 is stronger, <0 if hand2 is stronger
     */
    public abstract boolean compareHands(List<Card> cards1, List<Card> cards2);
}


