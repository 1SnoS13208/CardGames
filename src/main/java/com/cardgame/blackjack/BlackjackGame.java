package com.cardgame.blackjack;

import com.cardgame.core.CasinoStyleGame;
import com.cardgame.core.Card;
import java.util.List;

/**
 * Implementation of the Blackjack card game, refactored for GUI usage
 */
public class BlackjackGame extends CasinoStyleGame {
    private String resultMessage = "";
    private boolean playerStood = false;
    protected int bet = 0;
    protected int earnAmount = 0;    

    public BlackjackGame() {
        super(new BlackjackScoringStrategy());
        player = new BlackjackPlayer("Player", false);
        dealer = new BlackjackPlayer("Dealer", true);
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
        ((BlackjackPlayer) player).removeChips(betAmount); 
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
        dealerTurn();
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
        if (earnAmount > 0) {
            ((BlackjackPlayer) player).addChips(earnAmount);
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver || ((BlackjackPlayer) player).getChips() <= 0;
    }

    @Override
    public void endGame() {
        // No-op for GUI
    }

    public BlackjackPlayer getPlayer() {
        return (BlackjackPlayer) player;
    }

    @Override
    public void reset() {
        player.clearHand();
        dealer.clearHand();
        deck.shuffle();
        resultMessage = "";
        playerStood = false;
    }

    // --- BEGIN: Abstract methods for compatibility with CasinoStyleGame/Game ---
    @Override
    public void playRound() {
        // No-op: GUI will control game flow via public methods
    }

    @Override
    public void start() {
        // Khi bắt đầu game, chia 2 lá cho player và dealer (giống newRound)
        newRound(this.bet);
    }
    // --- END: Abstract methods ---
}

