package com.cardgame.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a player in a card game
 */
public class Player {
    protected String name;
    protected List<Card> hand;

    /**
     * Creates a new player with the given name
     * 
     * @param name The player's name
     * @throws IllegalArgumentException if name is null or empty
     */
    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Adds a card to the player's hand
     * 
     * @param card The card to add
     * @throws IllegalArgumentException if card is null
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.add(card);
    }

    /**
     * Clears all cards from the player's hand
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Gets the player's name
     * 
     * @return The player's name
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Gets a copy of the player's hand
     * Returns a defensive copy to prevent modification
     * 
     * @return A copy of the player's hand
     */
    public List<Card> getHand() { 
        return Collections.unmodifiableList(new ArrayList<>(hand)); 
    }

    /**
     * Sắp xếp hand mặc định theo giá trị tăng dần, sau đó là chất (Hearts > Diamonds > Clubs > Spades)
     */
    public void sortHand() {
        if (hand == null || hand.isEmpty()) return;
        hand.sort((Card a, Card b) -> {
            int valueCompare = Integer.compare(a.getValue(), b.getValue());
            if (valueCompare != 0) return valueCompare;
            int suitA = a.getSuitOrder();
            int suitB = b.getSuitOrder();
            return Integer.compare(suitA, suitB);
        });
    }

    /**
     * Sắp xếp hand với comparator tuỳ ý (dùng cho các game khác nhau)
     */
    public void sortHand(Comparator<Card> comparator) {
        if (hand == null || hand.isEmpty() || comparator == null) return;
        hand.sort(comparator);
    }

    /**
     * Returns a string representation of the player
     * 
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        return name + ": " + hand;
    }
}