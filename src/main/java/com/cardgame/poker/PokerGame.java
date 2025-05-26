package com.cardgame.poker;

import com.cardgame.core.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp đại diện cho một ván bài Poker Texas Hold'em.
 * Quản lý luồng chơi, vòng cược và xử lý người chơi.
 */
/**
 * Implementation of a Texas Hold'em Poker game.
 * Manages the game flow, betting rounds, and player actions.
 */
public class PokerGame extends MultiplayerGame<PokerPlayer> {
    private static final int SMALL_BLIND = 10;
    private static final int BIG_BLIND = 20;
    private static final int MAX_PLAYERS = 5;
    
    private final List<Card> communityCards;
    private int pot;
    private int currentBet;
    private int dealerPosition;
    private final PokerHandEvaluator evaluator;
    private int lastRaisePosition;
    private GameState gameState;
    
    /**
     * Khởi tạo một ván Poker mới.
     */
    /**
     * Possible game states during a poker round
     */
    private enum GameState {
        PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN, GAME_OVER
    }

    public PokerGame() {
        super();
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = 0;
        this.dealerPosition = 0;
        this.lastRaisePosition = -1;
        this.evaluator = new PokerHandEvaluator();
        this.deck = new Deck();
        this.gameState = GameState.PRE_FLOP;
    }
    
    @Override
    public void start() {
        if (players.size() < 2) {
            throw new IllegalStateException("At least 2 players are required to start");
        }
        
        // Reset game state
        resetGame();
        
        // Shuffle the deck
        deck.shuffle();
        
        // Post blinds
        postBlinds();
        
        // Deal hole cards
        dealHoleCards();
        
        // Start pre-flop betting round
        startBettingRound();
        
        // If we still have active players after pre-flop, continue to flop
        if (countActivePlayers() > 1) {
            gameState = GameState.FLOP;
            dealCommunityCards(3);
            System.out.println("\n--- Flop ---");
            showCommunityCards();
            startBettingRound();
        }
        
        // Continue to turn if we still have active players
        if (countActivePlayers() > 1) {
            gameState = GameState.TURN;
            dealCommunityCards(1);
            System.out.println("\n--- Turn ---");
            showCommunityCards();
            startBettingRound();
        }
        
        // Continue to river if we still have active players
        if (countActivePlayers() > 1) {
            gameState = GameState.RIVER;
            dealCommunityCards(1);
            System.out.println("\n--- River ---");
            showCommunityCards();
            startBettingRound();
        }
        
        // End the round (will handle showdown if needed)
        endRound();
    }
    
    /**
     * Advances the game to the next round if possible
     */
    private void nextRound() {
        // This method is now a simple state transition
        // The actual game flow is now handled in the start() method
        // to make the flow more linear and easier to follow
    }
    
    /**
     * Deals hole cards to all active players (2 cards each)
     */
    private void dealHoleCards() {
        // Clear any existing cards from players' hands
        for (PokerPlayer player : players) {
            player.clearHand();
        }
        
        // Deal two cards to each player in turn
        for (int i = 0; i < 2; i++) {
            for (PokerPlayer player : players) {
                if (player.getChips() > 0) {  // Only deal to players with chips
                    player.addCard(deck.draw());
                }
            }
        }
    }
    
    private void postBlinds() {
        // Người chơi sau dealer đặt small blind
        int smallBlindPos = (dealerPosition + 1) % players.size();
        PokerPlayer smallBlindPlayer = players.get(smallBlindPos);
        smallBlindPlayer.bet(SMALL_BLIND);
        pot += SMALL_BLIND;
        
        // Người chơi tiếp theo đặt big blind
        int bigBlindPos = (dealerPosition + 2) % players.size();
        PokerPlayer bigBlindPlayer = players.get(bigBlindPos);
        bigBlindPlayer.bet(BIG_BLIND);
        pot += BIG_BLIND;
        
        currentBet = BIG_BLIND;
        currentPlayerIndex = (bigBlindPos + 1) % players.size();
    }
    

    
    /**
     * Chia bài chung
     * @param count Số lá bài cần chia
     */
    private void dealCommunityCards(int count) {
        for (int i = 0; i < count; i++) {
            communityCards.add(deck.draw());
        }
    }
    
    /**
     * Starts a new betting round
     */
    private void startBettingRound() {
        currentBet = 0;
        lastRaisePosition = -1;
        currentBet = 0;
        
        // Reset current bet for all players
        players.forEach(PokerPlayer::resetBet);
        
        // Start with the player after the big blind (or dealer if pre-flop)
        currentPlayerIndex = (dealerPosition + 3) % players.size();
    }
    
    /**
     * Checks if the current betting round is complete
     */
    private boolean isBettingRoundComplete() {
        // If only one player remains, round is complete
        if (countActivePlayers() <= 1) {
            return true;
        }
        
        // If we've gone full circle without a raise, round is complete
        if (currentPlayerIndex == (dealerPosition + 3) % players.size() && 
            lastRaisePosition == -1) {
            return true;
        }
        
        // If we've reached the last raiser, round is complete
        if (currentPlayerIndex == lastRaisePosition) {
            return true;
        }
        
        return false;
    }
    
    private int countActivePlayers() {
        return (int) players.stream()
                .filter(p -> !p.hasFolded() && p.getChips() > 0)
                .count();
    }
    
    /**
     * Processes a player's action
     */
    private void processPlayerAction(PokerPlayer player, PlayerAction action) {
        switch (action.getType()) {
            case FOLD:
                player.fold();
                break;
                
            case CALL:
                int callAmount = Math.min(currentBet - player.getCurrentBet(), player.getChips());
                if (callAmount > 0) {
                    player.bet(callAmount);
                    pot += callAmount;
                }
                break;
                
            case RAISE:
                int raiseAmount = action.getAmount();
                if (raiseAmount <= currentBet) {
                    throw new IllegalArgumentException("Raise amount must be greater than current bet");
                }
                if (raiseAmount > player.getChips() + player.getCurrentBet()) {
                    throw new IllegalArgumentException("Not enough chips to raise");
                }
                
                // Process the call portion first
                int callPortion = currentBet - player.getCurrentBet();
                if (callPortion > 0) {
                    player.bet(callPortion);
                    pot += callPortion;
                }
                
                // Then process the raise
                int actualRaise = raiseAmount - currentBet;
                player.bet(actualRaise);
                pot += actualRaise;
                currentBet = raiseAmount;
                lastRaisePosition = currentPlayerIndex;
                break;
                
            case ALL_IN:
                int allInAmount = player.getChips();
                player.bet(allInAmount);
                pot += allInAmount;
                System.out.println(player.getName() + " goes all in with " + allInAmount + " chips");
                break;
                
            case CHECK:
                if (player.getCurrentBet() < currentBet) {
                    throw new IllegalStateException("Cannot check when facing a bet");
                }
                break;
                
        }
    }
    
    @Override
    public void addPlayer(PokerPlayer player) {
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Maximum number of players reached");
        }
        super.addPlayer(player);
    }
    
    @Override
    public void endGame() {
        // Implement end game logic
    }
    
    @Override
    public void playRound() {
        if (isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
        
        // Process current player's action
        PokerPlayer currentPlayer = getCurrentPlayer();
        if (currentPlayer != null && !currentPlayer.hasFolded() && currentPlayer.getChips() > 0) {
            PlayerAction action = currentPlayer.getAction(this);
            processPlayerAction(currentPlayer, action);
            
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            
            // Check if betting round is complete
            if (isBettingRoundComplete()) {
                nextRound();
            }
        }
    }
    
    @Override
    public boolean isGameOver() {
        return gameOver;
    }
    
    @Override
    public void reset() {
        // Reset game state for a new round
        resetGame();
    }
    
    /**
     * Handles the end of a poker round
     */
    private void showCommunityCards() {
        System.out.print("Community cards: ");
        if (communityCards.isEmpty()) {
            System.out.println("[]");
            return;
        }
        
        // Always show all community cards that have been dealt so far
        System.out.println(communityCards);
    }
    
    private String getHandDescription(List<Card> hand) {
        // Combine player's hole cards with community cards
        List<Card> allCards = new ArrayList<>(hand);
        allCards.addAll(communityCards);
        
        // Evaluate the best 5-card hand
        PokerHandEvaluator.PokerHandResult result = evaluator.evaluateBestHand(allCards);
        return result.rank.toString().replace('_', ' ');
    }
    
    private void endRound() {
        // Show final community cards if we reached showdown
        if (gameState == GameState.RIVER || gameState == GameState.SHOWDOWN) {
            System.out.println("\n--- Final Board ---");
            showCommunityCards();
        }
        
        // Determine the winner
        PokerPlayer winner = determineWinner();
        if (winner != null) {
            winner.addChips(pot);
            System.out.println("\n--- Round Over ---");
            System.out.println(winner.getName() + " wins " + pot + " chips!");
            
            // Show all hands in showdown
            if (countActivePlayers() > 1) {
                System.out.println("\n--- Showdown ---");
                for (PokerPlayer player : players) {
                    if (!player.hasFolded() && player.getChips() > 0) {
                        System.out.println(player.getName() + " shows: " + player.getHand() + 
                                       " (" + getHandDescription(player.getHand()) + ")");
                    }
                }
            }
        }
        
        // Check if game should continue
        int playersWithChips = (int) players.stream().filter(p -> p.getChips() > 0).count();
        if (playersWithChips < 2) {
            System.out.println("\n--- Game Over ---");
            if (playersWithChips == 1) {
                PokerPlayer champion = players.stream().filter(p -> p.getChips() > 0).findFirst().orElse(null);
                if (champion != null) {
                    System.out.println(champion.getName() + " wins the game with " + champion.getChips() + " chips!");
                }
            }
            gameOver = true;
        }
    }
    
    private PokerPlayer determineWinner() {
        // If only one active player remains, they win
        List<PokerPlayer> activePlayers = players.stream()
                .filter(p -> !p.hasFolded() && p.getChips() > 0)
                .toList();
                
        if (activePlayers.size() == 1) {
            return activePlayers.get(0);
        }
        
        // If we're not in showdown and have no community cards, return null
        if (gameState != GameState.SHOWDOWN && communityCards.isEmpty()) {
            return null;
        }
        
        // Find the best hand among active players
        PokerHandEvaluator.PokerHandResult bestResult = null;
        List<PokerPlayer> winners = new ArrayList<>();
        
        for (PokerPlayer player : activePlayers) {
            List<Card> playerCards = new ArrayList<>(player.getHand());
            playerCards.addAll(communityCards);
            
            try {
                PokerHandEvaluator.PokerHandResult result = evaluator.evaluateBestHand(playerCards);
                
                if (bestResult == null) {
                    // First player's hand becomes the best hand
                    bestResult = result;
                    winners.clear();
                    winners.add(player);
                } else {
                    // Compare with current best hand
                    int compare = evaluator.compareHandResults(result, bestResult);
                    if (compare > 0) {
                        // New best hand found
                        bestResult = result;
                        winners.clear();
                        winners.add(player);
                    } else if (compare == 0) {
                        // Tied with current best hand
                        winners.add(player);
                    }
                }
            } catch (IllegalArgumentException e) {
                // Skip players with invalid hands
                continue;
            }
        }
        
        // Handle split pot if multiple winners
        if (winners.size() > 1) {
            int splitAmount = pot / winners.size();
            for (PokerPlayer winner : winners) {
                winner.addChips(splitAmount);
            }
            System.out.println("\n--- Split Pot! ---");
            System.out.println("Winners (" + splitAmount + " chips each): " + 
                winners.stream().map(Player::getName).collect(Collectors.joining(", ")));
            return null; // Indicate split pot
        }
        
        // Return the single winner
        return winners.isEmpty() ? null : winners.get(0);
    }
    
    // Các phương thức getter cần thiết
    public List<Card> getCommunityCards() {
        return new ArrayList<>(communityCards);
    }
    
    public int getPot() {
        return pot;
    }
    
    public int getCurrentBet() {
        return currentBet;
    }
    
    /**
     * Resets the game state for a new round
     */
    private void resetGame() {
        // Reset game state
        pot = 0;
        currentBet = 0;
        communityCards.clear();
        deck = new Deck();
        deck.shuffle();
        lastRaisePosition = -1;
        
        // Reset player states
        for (PokerPlayer player : players) {
            player.clearHand();
            player.resetBet();
            player.resetFold();
        }
        
        // Move dealer button to next player who has chips
        int nextDealer = (dealerPosition + 1) % players.size();
        int attempts = 0;
        while (players.get(nextDealer).getChips() <= 0 && attempts < players.size()) {
            nextDealer = (nextDealer + 1) % players.size();
            attempts++;
        }
        dealerPosition = nextDealer;
        currentPlayerIndex = (dealerPosition + 1) % players.size();
        
        // Skip players with no chips
        while (currentPlayerIndex < players.size() && players.get(currentPlayerIndex).getChips() <= 0) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0; // Wrap around if needed
            }
        }
        
        // Reset game state
        gameState = GameState.PRE_FLOP;
        gameOver = false;
        
        // Check if we have enough players with chips to continue
        int activePlayers = (int) players.stream().filter(p -> p.getChips() > 0).count();
        if (activePlayers < 2) {
            gameOver = true;
        }
    }
}