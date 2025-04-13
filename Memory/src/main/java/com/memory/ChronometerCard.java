package com.memory;

public class ChronometerCard extends Card {

    public ChronometerCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
        this.setScoring(false); // Ne marque pas de points
    }

    @Override
    public void onMatch(GameManager gameManager) {
        // Calcul des points bonus en fonction du tour actuel
        int currentTurn = gameManager.getTotalMoves(); // Nombre de tours joués
        int baseBonus = 15000; // Bonus maximum au tour 1
        int decrementPerTurn = 1000; // Réduction de 1000 points par tour

        // Calculer les points bonus en s'assurant qu'ils ne deviennent pas négatifs
        int bonusPoints = Math.max(baseBonus - (decrementPerTurn * (currentTurn - 1)), 0);

        // Ajouter les points bonus au score via GameManager
        gameManager.addPoints(bonusPoints);

        System.out.println("Carte Chronomètre appariée ! Points bonus gagnés : " + bonusPoints);
    }
}