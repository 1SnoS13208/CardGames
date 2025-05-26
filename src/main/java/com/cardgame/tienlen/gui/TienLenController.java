package com.cardgame.tienlen.gui;

import com.cardgame.tienlen.TienLenGame;
import com.cardgame.tienlen.TienLenPlayer;
import com.cardgame.tienlen.ai.*;
import com.cardgame.tienlen.TienLenHandEvaluator;
import com.cardgame.core.Card;
import com.cardgame.core.Player;
import com.cardgame.core.CardImageUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class TienLenController {
    private TienLenGame game;
    private List<Card> selectedCards = new ArrayList<>();
    private boolean playWithAI; // To store the selected game mode
    
    @FXML
    public void initialize() {
        // Hide the main interface initially, only show the player selection panel
        mainGamePane.setVisible(false);
        playerSelectionPanel.setVisible(true);
        if (playerCountSelectionVBox != null) { // Check if FXML element is injected
            playerCountSelectionVBox.setVisible(false);
            playerCountSelectionVBox.setManaged(false);
        }
        
        // Disable control buttons until the game starts
        if (playButton != null) playButton.setDisable(true);
        if (passButton != null) passButton.setDisable(true);
    }

    @FXML private BorderPane mainGamePane;
    @FXML private HBox currentPile;
    @FXML private HBox playerCards;
    @FXML private HBox opponent1Cards;
    @FXML private HBox opponent2Cards;
    @FXML private HBox opponent3Cards;
    @FXML private Label statusLabel;
    // Labels for player names
    @FXML private Label Slot1;  // Current player
    @FXML private Label Slot2;  // Top opponent
    @FXML private Label Slot3;  // Left opponent
    @FXML private Label Slot4;  // Right opponent
    
    // Labels for card counts
    @FXML private Label opponent1CardsCount;  // Left opponent
    @FXML private Label opponent2CardsCount;  // Top opponent
    @FXML private Label opponent3CardsCount;  // Right opponent
    @FXML private Button playButton;
    @FXML private Button passButton;
    @FXML private Button newGameButton;
    @FXML private Button quitButton;
    @FXML private VBox playerSelectionPanel;
    @FXML private VBox playerCountSelectionVBox; // Added
    @FXML private Button playWithAIButton; // Added
    @FXML private Button playManuallyButton; // Added
    @FXML private Label modeSelectionPromptLabel; // Added
    @FXML private Label playerCountPromptLabel; // Added
    @FXML private Button twoPlayersBtn;
    @FXML private Button threePlayersBtn;
    @FXML private Button fourPlayersBtn;
    @FXML private StackPane root;

    @FXML
    void handleNewGame(ActionEvent event) {
        // Show the player selection panel again
        playerSelectionPanel.setVisible(true);
        if (playerCountSelectionVBox != null) {
            playerCountSelectionVBox.setVisible(false);
            playerCountSelectionVBox.setManaged(false);
        }
        if (playWithAIButton != null) playWithAIButton.setVisible(true); // Show mode buttons
        if (playManuallyButton != null) playManuallyButton.setVisible(true);
        if (modeSelectionPromptLabel != null) modeSelectionPromptLabel.setVisible(true);

        mainGamePane.setVisible(false);
        
        // Reset game state
        game = null;
        selectedCards.clear();
    }

    @FXML
    void handleModeSelection(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source == playWithAIButton) {
            playWithAI = true;
        } else if (source == playManuallyButton) {
            playWithAI = false;
        }

        // Hide mode selection UI
        if (playWithAIButton != null) playWithAIButton.setVisible(false);
        if (playManuallyButton != null) playManuallyButton.setVisible(false);
        if (modeSelectionPromptLabel != null) modeSelectionPromptLabel.setVisible(false);

        // Show player count selection UI
        if (playerCountSelectionVBox != null) {
            playerCountSelectionVBox.setVisible(true);
            playerCountSelectionVBox.setManaged(true);
        }
    }

    @FXML
    void handlePlayerCountSelection(ActionEvent event) { // Renamed from handlePlayerSelection
        Button source = (Button) event.getSource();
        int playerCount = 2; // Default to 2 players
        
        // Determine number of players based on which button was clicked
        if (source == twoPlayersBtn) {
            playerCount = 2;
        } else if (source == threePlayersBtn) {
            playerCount = 3;
        } else if (source == fourPlayersBtn) {
            playerCount = 4;
        }
        
        // Hide the player selection panel and show the main interface
        playerSelectionPanel.setVisible(false);
        // Ensure player count selection is hidden for next new game
        if (playerCountSelectionVBox != null) {
            playerCountSelectionVBox.setVisible(false);
            playerCountSelectionVBox.setManaged(false);
        }
        // Ensure mode selection buttons are visible for next new game (handled in handleNewGame)
        mainGamePane.setVisible(true);
        
        // Start new game with selected number of players and mode
        startNewGame(playerCount);
    }

    @FXML
    void handlePass(ActionEvent event) {
        if (game == null || game.isGameOver()) return;
        
        try {
            // Pass the turn
            TienLenPlayer currentPlayer = game.getCurrentPlayer();
            boolean moveSuccessful = game.passTurn(currentPlayer);
            
            if (moveSuccessful) {
                updateGameStateAfterMove();
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handlePlay(ActionEvent event) {
        if (game == null || game.isGameOver() || selectedCards.isEmpty()) {
            return;
        }

        TienLenHandEvaluator evaluator = game.getEvaluator();
        TienLenHandEvaluator.MoveType selectedHandType = evaluator.detectMoveType(selectedCards);

        if (selectedHandType == TienLenHandEvaluator.MoveType.INVALID) {
            return;
        }

        // Additional check: Sequences and Double Sequences cannot contain a '2'
        if ((selectedHandType == TienLenHandEvaluator.MoveType.SEQUENCE || selectedHandType == TienLenHandEvaluator.MoveType.DOUBLE_SEQUENCE) && evaluator.isContainsTwo(selectedCards)) {
            return;
        }

        try {
            TienLenPlayer currentPlayer = game.getCurrentPlayer();
            boolean moveSuccessful = game.makeMove(currentPlayer, selectedCards);
            
            if (moveSuccessful) {
                updateGameStateAfterMove();
            } 

        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleQuit(ActionEvent event) {
        // Thoát ứng dụng
        Platform.exit();
    }
    
    private void startNewGame(int playerCount) {
        try {
            // Khởi tạo game mới
            game = new TienLenGame();
            
            // Add players to the game
            if (playWithAI) {
                // Player 1 is always human, others are AI
                game.addPlayer(new TienLenPlayer("Player 1", false)); 
                for (int i = 1; i < playerCount; i++) {
                    game.addPlayer(new TienLenPlayer("Player " + (i + 1), true)); 
                }
            } else {
                // All players are human (manual/hotseat mode)
                for (int i = 0; i < playerCount; i++) {
                    game.addPlayer(new TienLenPlayer("Player " + (i + 1), false)); 
                }
            }
            
            // Start the game   
            game.start();
            
            // Update the interface
            updateGameState(); // This will also trigger AI turn if Player 1 (human) is not starting
            
            // Enable control buttons
            if (playButton != null) playButton.setDisable(false);
            if (passButton != null) passButton.setDisable(false);
            
        } catch (Exception e) {
            showAlert("Error", "Could not start the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAITurn(TienLenPlayer aiPlayer) {
        if (game == null || game.isGameOver() || !aiPlayer.isAI()) {
            return;
        }

        // Update UI to show which AI is currently playing
        if (statusLabel != null) {
            statusLabel.setText(aiPlayer.getName() + " (AI) is making a move...");
        }

        TienLenAIStrategy strategy = aiPlayer.getAiStrategy();
        if (strategy == null) {
            // Should not happen if AI players are initialized with a strategy
            System.err.println("AI Player " + aiPlayer.getName() + " has no strategy.");
            // AI will effectively pass by doing nothing
            game.passTurn(aiPlayer); // Or handle error appropriately
            finishAITurn();
            return;
        }

        List<Card> hand = game.getPlayerHand(aiPlayer);
        List<Card> pile = game.getCurrentPile();
        TienLenHandEvaluator evaluator = game.getEvaluator();
        boolean isFirstMoveOfGame = game.isFirstMove();
        boolean mustPlay3Spades = isFirstMoveOfGame && game.getPlayers().size() == 4;

        List<Card> cardsToPlay = strategy.chooseCardsToPlay(hand, pile, evaluator, isFirstMoveOfGame, mustPlay3Spades);

        boolean moveMade;
        if (cardsToPlay == null || cardsToPlay.isEmpty()) {
            // AI decides to pass or has no valid move
            // Ensure AI can pass if pile is not empty
            if (pile.isEmpty() && !isFirstMoveOfGame) {
                 // AI must play if it's leading a new round (and not the very first move of game where it might not have 3s)
                 // This scenario should ideally be handled by the strategy to always return a valid move
                 // For now, if strategy returns empty for a leading turn, force a pass (which might be invalid by game rules)
                 // A better strategy would find *any* valid move here.
                 // For RandomStrategy, it should find one if one exists.
                 System.out.println("AI " + aiPlayer.getName() + " attempted to pass when leading. This might be an issue with strategy or game state.");
                 // Attempt to pass, though game rules might prevent it.
                 moveMade = game.passTurn(aiPlayer);
                 if (!moveMade) {
                    // If pass is invalid (e.g. must lead), this is a strategy flaw. For now, log and let game proceed.
                    System.err.println("AI " + aiPlayer.getName() + " failed to make a mandatory leading move. Game state might be inconsistent.");
                 }
            } else {
                moveMade = game.passTurn(aiPlayer);
            }
            if (moveMade) {
                 System.out.println("AI " + aiPlayer.getName() + " passes.");
                 // Update status to show the AI's action
                 if (statusLabel != null) {
                     statusLabel.setText(aiPlayer.getName() + " (AI) passes.");
                 }
            }
        } else {
            moveMade = game.makeMove(aiPlayer, cardsToPlay);
            if (moveMade) {
                System.out.println("AI " + aiPlayer.getName() + " plays: " + cardsToPlay);
                // Update status to show the AI's action
                if (statusLabel != null) {
                    statusLabel.setText(aiPlayer.getName() + " (AI) plays " + cardsToPlay.size() + " card(s).");
                }
            }
        }

        if (!moveMade) {
            // This indicates an issue with the AI's chosen move or the game's validation logic
            // Or AI tried to pass when it shouldn't (e.g. leading a new round)
            System.err.println("AI " + aiPlayer.getName() + " made an invalid move or failed to pass: " + cardsToPlay);
            // As a fallback, make the AI pass if the move was invalid, to prevent game freeze
            // (if passing is also invalid, then there's a deeper issue)
            if (!game.passTurn(aiPlayer)) {
                 System.err.println("AI " + aiPlayer.getName() + " also failed to pass after invalid move. Game might be stuck.");
            }
        }
        
        // Schedule the completion of the AI turn with a small delay
        // This makes the AI's action visible to the player before moving to the next turn
        Platform.runLater(() -> {
            try {
                // Short delay to show the AI's action before proceeding
                Thread.sleep(100);
                finishAITurn();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                finishAITurn();
            }
        });
    }
    
    // Helper method to finish an AI turn and check if the next player is also AI
    private void finishAITurn() {
        // Clear selections and update UI
        selectedCards.clear();
        updateCurrentPile();
        
        // Check if game is over
        if (game == null || game.isGameOver()) {
            if (game != null && game.isGameOver()) {
                showGameOver();
            }
            return;
        }
        
        // Get the next player
        TienLenPlayer nextPlayer = game.getCurrentPlayer();
        
        // Update UI for the next player
        updateGameState();
        
        // If the next player is also AI and we're in AI mode, schedule their turn with a delay
        if (playWithAI && nextPlayer.isAI()) {
            // Disable buttons during AI turn
            playButton.setDisable(true);
            passButton.setDisable(true);
            
            // Update status to show next AI is thinking
            if (statusLabel != null) {
                statusLabel.setText(nextPlayer.getName() + " (AI) is thinking...");
            }
            
            // Schedule next AI turn with a delay
            Platform.runLater(() -> {
                try {
                    // Longer delay (2 seconds) between AI turns
                    Thread.sleep(100);
                    handleAITurn(nextPlayer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } else if (!nextPlayer.isAI()) {
            // Enable buttons for human player
            playButton.setDisable(false);
            passButton.setDisable(false);
            
            // Update status for human player
            if (statusLabel != null) {
                statusLabel.setText("Your turn!");
            }
        }
    }

    private void updateGameState() {
        if (game == null || game.isGameOver()) {
            if (game != null && game.isGameOver()) {
                showGameOver();
            }
            return;
        }
        
        TienLenPlayer currentPlayer = game.getCurrentPlayer();
        List<TienLenPlayer> players = game.getPlayers(); 
        
        // Update the status to show current player's turn
        if (statusLabel != null) {
            statusLabel.setText("Current turn: " + currentPlayer.getName() + (currentPlayer.isAI() ? " (AI)" : " (Human)"));
        }
        
        // Update player names and cards based on game mode
        if (playWithAI) {
            // In AI mode, always show human player at the bottom
            updatePlayerHandsAIMode(players, currentPlayer);
        } else {
            // In manual mode, show current player at the bottom
            if (Slot1 != null) {
                Slot1.setText(currentPlayer.getName());
            }
            updatePlayerHand(currentPlayer);
            updateOpponentHands(players);
        }
        
        // Update the current pile
        updateCurrentPile();

        // If the current player is AI, handle their turn
        if (currentPlayer.isAI()) {
            playButton.setDisable(true);
            passButton.setDisable(true);
            // Use Platform.runLater to allow UI updates before AI processing, and to avoid blocking UI thread
            Platform.runLater(() -> {
                // Add a small delay to make AI moves observable
                try {
                    Thread.sleep(100); // 1 second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                handleAITurn(currentPlayer);
            });
        } else {
            playButton.setDisable(false);
            passButton.setDisable(false);
        }
    }
    
    // New method to update player hands in AI mode
    private void updatePlayerHandsAIMode(List<TienLenPlayer> allPlayers, TienLenPlayer currentPlayer) {
        if (game == null || allPlayers == null || allPlayers.isEmpty()) return;
        
        // Find the human player (always the first player in AI mode)
        TienLenPlayer humanPlayer = null;
        List<TienLenPlayer> aiPlayers = new ArrayList<>();
        
        for (TienLenPlayer player : allPlayers) {
            if (!player.isAI()) {
                humanPlayer = player;
            } else {
                aiPlayers.add(player);
            }
        }
        
        if (humanPlayer == null) return; // Safety check
        
        // Always show human player's cards at the bottom
        if (Slot4 != null) {
            Slot4.setText(humanPlayer.getName() + "'s Cards");
        }
        
        // Update human player's hand
        if (playerCards != null) {
            playerCards.getChildren().clear();
            List<Card> hand = game.getPlayerHand(humanPlayer);
            
            for (Card card : hand) {
                try {
                    ImageView cardView = CardImageUtil.createCardImageView(card);
                    cardView.setFitWidth(80);
                    cardView.setPreserveRatio(true);
                    
                    // Only add selection handler if it's the human player's turn
                    if (currentPlayer.equals(humanPlayer)) {
                        cardView.setOnMouseClicked(event -> {
                            if (selectedCards.contains(card)) {
                                selectedCards.remove(card);
                                cardView.setTranslateY(0);
                            } else {
                                selectedCards.add(card);
                                cardView.setTranslateY(-20);
                            }
                        });
                    }
                    
                    // Highlight if selected
                    if (selectedCards.contains(card)) {
                        cardView.setTranslateY(-20);
                    }
                    
                    playerCards.getChildren().add(cardView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Clear all opponent displays
        opponent1Cards.getChildren().clear();
        opponent2Cards.getChildren().clear();
        opponent3Cards.getChildren().clear();
        
        // Hide all opponent slots initially
        if (opponent1Cards.getParent() != null) opponent1Cards.getParent().setVisible(false);
        if (opponent2Cards.getParent() != null) opponent2Cards.getParent().setVisible(false);
        if (opponent3Cards.getParent() != null) opponent3Cards.getParent().setVisible(false);
        
        // Update AI players' hands based on their positions
        for (int i = 0; i < aiPlayers.size(); i++) {
            TienLenPlayer aiPlayer = aiPlayers.get(i);
            List<Card> hand = game.getPlayerHand(aiPlayer);
            boolean isCurrentPlayer = aiPlayer.equals(currentPlayer);
            
            // Highlight the current AI player
            String playerLabel = aiPlayer.getName() + (isCurrentPlayer ? " (Current Turn)" : "");
            
            // Position AI players based on their index
            switch (i) {
                case 0: // Left opponent
                    if (opponent1Cards.getParent() != null) opponent1Cards.getParent().setVisible(true);
                    if (Slot1 != null) Slot1.setText(playerLabel);
                    updateOpponentCardDisplay(opponent1Cards, hand, opponent1CardsCount);
                    break;
                case 1: // Top opponent
                    if (opponent2Cards.getParent() != null) opponent2Cards.getParent().setVisible(true);
                    if (Slot2 != null) Slot2.setText(playerLabel);
                    updateOpponentCardDisplay(opponent2Cards, hand, opponent2CardsCount);
                    break;
                case 2: // Right opponent
                    if (opponent3Cards.getParent() != null) opponent3Cards.getParent().setVisible(true);
                    if (Slot3 != null) Slot3.setText(playerLabel);
                    updateOpponentCardDisplay(opponent3Cards, hand, opponent3CardsCount);
                    break;
            }
        }
    }
    
    // Helper method to update an opponent's card display
    private void updateOpponentCardDisplay(HBox cardContainer, List<Card> hand, Label countLabel) {
        cardContainer.getChildren().clear();
        
        // Update card count
        if (countLabel != null) {
            countLabel.setText(hand.size() + " cards");
        }
        
        // Show just a single card back as a symbol for each opponent, regardless of card count
        if (!hand.isEmpty()) {
            try {
                ImageView cardBack = CardImageUtil.createCardBackImageView();
                cardBack.setFitWidth(80); // Slightly larger for better visibility
                cardBack.setPreserveRatio(true);
                cardContainer.getChildren().add(cardBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGameStateAfterMove() {
        selectedCards.clear(); // Clear any human selections
        
        if (game == null) return;
        
        // Check if the game is over
        if (game.isGameOver()) {
            showGameOver();
            return;
        }
        
        TienLenPlayer nextPlayer = game.getCurrentPlayer();
        
        // Update UI to show the current state
        updateGameState();
        
        // In AI mode, if the next player is AI, schedule their turn with a delay
        // In manual mode, just show a message for player switching
        if (playWithAI && nextPlayer.isAI()) {
            // Disable buttons during AI turn
            playButton.setDisable(true);
            passButton.setDisable(true);
            
            // Update status to show AI is thinking
            if (statusLabel != null) {
                statusLabel.setText(nextPlayer.getName() + " (AI) is thinking...");
            }
            
            // Schedule AI turn with a delay to make it more visible to the player
            Platform.runLater(() -> {
                try {
                    // Longer delay (2 seconds) to make AI turns more distinct
                    Thread.sleep(100);
                    handleAITurn(nextPlayer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } 
    }

    private void updatePlayerHand(TienLenPlayer player) {
        if (playerCards == null || game == null) return;
        
        // Clear current cards
        playerCards.getChildren().clear();
        
        // Get current player's hand
        List<Card> hand = game.getPlayerHand(player);
        
        // Note: Slot1 and statusLabel are now updated in updateGameState()
        // to ensure consistency
        
        // Add cards to hand
        for (Card card : hand) {
            try {
                ImageView cardView = CardImageUtil.createCardImageView(card);
                cardView.setFitWidth(80);
                cardView.setPreserveRatio(true);
                
                // Add selection handler
                cardView.setOnMouseClicked(event -> {
                    if (selectedCards.contains(card)) {
                        selectedCards.remove(card);
                        cardView.setTranslateY(0);
                    } else {
                        selectedCards.add(card);
                        cardView.setTranslateY(-20);
                    }
                    updateGameState();
                });
                
                // Highlight if selected
                if (selectedCards.contains(card)) {
                    cardView.setTranslateY(-20);
                }
                
                playerCards.getChildren().add(cardView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Status label is now updated in updateGameState() to ensure consistency
    }
    
    private void updateOpponentHands(List<TienLenPlayer> allPlayers) {
        if (game == null) return;
        
        TienLenPlayer currentPlayer = game.getCurrentPlayer();

        // Clear all opponent card displays
        opponent1Cards.getChildren().clear();
        opponent2Cards.getChildren().clear();
        opponent3Cards.getChildren().clear();
        
        // Hide all opponent slots initially
        if (opponent1Cards.getParent() != null) opponent1Cards.getParent().setVisible(false);
        if (opponent2Cards.getParent() != null) opponent2Cards.getParent().setVisible(false);
        if (opponent3Cards.getParent() != null) opponent3Cards.getParent().setVisible(false);
        
        // Show only the needed number of opponent slots
        int totalPlayers = allPlayers.size();
        for (int i = 1; i < totalPlayers; i++) {
            switch (i) {
                case 1: if (opponent1Cards.getParent() != null) opponent1Cards.getParent().setVisible(true); break;
                case 2: if (opponent2Cards.getParent() != null) opponent2Cards.getParent().setVisible(true); break;
                case 3: if (opponent3Cards.getParent() != null) opponent3Cards.getParent().setVisible(true); break;
            }
        }
        
        // Get current player index
        int currentPlayerIndex = allPlayers.indexOf(currentPlayer);
        
        // Calculate opponent positions relative to current player
        // The order is: current player (Slot4), left (Slot1), top (Slot2), right (Slot3)
        for (int i = 1; i < allPlayers.size(); i++) {
            int opponentIndex = (currentPlayerIndex + i) % allPlayers.size();
            TienLenPlayer opponent = allPlayers.get(opponentIndex);
            List<Card> hand = game.getPlayerHand(opponent);
            
            // Determine position (1: Slot1, 2: Slot2, 3: Slot3)
            int position = i;
            
            // Update UI based on opponent position
            switch (position) {
                case 1: // Next player (Slot1)
                    if (Slot1 != null) {
                        Slot1.setText(opponent.getName());
                    }
                    if (opponent1CardsCount != null) {
                        opponent1CardsCount.setText(hand.size() + " cards");
                    }
                    // Show just one card back to represent the opponent's hand
                    if (!hand.isEmpty()) {
                        ImageView cardBack = CardImageUtil.createCardBackImageView();
                        cardBack.setFitHeight(100);
                        cardBack.setPreserveRatio(true);
                        opponent1Cards.getChildren().add(cardBack);
                    }
                    break;
                    
                case 2: // Second next player (Slot2)
                    if (Slot2 != null) {
                        Slot2.setText(opponent.getName());
                    }
                    if (opponent2CardsCount != null) {
                        opponent2CardsCount.setText(hand.size() + " cards");
                    }
                    // Show just one card back to represent the opponent's hand
                    if (!hand.isEmpty()) {
                        ImageView cardBack = CardImageUtil.createCardBackImageView();
                        cardBack.setFitHeight(100);
                        cardBack.setPreserveRatio(true);
                        opponent2Cards.getChildren().add(cardBack);
                    }
                    break;
                    
                case 3: // Third next player (Slot3)
                    if (Slot3 != null) {
                        Slot3.setText(opponent.getName());
                    }
                    if (opponent3CardsCount != null) {
                        opponent3CardsCount.setText(hand.size() + " cards");
                    }
                    // Show just one card back to represent the opponent's hand
                    if (!hand.isEmpty()) {
                        ImageView cardBack = CardImageUtil.createCardBackImageView();
                        cardBack.setFitHeight(100);
                        cardBack.setPreserveRatio(true);
                        opponent3Cards.getChildren().add(cardBack);
                    }
                    break;
            }
        }
        
        // Update current player's name (Slot4 is for current player)
        if (Slot4 != null) {
            Slot4.setText(currentPlayer.getName());
        }
    }
    
    // Keep track of previous pile for visual stacking effect
    private List<Card> previousPile = new ArrayList<>();
    private int pileLayerCount = 0; // Count of pile layers (increments with each new play)
    private static final int MAX_PILE_LAYERS = 2; // Maximum number of visible pile layers
    
    private void updateCurrentPile() {
        if (currentPile == null || game == null) return;
        
        // Get current pile from game
        List<Card> pile = game.getCurrentPile();
        
        // If the pile is empty, clear everything and reset
        if (pile.isEmpty()) {
            currentPile.getChildren().clear();
            previousPile.clear();
            pileLayerCount = 0;
            return;
        }
        
        // Check if this is a new play (different from previous pile)
        boolean isNewPlay = false;
        if (pile.size() != previousPile.size()) {
            isNewPlay = true;
        } else {
            // Check if any card is different
            for (int i = 0; i < pile.size(); i++) {
                if (!pile.get(i).equals(previousPile.get(i))) {
                    isNewPlay = true;
                    break;
                }
            }
        }
        
        // If this is a new play, update the display with stacking effect
        if (isNewPlay) {
            // Increment layer count, but keep it within bounds
            pileLayerCount = Math.min(pileLayerCount + 1, MAX_PILE_LAYERS);
            
            // Clear current display
            currentPile.getChildren().clear();
            
            // If we have previous cards and we're not at the first layer,
            // add faded versions of previous cards with offset
            if (!previousPile.isEmpty() && pileLayerCount > 1) {
                // Add previous pile cards with offset and fading
                for (int layer = 1; layer < pileLayerCount; layer++) {
                    // Only show the most recent MAX_PILE_LAYERS layers
                    if (layer >= MAX_PILE_LAYERS) continue;
                    
                    // Calculate offset based on layer
                    // Newer cards should be higher (negative Y offset) and slightly to the right
                    // Older cards should be lower (positive Y offset) and slightly to the left
                    double xOffset = -5 * (pileLayerCount - layer); // Reduced horizontal spacing
                    double yOffset = 10 * (pileLayerCount - layer); // Positive Y means lower (older cards at bottom)
                    double opacity = 0.4 + (0.6 / pileLayerCount) * layer; // Slightly higher base opacity
                    
                    // Get the cards for this layer (most recent is previousPile)
                    List<Card> layerCards = (layer == pileLayerCount - 1) ? previousPile : previousPile;
                    
                    // Add the cards for this layer
                    for (Card card : layerCards) {
                        try {
                            ImageView cardView = CardImageUtil.createCardImageView(card);
                            cardView.setFitWidth(80);
                            cardView.setPreserveRatio(true);
                            cardView.setTranslateX(xOffset);
                            cardView.setTranslateY(yOffset);
                            cardView.setOpacity(opacity);
                            currentPile.getChildren().add(cardView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            // Add current pile cards on top with a slight elevation
            for (Card card : pile) {
                try {
                    ImageView cardView = CardImageUtil.createCardImageView(card);
                    cardView.setFitWidth(80);
                    cardView.setPreserveRatio(true);
                    // Elevate the newest cards (negative Y value moves them up)
                    cardView.setTranslateY(-15); // Newest cards are higher
                    currentPile.getChildren().add(cardView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Save current pile as previous for next update
            previousPile = new ArrayList<>(pile);
        }
    }
    
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    private void showGameOver() {
        if (game == null) return;
        
        // Find the winner (first player to run out of cards)
        Player winner = game.getPlayers().stream()
                .filter(p -> p.getHand().isEmpty())
                .findFirst()
                .orElse(null);
        
        // Update game status (instead of showing a message)
        if (statusLabel != null) {
            String message = (winner != null) ? 
                    "Congratulations " + winner.getName() + " has won!" : 
                    "Game over!";
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold; -fx-font-size: 1.5em;");
        }
        
        // Disable control buttons
        if (playButton != null) playButton.setDisable(true);
        if (passButton != null) passButton.setDisable(true);
    }
}
