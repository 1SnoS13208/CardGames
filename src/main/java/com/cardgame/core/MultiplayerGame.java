package com.cardgame.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Abstract base class for multiplayer card games without a dealer
 * This is for games where players compete against each other rather than against a dealer
 */
public abstract class MultiplayerGame<P extends Player> implements Game {
    protected Deck deck;
    protected List<P> players;
    protected boolean gameOver;
    protected Scanner scanner;
    protected int currentPlayerIndex;
    
    /**
     * Creates a new multiplayer game
     */
    public MultiplayerGame() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.gameOver = false;
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Adds a player to the game
     * 
     * @param player The player to add
     */
    public void addPlayer(P player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        players.add(player);
    }
    
    /**
     * Gets the current player
     * 
     * @return The current player
     */
    protected P getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players in the game");
        }
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Moves to the next player
     */
    protected void nextPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players in the game");
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    
    /**
     * Gets the number of players
     * 
     * @return The number of players
     */
    public int getPlayerCount() {
        return players.size();
    }
    
    /**
     * Gets the list of players in the game.
     * @return A list of players.
     */
    public List<P> getPlayers() {
        return new ArrayList<>(players);
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

    public abstract void start();
}
