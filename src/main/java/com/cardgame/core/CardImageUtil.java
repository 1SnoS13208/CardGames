package com.cardgame.core;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardImageUtil {
    public static ImageView createCardBackImageView() {
        ImageView view = new ImageView(new Image(CardImageUtil.class.getResourceAsStream("/cards/back.png")));
        view.setFitWidth(120);
        view.setFitHeight(180);
        return view;
    }

    public static ImageView createCardImageView(Card card) {
        String imagePath = "/cards/" + card.getImageFileName();
        ImageView view = new ImageView(new Image(CardImageUtil.class.getResourceAsStream(imagePath)));
        view.setFitWidth(120);
        view.setFitHeight(180);
        return view;
    }
}
