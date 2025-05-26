package com.cardgame.tienlen;

import com.cardgame.core.*;
import java.util.*;

/**
 * Implementation of the Vietnamese card game Tiến Lên (Vietnamese: Tiến lên miền Nam).
 * This is a shedding-type game where the objective is to be the first to play all cards.
 */
public class TienLenGame extends MultiplayerGame<TienLenPlayer> {
    private TienLenHandEvaluator evaluator;
    private List<Card> currentPile;
    private int startingPlayerIndex;
    private int currentWinnerIndex;
    private boolean gameStarted;
    private List<Card> deck;
    private Map<TienLenPlayer, List<Card>> playerHands;
    private int passCount; // Count consecutive passes
    private static final int CARDS_PER_PLAYER = 13;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;
    private boolean isFirstMove = true; // Track if it's the first move of the game

    public TienLenGame() {
        super();
        this.evaluator = new TienLenHandEvaluator();
        this.currentPile = new ArrayList<>();
        this.gameStarted = false;
        this.playerHands = new HashMap<>();
        this.passCount = 0;
    }

    // Getter for the hand evaluator
    public TienLenHandEvaluator getEvaluator() {
        return evaluator;
    }

    // Getter for isFirstMove status
    public boolean isFirstMove() {
        return isFirstMove;
    }

    @Override
    public void start() {
        if (players.size() < MIN_PLAYERS || players.size() > MAX_PLAYERS) {
            return;
        }

        initializeGame();
        gameStarted = true;
        isFirstMove = true;  // Reset isFirstMove khi bắt đầu game mới
        // Không gọi playRound() trong GUI version
        // Game sẽ được điều khiển thông qua các lệnh gọi từ controller
    }


    private void initializeGame() {
        // Create and shuffle a new deck
        deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        for (String suit : suits) {
            for (int value = 3; value <= 15; value++) { // 3 to Ace (14), 2 (15)
                String rank = getRankString(value);
                deck.add(new Card(suit, rank, value));
            }
        }
        Collections.shuffle(deck);

        // Deal cards to players
        playerHands.clear();
        for (TienLenPlayer player : players) {
            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < CARDS_PER_PLAYER; i++) {
                if (deck.isEmpty()) break;
                hand.add(deck.remove(0));
            }
            // Sort hand by Tiến Lên order
            hand.sort((c1, c2) -> {
                int rank1 = evaluator.getTienLenRank(c1);
                int rank2 = evaluator.getTienLenRank(c2);
                if (rank1 != rank2) return rank1 - rank2;
                return c1.getSuitOrder() - c2.getSuitOrder();
            });
            playerHands.put(player, hand);
        }

        // Find player with 3 of Spades to start
        findStartingPlayer();
        currentPlayerIndex = startingPlayerIndex;
        currentWinnerIndex = startingPlayerIndex;
    }

    private String getRankString(int value) {
        switch (value) {
            case 11: return "Jack";
            case 12: return "Queen";
            case 13: return "King";
            case 14: return "Ace";
            case 15: return "2";
            default: return String.valueOf(value);
        }
    }

    private void findStartingPlayer() {
        for (int i = 0; i < players.size(); i++) {
            TienLenPlayer player = players.get(i);
            List<Card> hand = playerHands.get(player);
            for (Card card : hand) {
                if (card.getRank().equals("3") && card.getSuit().equalsIgnoreCase("Spades")) {
                    startingPlayerIndex = i;
                    currentPlayerIndex = i;  // Đảm bảo người có 3 bích sẽ đi trước
                    return;
                }
            }
        }
        // If no one has 3 of Spades, start with first player
        startingPlayerIndex = 0;
        currentPlayerIndex = 0;
    }

    /**
     * Gets the current player whose turn it is
     * @return The current player
     */
    public TienLenPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    @Override
    public void playRound() {
        // Trong phiên bản GUI, phương thức này sẽ không được sử dụng trực tiếp
        // Thay vào đó, controller sẽ gọi các phương thức makeMove() và passTurn()
        if (!gameStarted) {
            return;
        }
        
        // Logic xử lý reset pile đã được chuyển vào passTurn()
    }

    // This method is called by the controller to make a move
    /**
     * Handles when a player passes their turn
     * @param player The player who is passing
     * @return true if passing is allowed, false otherwise
     */
    public boolean passTurn(TienLenPlayer player) {
        if (!gameStarted || !getCurrentPlayer().equals(player)) {
            return false;
        }
        
        // Cannot pass when no cards are on the table
        if (currentPile.isEmpty()) {
            return false;
        }
        
        passCount++;
        
        // Kiểm tra nếu tất cả người chơi khác đã bỏ lượt
        // Chỉ reset pile khi tất cả người chơi khác đều bỏ lượt
        if (passCount >= players.size() - 1) {
            // Người chơi đã thắng vòng này - lượt tiếp theo là của họ
            currentPlayerIndex = currentWinnerIndex;
            currentPile.clear();
            passCount = 0;
        } else {
            // Chỉ chuyển lượt khi chưa đủ người bỏ qua
            nextPlayer();
        }
        
        return true;
    }
    
    public boolean makeMove(TienLenPlayer player, List<Card> cards) {
        if (!gameStarted || !getCurrentPlayer().equals(player)) {
            return false;
        }

        if (cards == null || cards.isEmpty()) {
            // Handle pass
            return passTurn(player);
        }

        // Player plays cards
        if (isFirstMove) {
            // Lượt đầu tiên luôn phải có quân 3 bích
            boolean hasThreeOfSpades = cards.stream()
                .anyMatch(card -> card.getRank().equals("3") && 
                           card.getSuit().equalsIgnoreCase("Spades"));
                               
            if (!hasThreeOfSpades) {
                // Kiểm tra xem người chơi này có quân 3 bích không
                List<Card> playerCards = playerHands.get(player);
                boolean playerHasThreeOfSpades = playerCards.stream()
                    .anyMatch(card -> card.getRank().equals("3") && 
                               card.getSuit().equalsIgnoreCase("Spades"));
                                   
                if (playerHasThreeOfSpades) {
                    return false; // Nếu có 3 bích thì bắt buộc phải đánh nó trong lượt đầu
                }
            }
            
            isFirstMove = false;
        }

        if (evaluator.isValidMove(cards, currentPile)) {
            // Remove played cards from player's hand
            List<Card> hand = playerHands.get(player);
            hand.removeAll(cards);
            
            // Update current pile and winner
            currentPile = new ArrayList<>(cards);
            currentWinnerIndex = currentPlayerIndex; // Track the winner of this round
            passCount = 0;
            
            // Move to next player
            nextPlayer();
            
            // Check for winner
            if (hand.isEmpty()) {
                endGame();
            }
            return true;
        }
        
        return false; // Invalid move
    }

    @Override
    public boolean isGameOver() {
        // Game is over if any player has no cards left
        return playerHands.values().stream().anyMatch(List::isEmpty);
    }

    @Override
    public void endGame() {
        // Game over logic will be handled by the GUI
        gameStarted = false;
    }

    @Override
    public void reset() {
        currentPile.clear();
        gameStarted = false;
        playerHands.clear();
        currentPlayerIndex = 0;
        startingPlayerIndex = 0;
        currentWinnerIndex = 0;
    }

    // Get the list of players
    @Override 
    public List<TienLenPlayer> getPlayers() {
        return new ArrayList<>(this.players); 
    }
    
    // Get a player's hand
    public List<Card> getPlayerHand(Player player) { 
        List<Card> hand = playerHands.get(player); 
        if (player instanceof TienLenPlayer) {
            return hand != null ? new ArrayList<>(hand) : new ArrayList<>();
        }
        return new ArrayList<>(); 
    }
    
    // Get the current pile of cards
    public List<Card> getCurrentPile() {
        return new ArrayList<>(currentPile);
    }
    
    // Check if a move is valid
    public boolean isValidMove(List<Card> cards) {
        // Nếu đang ở lượt đầu tiên, phải đánh quân 3 bích
        if (isFirstMove) {
            // Kiểm tra xem bộ bài được chọn có chứa quân 3 bích không
            boolean hasThreeOfSpades = cards.stream()
                .anyMatch(card -> card.getRank().equals("3") && 
                           card.getSuit().equalsIgnoreCase("Spades"));
            
            // Kiểm tra xem người chơi hiện tại có quân 3 bích không
            Player currentPlayer = getCurrentPlayer();
            List<Card> playerCards = playerHands.get(currentPlayer);
            boolean playerHasThreeOfSpades = playerCards.stream()
                .anyMatch(card -> card.getRank().equals("3") && 
                           card.getSuit().equalsIgnoreCase("Spades"));
            
            // Nếu người chơi có 3 bích nhưng không đánh nó, thì đây là nước đi không hợp lệ
            if (playerHasThreeOfSpades && !hasThreeOfSpades) {
                return false;
            }
        }
        
        // Kiểm tra tính hợp lệ của bộ bài theo luật Tiến Lên
        return evaluator.isValidMove(cards, currentPile);
    }
    
    // Play the selected cards
    public void playCards(List<Card> cards) {
        Player currentPlayer = getCurrentPlayer();
        List<Card> hand = playerHands.get(currentPlayer);
        
        // Remove played cards from player's hand
        hand.removeAll(cards);
        
        // Update current pile and winner
        currentPile = new ArrayList<>(cards);
        currentWinnerIndex = currentPlayerIndex;
        passCount = 0;
        
        // Move to next player
        nextPlayer();
    }
    
    public static void main(String[] args) {
        // Main method is not needed for GUI version
        // Game will be started through the controller
    }
}