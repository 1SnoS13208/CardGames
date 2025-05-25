package com.cardgame.blackjack.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BlackJackGUI extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BlackJack.fxml"));
        Scene scene = new Scene(root, 900, 600);

        double baseWidth = 1920;
        double baseHeight = 1080;

        javafx.beans.binding.DoubleBinding scale = javafx.beans.binding.Bindings.createDoubleBinding(
            () -> Math.min(scene.getWidth() / baseWidth, scene.getHeight() / baseHeight),
            scene.widthProperty(), scene.heightProperty()
        );
        root.scaleXProperty().bind(scale);
        root.scaleYProperty().bind(scale);

        stage.setTitle("Blackjack");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
