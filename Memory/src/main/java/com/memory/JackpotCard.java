package com.memory;

public class JackpotCard extends Card {
    public JackpotCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false);
    }

    @Override
    public void onMatch(GameManager gameManager) {
        // Activer le bonus Jackpot pour 3 mouvements
        gameManager.activateJackpotBonus();
        System.out.println("Jackpot activé ! Les points pour les 3 prochains mouvements seront doublés.");
    }
}