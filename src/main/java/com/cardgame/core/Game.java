package com.cardgame.core;

import java.util.List;

public abstract class Game {
    protected Deck deck;
    protected Player player;
    protected Player dealer;
    protected boolean gameOver;

    public abstract void start();
    
    public abstract void playRound();
    
    public abstract boolean isGameOver();
    
    public abstract void endGame();
    
    public abstract void reset();
    
    protected abstract int calculateScore(List<Card> hand);
    
    protected abstract int compareHands(List<Card> hand1, List<Card> hand2);
    
    protected abstract boolean isBusted(List<Card> hand);
    
    protected void shuffleIfNeeded(int minCards) {
        if (deck.remainingCards() < minCards) {
            System.out.println("Shuffling deck...");
            deck = new Deck();
        }
    }
}