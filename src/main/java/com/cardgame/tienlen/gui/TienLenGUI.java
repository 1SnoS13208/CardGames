package com.cardgame.tienlen.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class TienLenGUI extends Application{
    @Override
    public void start(Stage arg0) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TienLenGUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        arg0.setTitle("Tiến Lên Card Game");
        arg0.setScene(scene);
        arg0.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
