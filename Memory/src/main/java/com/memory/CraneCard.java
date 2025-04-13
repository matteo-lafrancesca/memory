package com.memory;

public class CraneCard extends Card {

    public CraneCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false); // Ne marque pas de points
    }

    @Override
    public void onMatch(GameManager gameManager) {
        triggerLoss(gameManager);
    }

    public void triggerLoss(GameManager gameManager) {
        gameManager.setGameOver(true); // Déclenche la fin de partie immédiatement
    }
}