package com.cardgame.poker;

import com.cardgame.core.Card;
import com.cardgame.core.DealerGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Implementation of Texas Hold'em Poker for multiple human players
 */
public class PokerGame extends DealerGame {
    private List<PokerPlayer> players;
    private List<Card> communityCards;
    private int pot;
    private int currentPlayerIndex;
    private int dealerIndex;
    private static final int MIN_BET = 10;
    private static final int MAX_PLAYERS = 8;
    private Scanner scanner;

    /**
     * Initializes a new Poker game
     */
    public PokerGame() {
        super(new PokerScoringStrategy());
        this.players = new ArrayList<>();
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentPlayerIndex = 0;
        this.dealerIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void start() {
        System.out.println("Welcome to Texas Hold'em Poker!");
        
        // Get player names and create players
        setupPlayers();
        
        while (!gameOver) {
            playRound();
            
            System.out.print("\nDo you want to play again? (y/n): ");
            String choice = scanner.nextLine().toLowerCase();
            if (!choice.startsWith("y")) {
                gameOver = true;
            } else {
                reset();
                rotateDealerPosition();
                shuffleIfNeeded(20);
            }
        }
        endGame();
    }

    private void setupPlayers() {
        System.out.print("How many players? (2-" + MAX_PLAYERS + "): ");
        int playerCount;
        try {
            playerCount = Integer.parseInt(scanner.nextLine().trim());
            playerCount = Math.max(2, Math.min(MAX_PLAYERS, playerCount)); // Ensure between 2 and MAX_PLAYERS
        } catch (NumberFormatException e) {
            playerCount = 2; // Default to 2 if invalid input
            System.out.println("Invalid input. Using 2 players.");
        }
        
        for (int i = 1; i <= playerCount; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String playerName = scanner.nextLine();
            if (playerName.trim().isEmpty()) {
                playerName = "Player " + i;
            }
            players.add(new PokerPlayer(playerName));
        }
        
        // Set the main player and dealer
        player = players.get(0);
        dealerIndex = players.size() - 1; // Last player is dealer for first round
    }

    @Override
    public void playRound() {
        System.out.println("\n--- New Round ---");
        pot = 0;
        communityCards.clear();
        
        // Collect blinds
        collectBlinds();
        
        // Deal hole cards (2 cards to each player)
        dealHoleCards();
        
        // Pre-flop betting round
        System.out.println("\n--- Pre-flop betting ---");
        if (bettingRound()) {
            // Flop (3 community cards)
            dealFlop();
            System.out.println("\n--- Flop betting ---");
            if (bettingRound()) {
                // Turn (1 more community card)
                dealTurn();
                System.out.println("\n--- Turn betting ---");
                if (bettingRound()) {
                    // River (1 final community card)
                    dealRiver();
                    System.out.println("\n--- River betting ---");
                    bettingRound();
                }
            }
        }
        
        // Showdown
        showdown();
    }

    private void collectBlinds() {
        int smallBlindIndex = (dealerIndex + 1) % players.size();
        int bigBlindIndex = (dealerIndex + 2) % players.size();
        
        PokerPlayer smallBlind = players.get(smallBlindIndex);
        PokerPlayer bigBlind = players.get(bigBlindIndex);
        
        System.out.println(smallBlind.getName() + " posts small blind: " + MIN_BET / 2);
        smallBlind.placeBet(MIN_BET / 2);
        pot += MIN_BET / 2;
        
        System.out.println(bigBlind.getName() + " posts big blind: " + MIN_BET);
        bigBlind.placeBet(MIN_BET);
        pot += MIN_BET;
        
        // Start with player after big blind
        currentPlayerIndex = (bigBlindIndex + 1) % players.size();
    }

    private void dealHoleCards() {
        System.out.println("\n--- Dealing hole cards ---");
        
        // Deal 2 cards to each player
        for (int i = 0; i < 2; i++) {
            for (PokerPlayer player : players) {
                player.addCard(deck.draw());
            }
        }
        
        // Show each player their cards
        for (PokerPlayer player : players) {
            System.out.println("\n" + player.getName() + "'s hole cards: " + player.getHand());
        }
    }
    
    private void dealFlop() {
        // Burn a card
        deck.draw();
        
        // Deal 3 community cards
        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.draw());
        }
        
        System.out.println("\n--- Flop ---");
        System.out.println("Community cards: " + communityCards);
    }
    
    private void dealTurn() {
        // Burn a card
        deck.draw();
        
        // Deal 1 community card
        Card turnCard = deck.draw();
        communityCards.add(turnCard);
        
        System.out.println("\n--- Turn ---");
        System.out.println("Turn card: " + turnCard);
        System.out.println("Community cards: " + communityCards);
    }
    
    private void dealRiver() {
        // Burn a card
        deck.draw();
        
        // Deal 1 community card
        Card riverCard = deck.draw();
        communityCards.add(riverCard);
        
        System.out.println("\n--- River ---");
        System.out.println("River card: " + riverCard);
        System.out.println("Community cards: " + communityCards);
    }

    /**
     * Conducts a betting round
     * 
     * @return true if more than one player is still active after the betting round, false otherwise
     */
    private boolean bettingRound() {
        int highestBet = getHighestBet();
        boolean roundComplete = false;
        
        // Track which players have had a chance to act in this round
        Set<PokerPlayer> playersActed = new HashSet<>();
        currentPlayerIndex = (dealerIndex + 3) % players.size(); // Start with player after big blind
        
        while (!roundComplete) {
            // Check if all players have folded except one
            if (countActivePlayers() == 1) {
                awardPotToLastActivePlayer();
                return false;
            }
            
            PokerPlayer currentPlayer = players.get(currentPlayerIndex);
            
            // Skip folded players
            if (currentPlayer.hasFolded()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                continue;
            }
            
            // If all active players have acted and all bets are matched, we're done
            if (playersActed.containsAll(getActivePlayers()) && allBetsMatched()) {
                roundComplete = true;
                continue;
            }
            
            // Get player action
            System.out.println("\n" + currentPlayer.getName() + "'s turn");
            
            processPlayerAction(currentPlayer, highestBet);
            
            // Mark this player as having acted
            playersActed.add(currentPlayer);
            
            // Update highest bet
            highestBet = getHighestBet();
            
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        
        // Reset bets for the next betting round
        for (PokerPlayer player : players) {
            player.resetBet();
        }
        
        return countActivePlayers() > 1;
    }
    
    /**
     * Checks if all active players have matched the highest bet
     */
    private boolean allBetsMatched() {
        int highestBet = getHighestBet();
        
        for (PokerPlayer player : players) {
            if (!player.hasFolded() && player.getCurrentBet() != highestBet) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets a list of all active (non-folded) players
     */
    private List<PokerPlayer> getActivePlayers() {
        List<PokerPlayer> activePlayers = new ArrayList<>();
        
        for (PokerPlayer player : players) {
            if (!player.hasFolded()) {
                activePlayers.add(player);
            }
        }
        
        return activePlayers;
    }

    private void processPlayerAction(PokerPlayer player, int highestBet) {
        int callAmount = highestBet - player.getCurrentBet();
        
        System.out.println("\nCurrent pot: " + pot);
        System.out.println("Community cards: " + communityCards);
        System.out.println(player.getName() + "'s hole cards: " + player.getHand());
        System.out.println(player.getName() + "'s chips: " + player.getChips());
        
        if (callAmount > 0) {
            System.out.println("Call amount: " + callAmount);
        }
        
        System.out.print("Action (fold/check/call/raise): ");
        String action = new Scanner(System.in).nextLine().toLowerCase();
        
        if (action.startsWith("f")) {
            player.fold();
            System.out.println(player.getName() + " folded.");
        } else if (action.startsWith("ch") && callAmount == 0) {
            // Check is only valid if no call amount is required
            System.out.println(player.getName() + " checked.");
        } else if (action.startsWith("c")) {
            if (callAmount > 0) {
                if (player.placeBet(callAmount)) {
                    pot += callAmount;
                    System.out.println(player.getName() + " called " + callAmount);
                } else {
                    System.out.println("Not enough chips. Going all-in with " + player.getChips());
                    pot += player.getChips();
                    player.placeBet(player.getChips());
                }
            } else {
                // If there's nothing to call, treat as check
                System.out.println(player.getName() + " checked.");
            }
        } else if (action.startsWith("r")) {
            System.out.print("Raise amount: ");
            try {
                int raiseAmount = Integer.parseInt(new Scanner(System.in).nextLine().trim());
                if (raiseAmount > callAmount) {
                    int totalBet = callAmount + raiseAmount;
                    if (player.placeBet(totalBet)) {
                        pot += totalBet;
                        System.out.println(player.getName() + " raised by " + raiseAmount);
                    } else {
                        System.out.println("Not enough chips. Going all-in with " + player.getChips());
                        pot += player.getChips();
                        player.placeBet(player.getChips());
                    }
                } else {
                    System.out.println("Raise amount must be greater than call amount. Treating as call.");
                    if (player.placeBet(callAmount)) {
                        pot += callAmount;
                        System.out.println(player.getName() + " called " + callAmount);
                    } else {
                        System.out.println("Not enough chips. Going all-in with " + player.getChips());
                        pot += player.getChips();
                        player.placeBet(player.getChips());
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Treating as call.");
                if (player.placeBet(callAmount)) {
                    pot += callAmount;
                    System.out.println(player.getName() + " called " + callAmount);
                } else {
                    System.out.println("Not enough chips. Going all-in with " + player.getChips());
                    pot += player.getChips();
                    player.placeBet(player.getChips());
                }
            }
        } else {
            System.out.println("Invalid action. Treating as call/check.");
            if (callAmount > 0) {
                if (player.placeBet(callAmount)) {
                    pot += callAmount;
                    System.out.println(player.getName() + " called " + callAmount);
                } else {
                    System.out.println("Not enough chips. Going all-in with " + player.getChips());
                    pot += player.getChips();
                    player.placeBet(player.getChips());
                }
            } else {
                System.out.println(player.getName() + " checked.");
            }
        }
    }

    private void showdown() {
        List<PokerPlayer> activePlayers = new ArrayList<>();
        
        // Get all active players
        for (PokerPlayer player : players) {
            if (!player.hasFolded()) {
                activePlayers.add(player);
            }
        }
        
        if (activePlayers.size() == 1) {
            // Only one player left, they win by default
            PokerPlayer winner = activePlayers.get(0);
            System.out.println("\nAll other players folded. " + winner.getName() + " wins " + pot + " chips!");
            winner.addChips(pot);
            return;
        }
        
        System.out.println("\n--- Showdown ---");
        
        // Show all hands and calculate best 5-card hands
        Map<PokerPlayer, List<Card>> bestHands = new HashMap<>();
        Map<PokerPlayer, Integer> scores = new HashMap<>();
        
        for (PokerPlayer player : activePlayers) {
            // Combine hole cards and community cards
            List<Card> allCards = new ArrayList<>(player.getHand());
            allCards.addAll(communityCards);
            
            // Get the best 5-card hand
            List<Card> bestHand = findBestHand(allCards);
            int score = ((PokerScoringStrategy) scoringStrategy).calculateScore(bestHand);
            
            bestHands.put(player, bestHand);
            scores.put(player, score);
            
            System.out.println(player.getName() + " - Hole cards: " + player.getHand());
            System.out.println("Best hand: " + bestHand + " - " + getHandRankName(score));
        }
        
        // Find winner(s)
        List<PokerPlayer> winners = new ArrayList<>();
        int highestScore = -1;
        
        for (PokerPlayer player : activePlayers) {
            int score = scores.get(player);
            if (score > highestScore) {
                winners.clear();
                winners.add(player);
                highestScore = score;
            } else if (score == highestScore) {
                winners.add(player);
            }
        }
        
        // Award pot to winner(s)
        int winAmount = pot / winners.size();
        for (PokerPlayer winner : winners) {
            System.out.println("\n" + winner.getName() + " wins " + winAmount + " chips with " + getHandRankName(highestScore) + "!");
            winner.addChips(winAmount);
        }
        
        // Show updated chip counts
        System.out.println("\nChip counts:");
        for (PokerPlayer player : players) {
            System.out.println(player.getName() + ": " + player.getChips());
        }
    }
    
    private List<Card> findBestHand(List<Card> cards) {
        // Find the best 5-card hand from the 7 available cards
        List<List<Card>> combinations = generateCombinations(cards, 5);
        List<Card> bestHand = combinations.get(0);
        int highestScore = ((PokerScoringStrategy) scoringStrategy).calculateScore(bestHand);
        
        for (int i = 1; i < combinations.size(); i++) {
            List<Card> hand = combinations.get(i);
            int score = ((PokerScoringStrategy) scoringStrategy).calculateScore(hand);
            
            if (score > highestScore) {
                bestHand = hand;
                highestScore = score;
            }
        }
        
        return bestHand;
    }
    
    private List<List<Card>> generateCombinations(List<Card> cards, int k) {
        List<List<Card>> result = new ArrayList<>();
        generateCombinationsHelper(cards, k, 0, new ArrayList<>(), result);
        return result;
    }
    
    private void generateCombinationsHelper(List<Card> cards, int k, int start, List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    private String getHandRankName(int score) {
        int rank = score / 100;
        switch (rank) {
            case 10: return "Royal Flush";
            case 9: return "Straight Flush";
            case 8: return "Four of a Kind";
            case 7: return "Full House";
            case 6: return "Flush";
            case 5: return "Straight";
            case 4: return "Three of a Kind";
            case 3: return "Two Pair";
            case 2: return "Pair";
            default: return "High Card";
        }
    }

    private void awardPotToLastActivePlayer() {
        for (PokerPlayer player : players) {
            if (!player.hasFolded()) {
                System.out.println("\nAll other players folded. " + player.getName() + " wins " + pot + " chips!");
                player.addChips(pot);
                return;
            }
        }
    }

    private int countActivePlayers() {
        int count = 0;
        for (PokerPlayer player : players) {
            if (!player.hasFolded()) {
                count++;
            }
        }
        return count;
    }

    private int getLastActivePlayerIndex() {
        for (int i = players.size() - 1; i >= 0; i--) {
            if (!players.get(i).hasFolded()) {
                return i;
            }
        }
        return 0; // Should never happen if we have at least one active player
    }

    private int getHighestBet() {
        int highest = 0;
        for (PokerPlayer player : players) {
            highest = Math.max(highest, player.getCurrentBet());
        }
        return highest;
    }

    private void rotateDealerPosition() {
        dealerIndex = (dealerIndex + 1) % players.size();
    }

    @Override
    public boolean isGameOver() {
        // Game is over if only one player has chips
        int playersWithChips = 0;
        for (PokerPlayer player : players) {
            if (player.getChips() > 0) {
                playersWithChips++;
            }
        }
        return gameOver || playersWithChips <= 1;
    }

    @Override
    public void endGame() {
        System.out.println("\nThank you for playing Poker!");
        
        // Find the winner (player with most chips)
        PokerPlayer winner = players.get(0);
        for (PokerPlayer player : players) {
            if (player.getChips() > winner.getChips()) {
                winner = player;
            }
        }
        
        System.out.println("Game winner: " + winner.getName() + " with " + winner.getChips() + " chips!");
        
        // Show final chip counts for all players
        System.out.println("\nFinal chip counts:");
        for (PokerPlayer player : players) {
            System.out.println(player.getName() + ": " + player.getChips() + " chips");
        }
        
        closeResources();
    }

    @Override
    public void reset() {
        for (PokerPlayer player : players) {
            player.clearHand();
        }
        communityCards.clear();
        pot = 0;
    }

    public static void main(String[] args) {
        PokerGame game = new PokerGame();
        game.start();
    }
}
