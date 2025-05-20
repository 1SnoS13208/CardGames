package com.cardgame.blackjack;

import com.cardgame.core.CasinoStyleGame;
import com.cardgame.core.Card;
import java.util.List;

/**
 * Implementation of the Blackjack card game
 */
public class BlackjackGame extends CasinoStyleGame {
    protected int bet;

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
            }
        }
        endGame();
    }

    @Override
    public void playRound() {
        System.out.println("\n--- New Round ---");
        BlackjackPlayer bjPlayer = (BlackjackPlayer) player;
        System.out.println("Your chips: " + bjPlayer.getChips());
        
        // Get and validate bet
        boolean validBet = false;
        while (!validBet) {
            System.out.print("Enter your bet: ");
            try {
                bet = Integer.parseInt(scanner.nextLine().trim());
                if (bet <= 0) {
                    System.out.println("Bet must be positive.");
                } else if (bet > bjPlayer.getChips()) {
                    System.out.println("You don't have enough chips.");
                } else {
                    validBet = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Ensure we have enough cards
        shuffleIfNeeded(10);
        
        // Deal initial cards
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        
        // Show cards
        System.out.println("\n" + player);
        System.out.println("Dealer shows: " + dealer.getHand().get(0) + " and [Hidden Card]");
        
        // Check for blackjack
        if (hasBlackjack(bjPlayer.getHand())) {
            System.out.println("Blackjack! You win 3:2 on your bet!");
            bjPlayer.addChips((int)(bet * 1.5)); // 3:2 payout for blackjack
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
                ((BlackjackPlayer) player).addChips(bet);
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
            bjPlayer.removeChips(bet);
        } else if (scoringStrategy.isBusted(dealer.getHand())) {
            System.out.println("Dealer busted! You win!");
            bjPlayer.addChips(bet);
        } else {
            int comparison = scoringStrategy.compareHands(player.getHand(), dealer.getHand());
            if (comparison > 0) {
                System.out.println("You win!");
                bjPlayer.addChips(bet);
            } else if (comparison < 0) {
                System.out.println("Dealer wins!");
                bjPlayer.removeChips(bet);
            } else {
                System.out.println("It's a tie! Your bet is returned.");
                // No chips exchanged on a tie
            }
        }
        
        System.out.println("Your chips: " + bjPlayer.getChips());
        
        // Check if player is out of chips
        if (bjPlayer.getChips() <= 0) {
            System.out.println("You're out of chips! Game over.");
            gameOver = true;
        }
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