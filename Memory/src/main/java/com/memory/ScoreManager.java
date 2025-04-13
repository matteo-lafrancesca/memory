package com.memory;

public class ScoreManager {
    private final int baseScore = 5000; // Score brut de base pour chaque appariement
    private final double coefficientTemps = 100.0; // Coefficient pour ajuster l'impact du temps
    private final double coefficientMouvements = 1.0; // Coefficient pour ajuster l'impact des mouvements
    private final double adjustmentFactor = 1.5; // Facteur d'ajustement global pour ralentir la décroissance

    public int calculateScore(int totalMoves, long elapsedTime) {
        // Calcul des facteurs de pénalité
        double moveFactor = Math.log(totalMoves + 1) * coefficientMouvements;
        double timeFactor = Math.log(elapsedTime + 1) / coefficientTemps;

        // Appliquer la formule
        double score = (baseScore / (1 + moveFactor + timeFactor)) * adjustmentFactor;

        return (int) Math.max(score, 100); // Assurer un score minimum de 100 points
    }
}