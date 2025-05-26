package com.cardgame.tienlen;

import com.cardgame.core.Player;
import com.cardgame.tienlen.ai.TienLenAIStrategy;
import com.cardgame.tienlen.ai.RandomStrategy;

public class TienLenPlayer extends Player {
    boolean isAI;
    private TienLenAIStrategy aiStrategy;

    public TienLenPlayer(String name, boolean isAI) {
        super(name);
        this.isAI = isAI;
        if (isAI) {
            // Default to RandomStrategy for AI players
            this.aiStrategy = new RandomStrategy();
        }
    }

    public TienLenPlayer(String name, boolean isAI, TienLenAIStrategy strategy) {
        super(name);
        this.isAI = isAI;
        if (isAI) {
            this.aiStrategy = strategy;
        } else {
            this.aiStrategy = null; // Ensure non-AI players don't have a strategy
        }
    }

    public boolean isAI() {
        return isAI;
    }

    public TienLenAIStrategy getAiStrategy() {
        return aiStrategy;
    }

    public void setAiStrategy(TienLenAIStrategy aiStrategy) {
        if (this.isAI) {
            this.aiStrategy = aiStrategy;
        }
    }
}
