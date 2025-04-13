package com.memory;

public class TreasureCard extends Card {

    public TreasureCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
    }

    @Override
    public void onMatch(GameManager gameManager) {
        // Appliquer un bonus spécial de multiplication des points par 5
        gameManager.activateTreasureBonus();
    }
}