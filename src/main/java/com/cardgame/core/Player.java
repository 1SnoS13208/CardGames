package com.cardgame.core;

import java.util.ArrayList;
import java.util.List;

public class Player {
    protected String name;
    protected List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    // Getter methods
    public String getName() { 
        return name; 
    }
    public List<Card> getHand() { 
        return new ArrayList<>(hand); 
    }

    @Override
    public String toString() {
        return name + ": " + hand;
    }
}