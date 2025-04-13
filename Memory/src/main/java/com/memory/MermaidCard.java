package com.memory;

public class MermaidCard extends Card {

    public MermaidCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false); // Ne marque pas de points
    }

    @Override
    public void onMatch(GameManager gameManager) {
        triggerMermaidConstraint(gameManager);
    }

    public void triggerMermaidConstraint(GameManager gameManager) {
        gameManager.setPendingMermaidConstraint(true); // Activer la contrainte de la Sirène
        System.out.println("La contrainte de la Sirène est activée ! Vous devez réussir à relier deux cartes identiques.");
    }
}