package com.cardgame.blackjack;

import com.cardgame.core.Game;
import com.cardgame.core.Deck;
import com.cardgame.core.Card;
import java.util.List;
import java.util.Scanner;

public class BlackjackGame extends Game {
    private Scanner scanner;
    private boolean gameOver;
    private BlackjackPlayer player;
    private BlackjackPlayer dealer;
    private Deck deck;

    public BlackjackGame() {
        this.deck = new Deck();
        this.scanner = new Scanner(System.in);
        this.gameOver = false;
    }

    @Override
    public void start() {
        System.out.println("Welcome to Blackjack!");
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();
        
        player = new BlackjackPlayer(playerName, false);
        dealer = new BlackjackPlayer("Dealer", true);
        
        while (!gameOver) {
            playRound();
            
            System.out.print("\nDo you want to play again? (y/n): ");
            String choice = scanner.nextLine().toLowerCase();
            if (!choice.startsWith("y")) {
                gameOver = true;
            } else {
                reset();
                
                if (deck.remainingCards() < 20) {
                    System.out.println("Shuffling deck...");
                    deck = new Deck();
                }
            }
        }
        endGame();
    }

    @Override
    public void playRound() {
        System.out.println("\n--- New Round ---");
        
        // Deal initial cards
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        
        // Show cards
        System.out.println("\n" + player);
        System.out.println(dealer.getHand().get(0) + " and [??]");
        
        // Check for blackjack
        if (player.hasBlackjack()) {
            System.out.println("Blackjack! You win!");
            player.addChips(15); // 3:2 payout for blackjack
            return;
        }
        
        // Player's turn
        playerTurn();
        
        // Dealer's turn if player didn't bust
        if (!player.isBusted()) {
            dealerTurn();
        }
        
        // Show result
        showResult();
    }

    private void playerTurn() {
        while (true) {
            System.out.println("\nYour current score: " + player.getScore());
            System.out.print("Hit or stand? (h/s): ");
            String choice = scanner.nextLine().toLowerCase();
            
            if (!choice.startsWith("h")) {
                break;
            }
            
            player.addCard(deck.draw());
            System.out.println("You drew: " + player.getHand().get(player.getHand().size() - 1));
            System.out.println(player);
            
            if (player.isBusted()) {
                System.out.println("Bust! You went over 21!");
                break;
            }
        }
    }

    private void dealerTurn() {
        System.out.println("\nDealer's turn...");
        System.out.println(dealer);
        
        while (dealer.getScore() < 17) {
            Card card = deck.draw();
            dealer.addCard(card);
            System.out.println("Dealer draws: " + card);
            System.out.println(dealer);
            
            if (dealer.isBusted()) {
                System.out.println("Dealer busts! You win!");
                player.addChips(10); // Standard win
                return;
            }
        }
    }

    private void showResult() {
        System.out.println("\n--- Final Result ---");
        System.out.println("You: " + player.getScore() + " points - " + player.getHand());
        System.out.println("Dealer: " + dealer.getScore() + " points - " + dealer.getHand());
        
        if (player.isBusted()) {
            System.out.println("You busted! Dealer wins!");
            player.removeChips(10);
        } else if (dealer.isBusted()) {
            System.out.println("Dealer busted! You win!");
            player.addChips(10);
        } else if (player.getScore() > dealer.getScore()) {
            System.out.println("You win!");
            player.addChips(10);
        } else if (player.getScore() < dealer.getScore()) {
            System.out.println("Dealer wins!");
            player.removeChips(10);
        } else {
            System.out.println("It's a tie!");
            // No chips exchanged on a tie
        }
        
        System.out.println("Your chips: " + player.getChips());
    }

    @Override
    public boolean isGameOver() {
        return gameOver || player.getChips() <= 0;
    }

    @Override
    public void endGame() {
        System.out.println("\nThank you for playing Blackjack!");
        System.out.println("Final chip count: " + player.getChips());
        scanner.close();
    }

    @Override
    public void reset() {
        player.clearHand();
        dealer.clearHand();
        gameOver = false;
    }
    
    @Override
    protected int calculateScore(List<Card> hand) {
        int score = 0;
        int aces = 0;
        
        // First pass: count all cards, treating Aces as 11
        for (Card card : hand) {
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
        
        return score;
    }
    
    @Override
    protected int compareHands(List<Card> hand1, List<Card> hand2) {
        int score1 = calculateScore(hand1);
        int score2 = calculateScore(hand2);
        
        if (score1 > 21) return -1; // hand1 is busted
        if (score2 > 21) return 1;  // hand2 is busted
        return Integer.compare(score1, score2);
    }
    
    @Override
    protected boolean isBusted(List<Card> hand) {
        return calculateScore(hand) > 21;
    }
}