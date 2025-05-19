package com.cardgame.core;

import java.util.Scanner;

/**
 * Abstract base class for card games that implements common functionality
 */
public abstract class AbstractGame implements Game {
    protected Deck deck;
    protected Player player;
    protected Player dealer;
    protected boolean gameOver;
    protected Scanner scanner;
    protected ScoringStrategy scoringStrategy;

    /**
     * Creates a new game with the specified scoring strategy
     * 
     * @param scoringStrategy The strategy to use for scoring hands
     */
    public AbstractGame(ScoringStrategy scoringStrategy) {
        this.deck = new Deck();
        this.scanner = new Scanner(System.in);
        this.gameOver = false;
        this.scoringStrategy = scoringStrategy;
    }

    /**
     * Shuffles the deck if the number of remaining cards is less than the specified minimum
     * 
     * @param minCards The minimum number of cards required
     */
    protected void shuffleIfNeeded(int minCards) {
        if (deck.remainingCards() < minCards) {
            System.out.println("Shuffling deck...");
            deck = new Deck();
        }
    }
    
    /**
     * Closes resources used by the game
     */
    protected void closeResources() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
