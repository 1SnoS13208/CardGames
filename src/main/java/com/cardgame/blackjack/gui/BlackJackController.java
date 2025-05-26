package com.cardgame.blackjack.gui;

import com.cardgame.blackjack.BlackjackGame;
import com.cardgame.core.Card;
import static com.cardgame.core.CardImageUtil.createCardImageView;
import static com.cardgame.core.CardImageUtil.createCardBackImageView;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Slider;

public class BlackJackController {
    @FXML
    private Button quitButton; // Nút quit
    @FXML 
    private Pane betPane;
    @FXML
    private Pane mainGamePane;
    @FXML 
    private Slider betSlider;
    @FXML 
    private Button betOkButton;
    @FXML 
    private Label betNumberLabel;

    @FXML
    private Label playerHandLabel;
    @FXML
    private Label dealerHandLabel;

    @FXML
    private Label betLabel;
    @FXML
    private HBox dealerCardsArea;
    @FXML
    private Button hitButton;
    @FXML
    private Label moneyLabel;
    @FXML
    private Button newGameButton;
    @FXML
    private HBox playerCardsArea;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Button standButton;

    private BlackjackGame game;
    private boolean playerStood = false;
    private String playerName = "Player";
    private int currentBet = 0;

    @FXML
    public void initialize() {
        if (quitButton != null) {
            quitButton.setOnAction(e -> handleQuit(null));
            quitButton.setVisible(false);
        }
        if (newGameButton != null) {
            newGameButton.setVisible(false);
        }

        game = new BlackjackGame();
        playerNameLabel.setText(playerName);
        moneyLabel.setText("Money: $" + game.getPlayer().getChips());

        if (betSlider != null && betNumberLabel != null) {
            betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                betNumberLabel.setText(String.valueOf(newVal.intValue()));
            });
        }
        if (betPane != null) betPane.setVisible(false);
        startNewGame();
    }

    @FXML
    void handleHit(ActionEvent event) {
        game.playerHit();
        updateUI();
        if (game.isPlayerBust() || game.isPlayerBlackjack()) {
            endPlayerTurn();
        }
    }

    @FXML
    void handleStand(ActionEvent event) {
        game.playerStand();
        updateUI();
        endPlayerTurn();
    }

    @FXML
    void handleNewGame(ActionEvent event) {
        startNewGame();
    }

    private void startNewGame() {
        if(game.isGameOver()) {
            hitButton.setDisable(true);
            standButton.setDisable(true);
            showAlert("Game Over", "You have no more money!");
        } else {
            showBetPane();
        }
    }

    private void showBetPane() {
        int maxBet = game.getPlayer().getChips();
        if (betSlider != null) {
            betSlider.setMin(10);
            betSlider.setMax(maxBet);
            betSlider.setValue(Math.min(currentBet > 0 ? currentBet : 10, maxBet));
        }
        if (betNumberLabel != null && betSlider != null) {
            betNumberLabel.setText(String.valueOf((int)betSlider.getValue()));
        }
        if (mainGamePane != null) mainGamePane.setVisible(false);
        if (betPane != null) betPane.setVisible(true);
        if (betOkButton != null) betOkButton.setDisable(false);
    }

    @FXML
    void handleBetOk(ActionEvent event) {
        int currnetBet = (int) betSlider.getValue();
        int maxBet = game.getPlayer().getChips();
        if (currnetBet > 0 && currnetBet <= maxBet) {
            game.setBet(currnetBet);
            betLabel.setText("Current Bet: $" + currnetBet);
            if (betPane != null) betPane.setVisible(false);
            if (mainGamePane != null) mainGamePane.setVisible(true);
            playerStood = false;
            moneyLabel.setText("Money: $" + (game.getPlayer().getChips() - currnetBet));
            game.start();
            hitButton.setDisable(false);
            standButton.setDisable(false);
            if (newGameButton != null) newGameButton.setVisible(false);
            if (quitButton != null) quitButton.setVisible(false);
            updateUI();
            if (game.isPlayerBlackjack()) {
                endPlayerTurn();
            }
        } else {
            showAlert("Invalid bet", "Please select a valid bet amount (1-" + maxBet + ")");
        }
    }

    @FXML
    void handleQuit(ActionEvent event) {
        // Thoát ứng dụng
        javafx.application.Platform.exit();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Track dealer's cards for slow drawing
    private int dealerCardIndex = 0;
    
    private void endPlayerTurn() {
        playerStood = true;
        hitButton.setDisable(true);
        standButton.setDisable(true);
        
        // Instead of calling game.dealerTurn() immediately, we'll start the dealer's turn with animation
        dealerCardIndex = 1; // Start from 1 since the first card is already visible
        
        // Show the dealer's first card (which was already face up)
        updateDealerUI(true);
        
        // Start the dealer's turn with animation
        startDealerAnimation();
    }
    
    private void startDealerAnimation() {
        // Get dealer's cards after dealer's turn is complete
        game.dealerTurn();
        List<Card> dealerCards = getDealerCards();
        
        // If we haven't shown all dealer cards yet
        if (dealerCardIndex < dealerCards.size()) {
            // Use JavaFX animation timer to add delay between card draws
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(event -> {
                // Increment card index and update UI to show one more card
                dealerCardIndex++;
                updateDealerUI(false);
                
                // If more cards to show, continue animation
                if (dealerCardIndex < dealerCards.size()) {
                    startDealerAnimation();
                } else {
                    // All cards shown, finalize the game
                    finalizeGame();
                }
            });
            pause.play();
        } else {
            // No more cards to show, finalize the game
            finalizeGame();
        }
    }
    
    private void updateDealerUI(boolean firstUpdate) {
        dealerCardsArea.getChildren().clear();
        List<Card> dealerCards = getDealerCards();
        
        // Show only the cards up to dealerCardIndex
        for (int i = 0; i < Math.min(dealerCardIndex, dealerCards.size()); i++) {
            dealerCardsArea.getChildren().add(createCardImageView(dealerCards.get(i)));
        }
        
        // If this is the first update, don't show the score yet
        if (firstUpdate) {
            dealerHandLabel.setText("Dealer's Turn...");
        } else {
            // Calculate score of visible cards only
            int visibleScore = game.getDealerScore();
            dealerHandLabel.setText("Dealer Card: " + visibleScore);
        }
        
        // Always update player cards
        playerCardsArea.getChildren().clear();
        for (Card card : getPlayerCards()) {
            playerCardsArea.getChildren().add(createCardImageView(card));
        }
        playerHandLabel.setText("User Card: " + game.getPlayerScore());
    }
    
    private void finalizeGame() {
        // Show final result
        String result = game.getResultMessage();
        betLabel.setText(result + " Earn: " + game.getEarnAmount() + "$");
        moneyLabel.setText("Money: $" + game.getPlayer().getChips());
        
        // Show buttons for next actions
        if (newGameButton != null) newGameButton.setVisible(true);
        if (quitButton != null) quitButton.setVisible(true);
    }

    private void updateUI() {
        dealerCardsArea.getChildren().clear();
        List<Card> dealerCards = getDealerCards();
        if (!playerStood) {
            dealerCardsArea.getChildren().add(createCardImageView(dealerCards.get(0)));
            dealerCardsArea.getChildren().add(createCardBackImageView());
            
            int visibleScore = game.getDealerScore();
            
            dealerHandLabel.setText("Dealer Card: " + visibleScore);
        } else {
            for (Card card : dealerCards) {
                dealerCardsArea.getChildren().add(createCardImageView(card));
            }
            dealerHandLabel.setText("Dealer Card: " + game.getDealerScore());
        }

        playerCardsArea.getChildren().clear();
        for (Card card : getPlayerCards()) {
            playerCardsArea.getChildren().add(createCardImageView(card));
        }
        playerHandLabel.setText("User Card: " + game.getPlayerScore());
    }

    

    private List<Card> getDealerCards() {
        return game.getDealerCards();
    }

    private List<Card> getPlayerCards() {
        return game.getPlayerCards();
    }
}

