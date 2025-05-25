package com.cardgame.tienlen.gui;

import com.cardgame.tienlen.TienLenGame;
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
    
    @FXML
    public void initialize() {
        // Ẩn giao diện chính ban đầu, chỉ hiển thị panel chọn người chơi
        mainGamePane.setVisible(false);
        playerSelectionPanel.setVisible(true);
        
        // Tắt các nút điều khiển cho đến khi bắt đầu game
        if (playButton != null) playButton.setDisable(true);
        if (passButton != null) passButton.setDisable(true);
    }

    @FXML private BorderPane mainGamePane;
    @FXML private HBox currentPile;
    @FXML private HBox playerCards;
    @FXML private VBox opponent1Cards;
    @FXML private HBox opponent2Cards;
    @FXML private VBox opponent3Cards;
    @FXML private Label statusLabel;
    @FXML private Label opponent1CardsCount;
    @FXML private Label opponent2CardsCount;
    @FXML private Label opponent3CardsCount;
    @FXML private Button playButton;
    @FXML private Button passButton;
    @FXML private Button newGameButton;
    @FXML private Button quitButton;
    @FXML private VBox playerSelectionPanel;
    @FXML private Button twoPlayersBtn;
    @FXML private Button threePlayersBtn;
    @FXML private Button fourPlayersBtn;
    @FXML private StackPane root;

    @FXML
    void handleNewGame(ActionEvent event) {
        // Hiển thị lại panel chọn người chơi
        playerSelectionPanel.setVisible(true);
        mainGamePane.setVisible(false);
        
        // Reset trạng thái game
        game = null;
        selectedCards.clear();
    }

    @FXML
    void handlePass(ActionEvent event) {
        if (game == null || game.isGameOver()) return;
        
        try {
            // Thực hiện bỏ lượt
            Player currentPlayer = game.getCurrentPlayer();
            boolean moveSuccessful = game.passTurn(currentPlayer);
            
            if (moveSuccessful) {
                selectedCards.clear();
                updateGameState();
                
                if (game.isGameOver()) {
                    showGameOver();
                }
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handlePlay(ActionEvent event) {
        if (game == null || game.isGameOver() || selectedCards.isEmpty()) return;
        
        try {
            Player currentPlayer = game.getCurrentPlayer();
            boolean moveSuccessful = game.makeMove(currentPlayer, selectedCards);
            
            if (moveSuccessful) {
                selectedCards.clear();
                updateGameState();
                
                if (game.isGameOver()) {
                    showGameOver();
                }
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handlePlayerSelection(ActionEvent event) {
        // Luôn sử dụng 4 người chơi
        int playerCount = 4;
        
        // Ẩn panel chọn người chơi và hiển thị giao diện chính
        playerSelectionPanel.setVisible(false);
        mainGamePane.setVisible(true);
        
        // Khởi tạo game mới
        startNewGame(playerCount);
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
            
            // Thêm người chơi vào game
            for (int i = 0; i < playerCount; i++) {
                game.addPlayer(new Player("Người chơi " + (i + 1)));
            }
            
            // Bắt đầu game
            game.start();
            
            // Cập nhật giao diện
            updateGameState();
            
            // Bật các nút điều khiển
            if (playButton != null) playButton.setDisable(false);
            if (passButton != null) passButton.setDisable(false);
            
            // Không hiển thị thông báo khi bắt đầu trò chơi
            
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể bắt đầu trò chơi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateGameState() {
        if (game == null || game.isGameOver()) {
            if (game != null && game.isGameOver()) {
                showGameOver();
            }
            return;
        }
        
        // Cập nhật trạng thái hiện tại
        if (statusLabel != null) {
            statusLabel.setText("Đến lượt: " + game.getCurrentPlayer().getName());
        }
        
        // Cập nhật bài của người chơi hiện tại
        updatePlayerCards();
        
        // Cập nhật bài của các đối thủ
        updateOpponentsCards();
        
        // Cập nhật bài đang đánh
        updateCurrentPile();
        
        // Cập nhật trạng thái các nút điều khiển
        if (playButton != null) {
            // Kiểm tra tính hợp lệ của bộ bài được chọn
            boolean isValidMove = !selectedCards.isEmpty() && game.isValidMove(selectedCards);
            playButton.setDisable(!isValidMove);
            
            // Cập nhật hiển thị nút đánh
            if (isValidMove) {
                playButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120; -fx-min-height: 50; -fx-font-size: 1.2em;");
            } else {
                playButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120; -fx-min-height: 50; -fx-font-size: 1.2em;");
            }
        }
        
        if (passButton != null) {
            passButton.setDisable(game.getCurrentPile().isEmpty());
        }
    }
    
    private void updatePlayerCards() {
        if (playerCards == null || game == null) return;
        
        playerCards.getChildren().clear();
        Player currentPlayer = game.getCurrentPlayer();
        List<Card> hand = game.getPlayerHand(currentPlayer);
        
        for (Card card : hand) {
            try {
                // Tạo ImageView mới cho mỗi lá bài sử dụng CardImageUtil
                ImageView cardView = CardImageUtil.createCardImageView(card);
                cardView.setFitWidth(90);
                cardView.setFitHeight(120);
                
                // Highlight lá bài đang được chọn
                if (selectedCards.contains(card)) {
                    cardView.setStyle("-fx-effect: dropshadow(gaussian, #2ecc71, 10, 0.7, 0, 0); -fx-translate-y: -15;");
                }
                
                // Xử lý sự kiện click chọn bài
                cardView.setOnMouseClicked(event -> {
                    if (selectedCards.contains(card)) {
                        selectedCards.remove(card);
                        cardView.setStyle("-fx-effect: none; -fx-translate-y: 0;");
                    } else {
                        selectedCards.add(card);
                        cardView.setStyle("-fx-effect: dropshadow(gaussian, #2ecc71, 10, 0.7, 0, 0); -fx-translate-y: -15;");
                    }
                    
                    // Kiểm tra tính hợp lệ của bộ bài được chọn
                    boolean isValidMove = !selectedCards.isEmpty() && game.isValidMove(selectedCards);
                    
                    // Chỉ cho phép nút "Đánh" sáng khi chọn bộ bài hợp lệ
                    playButton.setDisable(!isValidMove);
                    
                    // Cập nhật hiển thị nút đánh
                    if (isValidMove) {
                        playButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120; -fx-min-height: 50; -fx-font-size: 1.2em;");
                    } else {
                        playButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120; -fx-min-height: 50; -fx-font-size: 1.2em;");
                    }
                    
                    updatePlayerCards(); // Cập nhật lại để hiển thị đúng thẻ bài được chọn
                });
                
                playerCards.getChildren().add(cardView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateOpponentsCards() {
        // Cập nhật số lượng bài của các đối thủ
        List<Player> players = game.getPlayers();
        
        // Không cần ẩn hiện khu vực đối thủ vì luôn có 4 người chơi
        
        // Đối thủ 1 (bên trái)
        if (players.size() > 1 && opponent1Cards != null) {
            Player opponent = players.get(1);
            int cardCount = game.getPlayerHand(opponent).size();
            
            // Cập nhật hiển thị bài của đối thủ (chỉ hiển thị 1 lá bài úp)
            opponent1Cards.getChildren().clear();
            
            if (cardCount > 0) {
                ImageView cardView = CardImageUtil.createCardBackImageView();
                cardView.setFitWidth(80);
                cardView.setFitHeight(120);
                cardView.setRotate(90); // Quay ngang cho đối thủ bên trái
                
                // Tạo overlay với số lượng lá bài
                StackPane cardPane = new StackPane();
                cardPane.getChildren().add(cardView);
                
                Label countLabel = new Label(String.valueOf(cardCount));
                countLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
                cardPane.getChildren().add(countLabel);
                
                opponent1Cards.getChildren().add(cardPane);
            }
            
            // Cập nhật text cho số lượng lá bài
            if (opponent1CardsCount != null) {
                opponent1CardsCount.setText(opponent.getName());
            }
        }
        
        // Đối thủ 2 (bên trên)
        if (players.size() > 2 && opponent2Cards != null) {
            Player opponent = players.get(2);
            int cardCount = game.getPlayerHand(opponent).size();
            
            // Cập nhật hiển thị bài của đối thủ (chỉ hiển thị 1 lá bài úp)
            opponent2Cards.getChildren().clear();
            
            if (cardCount > 0) {
                ImageView cardView = CardImageUtil.createCardBackImageView();
                cardView.setFitWidth(80);
                cardView.setFitHeight(120);
                
                // Tạo overlay với số lượng lá bài
                StackPane cardPane = new StackPane();
                cardPane.getChildren().add(cardView);
                
                Label countLabel = new Label(String.valueOf(cardCount));
                countLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
                cardPane.getChildren().add(countLabel);
                
                opponent2Cards.getChildren().add(cardPane);
            }
            
            // Cập nhật text cho số lượng lá bài
            if (opponent2CardsCount != null) {
                opponent2CardsCount.setText(opponent.getName());
            }
        }
        
        // Đối thủ 3 (bên phải)
        if (players.size() > 3 && opponent3Cards != null) {
            Player opponent = players.get(3);
            int cardCount = game.getPlayerHand(opponent).size();
            
            // Cập nhật hiển thị bài của đối thủ (chỉ hiển thị 1 lá bài úp)
            opponent3Cards.getChildren().clear();
            
            if (cardCount > 0) {
                ImageView cardView = CardImageUtil.createCardBackImageView();
                cardView.setFitWidth(80);
                cardView.setFitHeight(120);
                cardView.setRotate(90); // Quay ngang cho đối thủ bên phải
                
                // Tạo overlay với số lượng lá bài
                StackPane cardPane = new StackPane();
                cardPane.getChildren().add(cardView);
                
                Label countLabel = new Label(String.valueOf(cardCount));
                countLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
                cardPane.getChildren().add(countLabel);
                
                opponent3Cards.getChildren().add(cardPane);
            }
            
            // Cập nhật text cho số lượng lá bài
            if (opponent3CardsCount != null) {
                opponent3CardsCount.setText(opponent.getName());
            }
        }
    }
    
    private void updateCurrentPile() {
        if (currentPile == null) return;
        
        currentPile.getChildren().clear();
        List<Card> pile = game.getCurrentPile();
        
        if (pile != null && !pile.isEmpty()) {
            for (Card card : pile) {
                try {
                    ImageView cardView = CardImageUtil.createCardImageView(card);
                    cardView.setFitWidth(80);
                    cardView.setFitHeight(120);
                    currentPile.getChildren().add(cardView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        
        // Tìm người chiến thắng (người hết bài đầu tiên)
        Player winner = game.getPlayers().stream()
                .filter(p -> p.getHand().isEmpty())
                .findFirst()
                .orElse(null);
        
        // Cập nhật trạng thái game (thay vì hiển thị thông báo)
        if (statusLabel != null) {
            String message = (winner != null) ? 
                    "Chúc mừng " + winner.getName() + " đã chiến thắng!" : 
                    "Trò chơi kết thúc!";
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold; -fx-font-size: 1.5em;");
        }
        
        // Vô hiệu hóa các nút điều khiển
        if (playButton != null) playButton.setDisable(true);
        if (passButton != null) passButton.setDisable(true);
    }
}
