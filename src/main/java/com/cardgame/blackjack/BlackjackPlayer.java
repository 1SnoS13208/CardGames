package com.cardgame.blackjack;

import com.cardgame.core.Player;
import com.cardgame.core.Card;

public class BlackjackPlayer extends Player {
    private boolean isDealer;
    private int score;
    private int chips;
    private int aces;

    public BlackjackPlayer(String name, boolean isDealer) {
        super(name);
        this.isDealer = isDealer;
        this.score = 0;
        this.chips = isDealer ? 0 : 1000; // Dealer starts with 0 chips
        this.aces = 0;
    }

    
    @Override
    public void addCard(Card card) {
        super.addCard(card);
        updateScore();
    }
    
    @Override
    public void clearHand() {
        super.clearHand();
        score = 0;
        aces = 0;
    }

    private void updateScore() {
        score = 0;
        aces = 0;
        
        // First pass: count all cards, treating Aces as 11
        for (Card card : getHand()) {
            score += card.getValue();
            if (card.getRank().equals("A")) {
                aces++;
            }
        }

        // Adjust for Aces if needed
        while (score > 21 && aces > 0) {
            score -= 10; // Change Ace value from 11 to 1
            aces--;
        }
    }


    public boolean isBusted() {
        return score > 21;
    }

    public boolean hasBlackjack() {
        return getHand().size() == 2 && score == 21;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getChips() { 
        return chips; 
    }
    
    public void addChips(int amount) { 
        chips += amount; 
    }
    
    public boolean removeChips(int amount) { 
        if (chips >= amount) {
            chips -= amount;
            return true;
        }
        return false;
    }

    public boolean isDealer() {
        return isDealer;
    }

    @Override
    public String toString() {
        if (isDealer) {
            return "Dealer's cards: " + getHand() + " (Score: " + score + ")";
        }
        return super.getName() + " (Chips: " + chips + ", Score: " + score + "): " + getHand();
    }
}