package com.cardgame.poker.gui;

import com.cardgame.poker.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.*;

public class PokerController implements Initializable {
    @FXML private Label potLabel;
    @FXML private Label currentBetLabel;
    @FXML private Button foldButton;
    @FXML private Button checkButton;
    @FXML private Button callButton;
    @FXML private Button raiseButton;
    @FXML private TextField raiseAmount;
    @FXML private TextArea gameLog;
    @FXML private HBox communityCards;
    @FXML private HBox playerCards;
    @FXML private GridPane playersGrid;
    
    private PokerGame game;
    private final Map<String, VBox> playerPanes = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupGame();
        setupEventHandlers();
        updateUI();
    }
    
    private void setupGame() {
        game = new PokerGame();
        // Add players (you can modify this to get player names from a dialog)
        game.addPlayer(new PokerPlayer("Player 1"));
        game.addPlayer(new PokerPlayer("Player 2"));
        game.addPlayer(new PokerPlayer("Player 3"));
        
        // Initialize the game
        game.start();
    }
    
    private void setupEventHandlers() {
        foldButton.setOnAction(e -> {
            // Handle fold action
            log("You folded");
            // Add game logic here
        });
        
        checkButton.setOnAction(e -> {
            // Handle check action
            log("You checked");
            // Add game logic here
        });
        
        callButton.setOnAction(e -> {
            // Handle call action
            log("You called");
            // Add game logic here
        });
        
        raiseButton.setOnAction(e -> {
            try {
                int amount = Integer.parseInt(raiseAmount.getText());
                log("You raised " + amount);
                // Add game logic here
            } catch (NumberFormatException ex) {
                log("Please enter a valid number");
            }
        });
    }
    
    private void updateUI() {
        // Update pot and current bet
        potLabel.setText("$" + game.getPot());
        currentBetLabel.setText("$" + game.getCurrentBet());
        
        // Update community cards
        updateCommunityCards();
        
        // Update player cards (for the human player)
        updatePlayerCards();
        
        // Update player panels
        updatePlayerPanels();
    }
    
    private void updateCommunityCards() {
        communityCards.getChildren().clear();
        for (Card card : game.getCommunityCards()) {
            ImageView cardView = createCardView(card);
            communityCards.getChildren().add(cardView);
        }
    }
    
    private void updatePlayerCards() {
        playerCards.getChildren().clear();
        // Assuming first player is the human player for now
        PokerPlayer humanPlayer = (PokerPlayer) game.getPlayers().get(0);
        for (Card card : humanPlayer.getHand()) {
            ImageView cardView = createCardView(card);
            playerCards.getChildren().add(cardView);
        }
    }
    
    private void updatePlayerPanels() {
        playersGrid.getChildren().clear();
        int col = 0;
        int row = 0;
        
        for (int i = 0; i < game.getPlayers().size(); i++) {
            PokerPlayer player = (PokerPlayer) game.getPlayers().get(i);
            VBox playerPane = createPlayerPane(player);
            playersGrid.add(playerPane, col, row);
            
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createPlayerPane(PokerPlayer player) {
        VBox pane = new VBox(5);
        pane.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        Label chipsLabel = new Label("Chips: " + player.getChips());
        chipsLabel.setStyle("-fx-text-fill: #f1c40f;");
        
        HBox cardsBox = new HBox(5);
        for (Card card : player.getHand()) {
            ImageView cardView = createCardView(card);
            cardsBox.getChildren().add(cardView);
        }
        
        pane.getChildren().addAll(nameLabel, chipsLabel, cardsBox);
        return pane;
    }
    
    private ImageView createCardView(Card card) {
        try {
            String cardImagePath = "/cards/" + card.getRank().name().toLowerCase() + "_of_" + 
                                 card.getSuit().name().toLowerCase() + ".png";
            Image image = new Image(getClass().getResourceAsStream(cardImagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            // Fallback to text representation if image not found
            Label cardLabel = new Label(card.toString());
            cardLabel.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: black;");
            return new ImageView();
        }
    }
    
    private void log(String message) {
        gameLog.appendText("\n" + message);
    }
}
