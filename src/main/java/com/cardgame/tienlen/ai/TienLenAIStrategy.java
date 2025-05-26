package com.cardgame.tienlen.ai;

import com.cardgame.core.Card;
import com.cardgame.tienlen.TienLenHandEvaluator;

import java.util.List;

/**
 * Interface for defining AI behavior in the Tien Len game.
 */
public interface TienLenAIStrategy {
    /**
     * Decides which cards the AI should play.
     *
     * @param hand The AI player's current hand of cards.
     * @param currentPile The list of cards currently on the table from the last play.
     * @param evaluator The TienLenHandEvaluator to validate hand types and moves.
     * @param isFirstMove True if this is the first move of the entire game.
     * @param mustPlayThreeOfSpades True if the current game state (4 players, first move) requires the 3 of Spades to be played if held.
     * @return A list of cards to play. Returns an empty list if the AI decides to pass or has no valid move.
     */
    List<Card> chooseCardsToPlay(List<Card> hand, List<Card> currentPile, TienLenHandEvaluator evaluator, boolean isFirstMove, boolean mustPlayThreeOfSpades);
}
