package com.cardgame.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of playing cards
 */
public class Deck {
    private List<Card> cards;
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final int[] VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};

    /**
     * Creates a new deck of cards and shuffles it
     */
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes the deck with all 52 cards
     */
    private void initializeDeck() {
        for (String suit : SUITS) {
            for (int i = 0; i < RANKS.length; i++) {
                cards.add(new Card(suit, RANKS[i], VALUES[i]));
            }
        }
    }

    /**
     * Shuffles the deck
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the top of the deck
     * If the deck is empty, a new deck is created and shuffled
     * 
     * @return The drawn card
     */
    public Card draw() {
        if (cards.isEmpty()) {
            System.out.println("Out of cards! Shuffling a new deck...");
            initializeDeck();
            shuffle();
        }
        return cards.remove(0);
    }

    /**
     * Gets the number of cards remaining in the deck
     * 
     * @return The number of cards remaining
     */
    public int remainingCards() {
        return cards.size();
    }
}