package com.cardgame;

import com.cardgame.blackjack.BlackjackGame;
import com.cardgame.core.Game;
import java.util.Scanner;

/**
 * Main entry point for the Card Game application
 */
public class Main {
    /**
     * Main method that starts the application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Card Game!");
        System.out.println("Please select a game:");
        System.out.println("1. Blackjack");
        System.out.println("2. Poker (Coming soon)");
        
        Scanner scanner = new Scanner(System.in);
        int choice = 1; // Default to Blackjack
        
        try {
            System.out.print("Enter your choice (1-2): ");
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > 2) {
                System.out.println("Invalid choice. Defaulting to Blackjack.");
                choice = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to Blackjack.");
        }
        
        Game game;
        if (choice == 2) {
            System.out.println("Poker is coming soon! Defaulting to Blackjack.");
            game = new BlackjackGame();
        } else {
            game = new BlackjackGame();
        }
        
        game.start();
        scanner.close();
    }
}