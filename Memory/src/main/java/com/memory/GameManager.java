package com.memory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class GameManager {
    private List<Card> cards;
    private int score;
    private boolean gameOver;
    private int totalMoves; // Nombre total de mouvements effectués
    private int jackpotRemainingMoves = 0; // Compteur pour le bonus Jackpot
    private Instant startTime; // Heure de début de la partie
    private ScoreManager scoreManager;
    private boolean pendingMermaidConstraint; // Indique si une contrainte Sirène est en attente
    private GameResetCallback resetCallback; // Callback pour réinitialiser la partie
    private Runnable updateUI; // Callback pour mettre à jour l'UI
    private boolean isTreasureMatch;


    public GameManager(GameResetCallback resetCallback) {
        this.cards = new ArrayList<>();
        this.score = 0;
        this.gameOver = false;
        this.totalMoves = 0;
        this.scoreManager = new ScoreManager();
        this.pendingMermaidConstraint = false; // Par défaut, pas de contrainte active
        this.resetCallback = resetCallback; // Attacher le callback pour réinitialiser la partie
    }

    public void activateJackpotBonus() {
        this.jackpotRemainingMoves = 3; // Activer le bonus pour les 3 prochains mouvements
    }

    public void initializeGame(int numClassicPairs, List<Card> specialCards) {
        // Reset the game state
        cards.clear();
        score = 0;
        gameOver = false;
        totalMoves = 0;
        startTime = Instant.now(); // Enregistrer l'heure de début de la partie
        pendingMermaidConstraint = false; // Réinitialiser la contrainte Sirène
        jackpotRemainingMoves = 0; // Réinitialiser le bonus Jackpot

        // Initialize classic cards
        for (int i = 1; i <= numClassicPairs; i++) {
            cards.add(new Card(i, "image" + i + ".png", Card.CardType.CLASSIC));
            cards.add(new Card(i, "image" + i + ".png", Card.CardType.CLASSIC));
        }

        // Add special cards
        if (specialCards != null) {
            cards.addAll(specialCards);
        }

        // Shuffle all cards
        shuffleCards();
    }

    public void shuffleCards() {
        // Filtrer uniquement les cartes non appariées
        List<Card> nonMatchedCards = cards.stream()
                .filter(card -> !card.isMatched()) // Garder uniquement les cartes non appariées
                .collect(Collectors.toList());

        // Mélanger les cartes non appariées
        Collections.shuffle(nonMatchedCards);

        // Réinjecter les cartes mélangées dans la liste principale
        int index = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (!cards.get(i).isMatched()) {
                cards.set(i, nonMatchedCards.get(index));
                index++;
            }
        }
    }

    public boolean checkPair(Card card1, Card card2) {
        totalMoves++; // Incrémenter le compteur de mouvements

        // Vérifier si les deux cartes sont appariées
        if (card1.getId() == card2.getId()) {
            card1.setMatched(true);
            card2.setMatched(true);

            // Appeler les comportements spécifiques des cartes
            card1.onMatch(this);

            // Calcul des points
            if (card1.isScoring() && card2.isScoring()) {
                long elapsedTime = Instant.now().getEpochSecond() - startTime.getEpochSecond();

                // Vérifier si une des cartes est une carte Trésor
                boolean isTreasureMatch = (card1 instanceof TreasureCard) || (card2 instanceof TreasureCard);

                // Calculer les points de base avec ScoreManager
                int points = scoreManager.calculateScore(totalMoves, elapsedTime);

                // Appliquer le bonus Trésor
                if (isTreasureMatch) {
                    points *= 5; // Multiplier les points par 5 si une carte Trésor est appariée
                    System.out.println("Bonus Trésor appliqué ! Points multipliés par 5.");
                }

                // Ajouter les points au score total
                score += points;
                System.out.println("Points gagnés : " + points + " | Score total : " + score);
            }

            return true; // Succès
        }

        // Si les cartes ne sont pas appariées, pas de points ajoutés
        System.out.println("Pas de match. Prochain mouvement.");
        return false; // Échec
    }

    public boolean isGameFinished() {
        for (Card card : cards) {
            if (card.getType() == Card.CardType.CLASSIC && !card.isMatched()) {
                return false; // La partie continue
            }
        }

        // Si toutes les cartes classiques sont appariées, la partie est terminée
        System.out.println("Félicitations ! Vous avez découvert toutes les cartes classiques !");
        return true;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPendingMermaidConstraint() {
        return pendingMermaidConstraint;
    }

    public void setPendingMermaidConstraint(boolean pendingMermaidConstraint) {
        this.pendingMermaidConstraint = pendingMermaidConstraint;
    }

    public void setUpdateUICallback(Runnable updateUI) {
        this.updateUI = updateUI;
    }

    public void activateEyeEffect() {
        // Étape 1 : Filtrer les cartes non appariées
        List<Card> hiddenCards = new ArrayList<>();
        for (Card card : cards) {
            if (!card.isMatched() && !card.isVisible()) {
                hiddenCards.add(card);
            }
        }

        // Étape 2 : Mélanger les cartes non appariées
        Collections.shuffle(hiddenCards);

        // Étape 3 : Sélectionner jusqu'à 5 cartes
        List<Card> cardsToReveal = hiddenCards.subList(0, Math.min(5, hiddenCards.size()));

        // Étape 4 : Rendre ces cartes temporairement visibles
        for (Card card : cardsToReveal) {
            card.setVisible(true); // Rendre la carte visible
        }

        // Mise à jour immédiate de l'interface
        if (updateUI != null) {
            Platform.runLater(updateUI); // Actualiser l'interface sur le thread JavaFX
        }

        // Planifier une tâche pour réinitialiser leur état après 3 secondes
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    for (Card card : cardsToReveal) {
                        card.setVisible(false); // Rendre la carte invisible à nouveau
                    }

                    // Mise à jour de l'interface après la réinitialisation
                    if (updateUI != null) {
                        updateUI.run();
                    }

                    System.out.println("Les cartes révélées temporairement sont maintenant cachées.");
                });
            }
        }, 2000);
    }

    // Interface pour réinitialiser le jeu
    public interface GameResetCallback {
        void resetGame();
    }

    public void activateTreasureBonus() {
        System.out.println("Bonus Trésor activé ! Les points du prochain appariement seront multipliés par 5.");
        this.isTreasureMatch = true; // Activer le multiplicateur spécial
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void addPoints(int points) {
        score += points;
        System.out.println("Score actuel : " + score);
    }
}