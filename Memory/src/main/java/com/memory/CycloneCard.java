package com.memory;

public class CycloneCard extends Card {

    public CycloneCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false); // Ne marque pas de points
    }

    @Override
    public void onMatch(GameManager gameManager) {
        triggerCyclone(gameManager);
    }

    public void triggerCyclone(GameManager gameManager) {
        gameManager.shuffleCards(); // Mélanger les cartes dans GameManager
    }
}