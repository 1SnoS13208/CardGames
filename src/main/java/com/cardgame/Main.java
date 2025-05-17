package com.cardgame;

import com.cardgame.blackjack.BlackjackGame;
import com.cardgame.core.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new BlackjackGame();
        game.start();
    }
}