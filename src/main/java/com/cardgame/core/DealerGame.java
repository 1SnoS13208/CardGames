package com.cardgame.core;

import java.util.Scanner;

/**
 * Abstract base class for dealer-based card games where a player competes against a dealer.
 * This class provides common functionality for games like Blackjack and Poker
 * where scoring is based on hand evaluation and there is typically a house/dealer.
 * 
 * Games that extend this class benefit from:
 * - Player vs dealer structure
 * - Scoring strategy pattern for hand evaluation
 * - Common deck management
 * - Basic game flow control
 */
public abstract class DealerGame implements Game {
    protected Deck deck;
    protected ChipPlayer player;
    protected Player dealer;
    protected boolean gameOver;
    protected Scanner scanner;
    protected ScoringStrategy scoringStrategy;

    /**
     * Creates a new dealer-based game with the specified scoring strategy
     * 
     * @param scoringStrategy The strategy to use for scoring and comparing hands
     */
    public DealerGame(ScoringStrategy scoringStrategy) {
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
