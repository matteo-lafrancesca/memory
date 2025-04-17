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

    // Constructeur pour les cartes classiques (chemin d'image généré automatiquement)
    public Card(int id, CardType type) {
        this.id = id;
        this.type = type;
        this.isMatched = false;
        this.isVisible = false; // Par défaut, les cartes ne sont pas visibles
        this.scoring = true; // Par défaut, les cartes classiques marquent des points

        // Générer automatiquement l'image pour les cartes classiques
        if (type == CardType.CLASSIC) {
            this.image = generateClassicCardImage(id);
        } else {
            this.image = null; // Les cartes spéciales doivent définir leur image manuellement
        }
    }

    // Constructeur pour les cartes spéciales (chemin d'image spécifié manuellement)
    public Card(int id, String image, CardType type) {
        this.id = id;
        this.image = image;
        this.type = type;
        this.isMatched = false;
        this.isVisible = false;
        this.scoring = true; // Par défaut, les cartes marquent des points
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

    // Générer le chemin de l'image pour une carte classique
    private static String generateClassicCardImage(int id) {
        return "/images/carte_classique" + id + ".png";
    }

    public void onMatch(GameManager gameManager) {
        // Par défaut, rien à faire pour les cartes classiques
    }
}