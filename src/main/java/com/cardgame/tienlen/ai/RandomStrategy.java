package com.cardgame.tienlen.ai;

import com.cardgame.core.Card;
import com.cardgame.tienlen.TienLenHandEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple AI strategy that makes random valid moves.
 */
public class RandomStrategy implements TienLenAIStrategy {
    private Random random = new Random();

    @Override
    public List<Card> chooseCardsToPlay(List<Card> hand, List<Card> currentPile, TienLenHandEvaluator evaluator, boolean isFirstMove, boolean mustPlayThreeOfSpades) {
        List<List<Card>> possibleMoves = findAllPossibleValidMoves(hand, currentPile, evaluator, isFirstMove, mustPlayThreeOfSpades);

        if (possibleMoves.isEmpty()) {
            return new ArrayList<>(); // Pass if no valid moves
        }

        // If there's a current pile, AI can choose to pass randomly (e.g., 30% chance to pass if it can play)
        // However, it must play if the currentPile is empty (it's leading the new round)
        if (!currentPile.isEmpty() && random.nextInt(10) < 3) { // 30% chance to pass
            return new ArrayList<>(); // Pass
        }
        
        // Otherwise, pick a random valid move from the generated list
        return possibleMoves.get(random.nextInt(possibleMoves.size()));
    }

    private List<List<Card>> findAllPossibleValidMoves(List<Card> hand, List<Card> currentPile, TienLenHandEvaluator evaluator, boolean isFirstMove, boolean mustPlayThreeOfSpades) {
        List<List<Card>> allValidMoves = new ArrayList<>();
        int n = hand.size();
        if (n == 0) return allValidMoves;

        // Iterate over all possible non-empty subsets of the hand
        for (int i = 1; i < (1 << n); i++) {
            List<Card> potentialPlay = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    potentialPlay.add(hand.get(j));
                }
            }

            // Sort the potential play for consistent evaluation by TienLenHandEvaluator
            Collections.sort(potentialPlay, (c1, c2) -> {
                int rank1 = evaluator.getTienLenRank(c1);
                int rank2 = evaluator.getTienLenRank(c2);
                if (rank1 != rank2) return rank1 - rank2;
                return c1.getSuitOrder() - c2.getSuitOrder();
            });

            boolean isValidForFirstMove = true;
            if (isFirstMove && mustPlayThreeOfSpades) {
                boolean playerHasThreeOfSpadesInHand = hand.stream()
                    .anyMatch(card -> card.getRank().equals("3") && card.getSuit().equalsIgnoreCase("Spades"));
                boolean playedThreeOfSpades = potentialPlay.stream()
                    .anyMatch(card -> card.getRank().equals("3") && card.getSuit().equalsIgnoreCase("Spades"));

                if (playerHasThreeOfSpadesInHand && !playedThreeOfSpades) {
                    isValidForFirstMove = false; // Must play 3 of Spades if held on first move (4 players)
                }
                if (!playerHasThreeOfSpadesInHand && !potentialPlay.isEmpty() && currentPile.isEmpty()){
                    // If player doesn't have 3 of spades, any valid starting hand is fine (this condition is for the starting player)
                    // This specific check for 3 of spades is more about *forcing* its play if held.
                    // The general validity of the hand (e.g. being a single, pair etc.) is checked by evaluator.isValidMove
                }
            }

            if (isValidForFirstMove && evaluator.isValidMove(potentialPlay, currentPile)) {
                allValidMoves.add(potentialPlay);
            }
        }
        return allValidMoves;
    }
}
