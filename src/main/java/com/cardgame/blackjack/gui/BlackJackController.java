package com.cardgame.blackjack.gui;

import com.cardgame.blackjack.BlackjackGame;
import com.cardgame.core.Card;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.control.Slider;

public class BlackJackController {
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
        game = new BlackjackGame();
        playerNameLabel.setText(playerName);
        moneyLabel.setText("Money: $" + game.getPlayer().getChips());
        // Thiết lập listener cho betSlider
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
            betSlider.setMin(1);
            betSlider.setMax(maxBet);
            betSlider.setValue(Math.min(currentBet > 0 ? currentBet : 1, maxBet));
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
        int bet = (int) betSlider.getValue();
        int maxBet = game.getPlayer().getChips();
        if (bet > 0 && bet <= maxBet) {
            currentBet = bet;
            betLabel.setText("Current Bet: $" + currentBet);
            if (betPane != null) betPane.setVisible(false);
            if (mainGamePane != null) mainGamePane.setVisible(true);
            playerStood = false;
            game.getPlayer().removeChips(currentBet);
            moneyLabel.setText("Money: $" + game.getPlayer().getChips());
            game.start();
            hitButton.setDisable(false);
            standButton.setDisable(false);
            updateUI();
            if (game.isPlayerBlackjack()) {
                endPlayerTurn();
            }
        } else {
            showAlert("Invalid bet", "Please select a valid bet amount (1-" + maxBet + ")");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void endPlayerTurn() {
        playerStood = true;
        hitButton.setDisable(true);
        standButton.setDisable(true);
        game.dealerTurn();
        updateUI();
        String result = game.getResultMessage();
        if (game.isPlayerWin()) {
            betLabel.setText(result + " + " + currentBet);
            game.getPlayer().addChips(currentBet * 2);
            moneyLabel.setText("Money: $" + game.getPlayer().getChips());
        }
        else {
            betLabel.setText(result + " - " + currentBet);
            moneyLabel.setText("Money: $" + game.getPlayer().getChips());
        }
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

    private ImageView createCardBackImageView() {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream("/cards/back.png")));
        view.setFitWidth(60);
        view.setFitHeight(90);
        return view;
    }

    private List<Card> getDealerCards() {
        return game.getDealerCards();
    }

    private List<Card> getPlayerCards() {
        return game.getPlayerCards();
    }

    private ImageView createCardImageView(Card card) {
        String imagePath = "/cards/" + card.getImageFileName();
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        view.setFitWidth(60);
        view.setFitHeight(90);
        return view;
    }

    
}

