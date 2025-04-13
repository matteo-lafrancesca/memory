package com.memory;

public class DemonCard extends Card {

    public DemonCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false); // Ne marque pas de points
    }

    @Override
    public void onMatch(GameManager gameManager) {
        triggerScoreHalving(gameManager);
    }

    public void triggerScoreHalving(GameManager gameManager) {
        int currentScore = gameManager.getScore();
        gameManager.setScore(currentScore / 2); // Divise le score par 2
    }
}