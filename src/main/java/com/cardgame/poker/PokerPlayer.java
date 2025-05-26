package com.cardgame.poker;

import com.cardgame.core.ChipPlayer;
import java.util.Scanner;

/**
 * Represents a player in the Poker game
 */
/**
 * Represents a player in a poker game
 */
public class PokerPlayer extends ChipPlayer {
    private int currentBet;
    private boolean hasFolded;
    private boolean isAllIn;
    private final Scanner scanner;

    /**
     * Creates a new Poker player
     * 
     * @param name The player's name
     */
    public PokerPlayer(String name) {
        super(name, 1000);
        this.currentBet = 0;
        this.hasFolded = false;
        this.isAllIn = false;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Places a bet
     * 
     * @param amount The amount to bet
     * @return true if the bet was placed successfully, false otherwise
     */
    public boolean bet(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }
        
        if (chips >= amount) {
            chips -= amount;
            currentBet += amount;
            return true;
        }
        return false;
    }
    
    /**
     * Gets the player's current bet
     * 
     * @return The player's current bet
     */
    public int getCurrentBet() {
        return currentBet;
    }
    
    /**
     * Resets the player's current bet to 0
     */
    public void resetBet() {
        currentBet = 0;
    }
    
    /**
     * Marks the player as folded
     */
    public void fold() {
        hasFolded = true;
    }
    
    /**
     * Checks if the player has folded
     * 
     * @return true if the player has folded, false otherwise
     */
    public boolean hasFolded() {
        return hasFolded;
    }
    
    /**
     * Resets the player's folded status
     */
    public void resetFold() {
        hasFolded = false;
    }
    
    /**
     * Clears the player's hand and resets their bet and fold status
     */
    @Override
    public void clearHand() {
        super.clearHand();
        resetBet();
        resetFold();
    }

    /**
     * Returns a string representation of the player
     * 
     * @return A string representation of the player
     */
    /**
     * Gets the player's action for the current game state
     * 
     * @param game The current poker game
     * @return The player's chosen action
     */
    public PlayerAction getAction(PokerGame game) {
        if (isAllIn || hasFolded) {
            return new PlayerAction(ActionType.FOLD);
        }
        
        System.out.println("\n" + getName() + "'s turn");
        System.out.println("Your hand: " + getHand());
        System.out.println("Your chips: " + getChips());
        System.out.println("Current bet: " + game.getCurrentBet());
        System.out.println("Your current bet: " + currentBet);
        System.out.println("Pot: " + game.getPot());
        System.out.println("Community cards: " + game.getCommunityCards());
        
        // If no bet has been made, player can check or bet
        if (game.getCurrentBet() == 0) {
            System.out.println("Options: (1) Check, (2) Bet, (3) Fold");
            int choice = getIntInput("Enter your choice: ", 1, 3);
            
            if (choice == 1) {
                return new PlayerAction(ActionType.CHECK);
            } else if (choice == 2) {
                int maxBet = getChips();
                int betAmount = getIntInput("Enter bet amount (1-" + maxBet + "): ", 1, maxBet);
                return new PlayerAction(ActionType.RAISE, betAmount);
            } else {
                return new PlayerAction(ActionType.FOLD);
            }
        } else {
            // If there's a bet, player can call, raise, or fold
            int callAmount = game.getCurrentBet() - currentBet;
            System.out.println("Options: (1) Call " + callAmount + ", (2) Raise, (3) Fold");
            int choice = getIntInput("Enter your choice: ", 1, 3);
            
            if (choice == 1) {
                if (callAmount >= getChips()) {
                    return new PlayerAction(ActionType.ALL_IN);
                }
                return new PlayerAction(ActionType.CALL);
            } else if (choice == 2) {
                int maxRaise = getChips() - callAmount;
                int raiseTo = getIntInput(
                    "Enter total bet amount (" + (game.getCurrentBet() + 1) + "-" + 
                    (game.getCurrentBet() + maxRaise) + "): ", 
                    game.getCurrentBet() + 1, 
                    game.getCurrentBet() + maxRaise
                );
                return new PlayerAction(ActionType.RAISE, raiseTo);
            } else {
                return new PlayerAction(ActionType.FOLD);
            }
        }
    }
    
    /**
     * Helper method to get integer input from the player
     */
    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    @Override
    public String toString() {
        return getName() + " (Chips: " + getChips() + ", Bet: " + currentBet + 
               (hasFolded ? ", FOLDED" : "") + (isAllIn ? ", ALL-IN" : "") + "): " + getHand();
    }
}
