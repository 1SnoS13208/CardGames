package com.cardgame.core;

/**
 * Represents a playing card with a suit, rank, and value
 */
public class Card {
    private final String suit;
    private final String rank;
    private final int value;

    /**
     * Creates a new card
     * 
     * @param suit The suit of the card (Hearts, Diamonds, Clubs, Spades)
     * @param rank The rank of the card (2-10, J, Q, K, A)
     * @param value The numerical value of the card
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public Card(String suit, String rank, int value) {
        if (suit == null || suit.isEmpty()) {
            throw new IllegalArgumentException("Suit cannot be null or empty");
        }
        if (rank == null || rank.isEmpty()) {
            throw new IllegalArgumentException("Rank cannot be null or empty");
        }
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    /**
     * Gets the suit of the card
     * 
     * @return The suit of the card
     */
    public String getSuit() { return suit; }
    
    /**
     * Gets the rank of the card
     * 
     * @return The rank of the card
     */
    public String getRank() { return rank; }
    
    /**
     * Gets the value of the card
     * 
     * @return The value of the card
     */
    public int getValue() { return value; }

    /**
     * Returns a string representation of the card
     * 
     * @return A string representation of the card (e.g., "Ace of Spades")
     */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    /**
     * Returns the image file name for this card (e.g., "AS.png" for Ace of Spades)
     * @return The image file name
     */
    public String getImageFileName() {
        // Map suit to single character
        String suitChar;
        switch (suit.toLowerCase()) {
            case "spades": suitChar = "S"; break;
            case "hearts": suitChar = "H"; break;
            case "diamonds": suitChar = "D"; break;
            case "clubs": suitChar = "C"; break;
            default: suitChar = "?"; break;
        }
        // Map rank (should be "A", "2", ..., "10", "J", "Q", "K")
        String rankShort;
        if (rank.equalsIgnoreCase("Ace")) rankShort = "A";
        else if (rank.equalsIgnoreCase("Jack")) rankShort = "J";
        else if (rank.equalsIgnoreCase("Queen")) rankShort = "Q";
        else if (rank.equalsIgnoreCase("King")) rankShort = "K";
        else rankShort = rank;
        return rankShort + "-" + suitChar + ".png";
    }
}