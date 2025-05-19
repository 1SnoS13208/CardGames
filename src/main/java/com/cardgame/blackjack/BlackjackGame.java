package com.cardgame.blackjack;

import com.cardgame.core.AbstractGame;
import com.cardgame.core.Card;
import java.util.List;

/**
 * Implementation of the Blackjack card game
 */
public class BlackjackGame extends AbstractGame {

    /**
     * Initializes a new Blackjack game
     */
    public BlackjackGame() {
        super(new BlackjackScoringStrategy());
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
                shuffleIfNeeded(20);
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
        BlackjackPlayer bjPlayer = (BlackjackPlayer) player;
        if (hasBlackjack(bjPlayer.getHand())) {
            System.out.println("Blackjack! You win!");
            bjPlayer.addChips(15); // 3:2 payout for blackjack
            return;
        }
        
        // Player's turn
        playerTurn();
        
        // Dealer's turn if player didn't bust
        if (!scoringStrategy.isBusted(player.getHand())) {
            dealerTurn();
        }
        
        // Show result
        showResult();
    }

    private boolean hasBlackjack(List<Card> hand) {
        return ((BlackjackScoringStrategy) scoringStrategy).isBlackjack(hand);
    }

    private void playerTurn() {
        BlackjackPlayer bjPlayer = (BlackjackPlayer) player;
        
        while (true) {
            System.out.println("\nYour current score: " + scoringStrategy.calculateScore(player.getHand()));
            System.out.print("Hit or stand? (h/s): ");
            String choice = scanner.nextLine().toLowerCase();
            
            if (!choice.startsWith("h")) {
                break;
            }
            
            player.addCard(deck.draw());
            System.out.println("You drew: " + player.getHand().get(player.getHand().size() - 1));
            System.out.println(player);
            
            if (scoringStrategy.isBusted(player.getHand())) {
                System.out.println("Bust! You went over 21!");
                break;
            }
        }
    }

    private void dealerTurn() {
        System.out.println("\nDealer's turn...");
        System.out.println(dealer);
        
        while (scoringStrategy.calculateScore(dealer.getHand()) < 17) {
            Card card = deck.draw();
            dealer.addCard(card);
            System.out.println("Dealer draws: " + card);
            System.out.println(dealer);
            
            if (scoringStrategy.isBusted(dealer.getHand())) {
                System.out.println("Dealer busts! You win!");
                ((BlackjackPlayer) player).addChips(10); // Standard win
                return;
            }
        }
    }

    private void showResult() {
        BlackjackPlayer bjPlayer = (BlackjackPlayer) player;
        
        int playerScore = scoringStrategy.calculateScore(player.getHand());
        int dealerScore = scoringStrategy.calculateScore(dealer.getHand());
        
        System.out.println("\n--- Final Result ---");
        System.out.println("You: " + playerScore + " points - " + player.getHand());
        System.out.println("Dealer: " + dealerScore + " points - " + dealer.getHand());
        
        if (scoringStrategy.isBusted(player.getHand())) {
            System.out.println("You busted! Dealer wins!");
            bjPlayer.removeChips(10);
        } else if (scoringStrategy.isBusted(dealer.getHand())) {
            System.out.println("Dealer busted! You win!");
            bjPlayer.addChips(10);
        } else {
            int comparison = scoringStrategy.compareHands(player.getHand(), dealer.getHand());
            if (comparison > 0) {
                System.out.println("You win!");
                bjPlayer.addChips(10);
            } else if (comparison < 0) {
                System.out.println("Dealer wins!");
                bjPlayer.removeChips(10);
            } else {
                System.out.println("It's a tie!");
                // No chips exchanged on a tie
            }
        }
        
        System.out.println("Your chips: " + bjPlayer.getChips());
    }

    @Override
    public boolean isGameOver() {
        return gameOver || ((BlackjackPlayer) player).getChips() <= 0;
    }

    @Override
    public void endGame() {
        System.out.println("\nThank you for playing Blackjack!");
        System.out.println("Final chip count: " + ((BlackjackPlayer) player).getChips());
        closeResources();
    }

    @Override
    public void reset() {
        player.clearHand();
        dealer.clearHand();
    }
}