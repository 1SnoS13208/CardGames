package com.cardgame.blackjack;

import com.cardgame.core.DealerGame;
import com.cardgame.core.ChipPlayer;
import com.cardgame.core.Player;
import com.cardgame.core.Card;
import java.util.List;

/**
 * Implementation of the Blackjack card game, refactored for GUI usage
 */
public class BlackjackGame extends DealerGame {
    private String resultMessage = "";
    private boolean playerStood = false;
    protected int bet = 0;
    protected int earnAmount = 0;    

    public BlackjackGame() {
        super(new BlackjackScoringStrategy());
        player = new ChipPlayer("Player", 1000);
        dealer = new Player("Dealer");
        reset();
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getEarnAmount() {
        return earnAmount;
    }

    // --- PUBLIC METHODS FOR GUI CONTROLLER ---
    public List<Card> getDealerCards() {
        return dealer.getHand();
    }
    public List<Card> getPlayerCards() {
        return player.getHand();
    }
    public void newRound(int betAmount) {
        this.bet = betAmount;
        player.removeChips(betAmount); 
        reset();
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        player.addCard(deck.draw());
        resultMessage = "";
        playerStood = false;
    }
    public void playerHit() {
        if (!playerStood && !isPlayerBust() && !isPlayerBlackjack()) {
            player.addCard(deck.draw());
            if (isPlayerBust() || isPlayerBlackjack()) {
                playerStand();
            }
        }
    }
    public void playerStand() {
        playerStood = true;
    }
    public boolean isPlayerBust() {
        return scoringStrategy.isBusted(player.getHand());
    }
    public boolean isPlayerBlackjack() {
        return hasBlackjack(player.getHand());
    }
    public boolean isPlayerWin() {
        return resultMessage.toLowerCase().contains("you win");
    }
    public String getResultMessage() {
        return resultMessage;
    }
    public int getPlayerScore() {
        return scoringStrategy.calculateScore(player.getHand());
    }
    public int getDealerScore() {
        return scoringStrategy.calculateScore(dealer.getHand());
    }

    // --- END PUBLIC METHODS ---

    private boolean hasBlackjack(List<Card> hand) {
        return ((BlackjackScoringStrategy) scoringStrategy).isBlackjack(hand);
    }

    public void dealerTurn() {
        while (scoringStrategy.calculateScore(dealer.getHand()) < 17) {
            Card card = deck.draw();
            dealer.addCard(card);
            if (scoringStrategy.isBusted(dealer.getHand())) {
                break;
            }
        }
        showResult(); 
    }

    private void showResult() {    
        boolean playerBJ = isPlayerBlackjack();
        boolean dealerBJ = hasBlackjack(dealer.getHand());
        if (playerBJ) {
            if (dealerBJ) {
                resultMessage = "Both have Blackjack! It's a tie!";
                earnAmount = bet;
            } else {
                resultMessage = "Blackjack! You win!";
                earnAmount = bet * 2;
            }
        } else if (dealerBJ) {
            resultMessage = "Dealer has Blackjack! Dealer wins!";
            earnAmount = 0;
        } else if (scoringStrategy.isBusted(player.getHand())) {
            resultMessage = "You busted! Dealer wins!";
            earnAmount = 0;
        } else if (scoringStrategy.isBusted(dealer.getHand())) {
            resultMessage = "Dealer busted! You win!";
            earnAmount = (int)(bet * 1.5);
        } else {
            int comparison = scoringStrategy.compareHands(player.getHand(), dealer.getHand());
            if (comparison > 0) {
                resultMessage = "You win!";
                earnAmount = (int)(bet * 1.5);
            } else if (comparison < 0) {
                resultMessage = "Dealer wins!";
                earnAmount = 0;
            } else {
                resultMessage = "It's a tie! Your bet is returned.";
                earnAmount = bet;
            }
        }
        System.out.println("Earn amount: " + earnAmount);
        if (earnAmount > 0) {
            System.out.println("Player chips before: " + player.getChips());
            player.addChips(earnAmount);
            System.out.println("Player chips after: " + player.getChips());
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver || player.getChips() <= 0;
    }

    @Override
    public void endGame() {
    }

    public ChipPlayer getPlayer() {
        return player;
    }

    @Override
    public void reset() {
        player.clearHand();
        dealer.clearHand();
        deck.shuffle();
        resultMessage = "";
        playerStood = false;
    }

    @Override
    public void playRound() {
    }

    @Override
    public void start() {
        newRound(this.bet);
    }
}

