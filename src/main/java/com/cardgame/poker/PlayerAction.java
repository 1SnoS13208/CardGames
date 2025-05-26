package com.cardgame.poker;

/**
 * Represents an action a player can take during a poker game
 */
public class PlayerAction {
    private final ActionType type;
    private final int amount;

    /**
     * Creates a new player action
     * 
     * @param type The type of action
     * @param amount The amount associated with the action (for bets/raises)
     */
    public PlayerAction(ActionType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    /**
     * Creates a new player action with no amount (for fold/check)
     * 
     * @param type The type of action
     */
    public PlayerAction(ActionType type) {
        this(type, 0);
    }

    /**
     * Gets the action type
     * 
     * @return The action type
     */
    public ActionType getType() {
        return type;
    }

    /**
     * Gets the amount associated with the action
     * 
     * @return The action amount
     */
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return type + (amount > 0 ? " " + amount : "");
    }
}

/**
 * Represents the type of action a player can take
 */
enum ActionType {
    FOLD,       // Fold the hand
    CHECK,      // Check (no bet)
    CALL,       // Call the current bet
    RAISE,      // Raise the current bet
    ALL_IN      // Bet all remaining chips
}
