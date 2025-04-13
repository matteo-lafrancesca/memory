package com.memory;

public class Card {
    public enum CardType {
        CLASSIC, SPECIAL
    }

    private int id;
    private String image;
    private boolean isMatched; // Indique si la carte est appariée définitivement
    private boolean isVisible; // Indique si la carte est temporairement visible
    private CardType type;
    private boolean scoring; // Indique si la carte marque des points

    public Card(int id, String image, CardType type) {
        this.id = id;
        this.image = image;
        this.isMatched = false;
        this.isVisible = false; // Par défaut, les cartes ne sont pas visibles
        this.type = type;
        this.scoring = true; // Par défaut, les cartes classiques marquent des points
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public CardType getType() {
        return type;
    }

    public boolean isScoring() {
        return scoring;
    }

    public void setScoring(boolean scoring) {
        this.scoring = scoring;
    }

    public void onMatch(GameManager gameManager) {
        // Par défaut, rien à faire pour les cartes classiques
    }
}