package com.cardgame.core;

/**
 * Interface representing a card game
 */
public interface Game {
    /**
     * Starts the game
     */
    void start();
    
    /**
     * Plays a round of the game
     */
    void playRound();
    
    /**
     * Checks if the game is over
     * 
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver();
    
    /**
     * Ends the game and performs any necessary cleanup
     */
    void endGame();
    
    /**
     * Resets the game for a new round
     */
    void reset();
}