package com.memory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main extends Application {
    private GameManager gameManager;
    private Label scoreLabel = new Label("Score: 0");
    private Card firstCard = null;
    private Button firstButton = null;
    private Card secondCard = null;
    private Button secondButton = null;
    private Map<Card, Button> cardButtonMap = new HashMap<>();

    private void initializeUI(Stage stage) {
        // Initialiser le jeu avec des cartes spéciales
        List<Card> specialCards = new ArrayList<>();
        specialCards.add(new CraneCard(9, "crane.png"));
        specialCards.add(new CraneCard(9, "crane.png"));
        specialCards.add(new CycloneCard(10, "cyclone.png"));
        specialCards.add(new CycloneCard(10, "cyclone.png"));
        specialCards.add(new DemonCard(11, "demon.png"));
        specialCards.add(new DemonCard(11, "demon.png"));
        specialCards.add(new MermaidCard(12, "mermaid.png"));
        specialCards.add(new MermaidCard(12, "mermaid.png"));
        specialCards.add(new JackpotCard(13, "jackpot.png"));
        specialCards.add(new JackpotCard(13, "jackpot.png"));
        specialCards.add(new EyeCard(14, "eye.png"));
        specialCards.add(new EyeCard(14, "eye.png"));
        specialCards.add(new TreasureCard(15, "treasure.png"));
        specialCards.add(new TreasureCard(15, "treasure.png"));
        specialCards.add(new ChronometerCard(16, "chronometer.png"));
        specialCards.add(new ChronometerCard(16, "chronometer.png"));

        gameManager = new GameManager(() -> resetGame()); // Passer le callback ici
        gameManager.setUpdateUICallback(this::updateGrid); // Configurer le callback pour mettre à jour l'UI
        gameManager.initializeGame(8, specialCards);

        // Réinitialiser la map des boutons
        cardButtonMap.clear();

        // Créer la grille de cartes
        GridPane gridPane = new GridPane();
        int row = 0;
        int col = 0;

        for (Card card : gameManager.getCards()) {
            Button cardButton = new Button("?");
            cardButton.setDisable(false); // Assurez-vous que tous les boutons sont activés
            cardButton.setOnAction(e -> handleCardClick(card, cardButton));

            // Ajouter chaque bouton à la map
            cardButtonMap.put(card, cardButton);

            gridPane.add(cardButton, col, row);
            col++;
            if (col == 4) { // Exemple de disposition en grille
                col = 0;
                row++;
            }
        }

        // Configurer la scène ou mettre à jour l'interface
        VBox root = new VBox(10, scoreLabel, gridPane);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);

        stage.setTitle("Memory Game");
        stage.show();
    }

    private void updateGrid() {
        for (Card card : gameManager.getCards()) {
            Button cardButton = cardButtonMap.get(card); // Récupérer le bouton correspondant
            if (cardButton == null) continue; // Si aucun bouton n'est trouvé, passer

            if (card.isMatched() || card.isVisible()) {
                cardButton.setText(card.getImage()); // Afficher l'image si appariée ou visible
                cardButton.setDisable(card.isMatched()); // Désactiver le bouton si appariée
            } else {
                cardButton.setText("?"); // Afficher le dos de la carte
                cardButton.setDisable(false); // Assurer qu'elle reste cliquable
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initializeUI(primaryStage);

        // Arrêter le programme JavaFX lorsque la fenêtre principale est fermée
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de la fenêtre. Arrêt du programme.");
            System.exit(0); // Terminer le processus Java
        });
    }

    private void handleCardClick(Card card, Button cardButton) {
        if (gameManager.isGameOver()) {
            return; // Ne pas permettre d'autres actions si la partie est terminée
        }

        // Empêcher le clic sur une carte déjà appariée ou temporairement visible
        if (card.isMatched() || card.isVisible()) {
            return;
        }

        // Rendre la carte temporairement visible
        card.setVisible(true);
        updateGrid(); // Forcer l'affichage immédiat

        // Si aucune première carte n'a été sélectionnée
        if (firstCard == null) {
            firstCard = card;
            firstButton = cardButton;
        }
        // Si une première carte a été sélectionnée, gérer la deuxième carte
        else if (secondCard == null && card != firstCard) {
            secondCard = card;
            secondButton = cardButton;
            checkCards(); // Vérifier les appariements
        }
    }

    private void checkCards() {
        if (firstCard == null || secondCard == null || firstButton == null || secondButton == null) {
            resetCards(); // Réinitialiser pour éviter d'autres erreurs
            return;
        }

        if (gameManager.checkPair(firstCard, secondCard)) {
            firstButton.setDisable(true);
            secondButton.setDisable(true);
            scoreLabel.setText("Score: " + gameManager.getScore());
            resetCards();
        } else {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                firstButton.setText("?");
                secondButton.setText("?");
                firstCard.setVisible(false);
                secondCard.setVisible(false);
                resetCards();
            });
            pause.play();
        }
    }

    private void resetCards() {
        firstCard = null;
        firstButton = null;
        secondCard = null;
        secondButton = null;
    }

    private void resetGame() {
        gameManager.setGameOver(false);
        gameManager.initializeGame(8, null);
        initializeUI((Stage) scoreLabel.getScene().getWindow());
        scoreLabel.setText("Score: 0");
        resetCards();
    }

    public static void main(String[] args) {
        launch(args);
    }
}