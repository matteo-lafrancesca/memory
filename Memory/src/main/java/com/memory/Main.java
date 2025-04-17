package com.memory;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Main extends Application {
    private GameManager gameManager;
    private Label scoreLabel = new Label("Score: 0");
    private Card firstCard = null;
    private Button firstButton = null;
    private Card secondCard = null;
    private Button secondButton = null;
    private Map<Card, Button> cardButtonMap = new HashMap<>();
    private final String BACK_IMAGE = "/images/carte_dos.png";
    private BorderPane root; // Root pane for the UI
    private GridPane gridPane; // Grid containing the cards

    private void initializeUI(Stage stage) {
        // Initialiser le jeu avec des cartes spéciales
        List<Card> specialCards = new ArrayList<>();
        specialCards.add(new CraneCard(9, "/images/carte_crane.png"));
        specialCards.add(new CraneCard(9, "/images/carte_crane.png"));
        specialCards.add(new CycloneCard(10, "/images/carte_cyclone.png"));
        specialCards.add(new CycloneCard(10, "/images/carte_cyclone.png"));
        specialCards.add(new DemonCard(11, "/images/carte_demon.png"));
        specialCards.add(new DemonCard(11, "/images/carte_demon.png"));
        specialCards.add(new MermaidCard(12, "/images/carte_mermaid.png"));
        specialCards.add(new MermaidCard(12, "/images/carte_mermaid.png"));
        specialCards.add(new TreasureCard(13, "/images/carte_treasure.png"));
        specialCards.add(new TreasureCard(13, "/images/carte_treasure.png"));
        specialCards.add(new ChronometerCard(14, "/images/carte_chronometre.png"));
        specialCards.add(new ChronometerCard(14, "/images/carte_chronometre.png"));
        specialCards.add(new JackpotCard(15, "/images/carte_jackpot.png"));
        specialCards.add(new JackpotCard(15, "/images/carte_jackpot.png"));
        specialCards.add(new EyeCard(16, "/images/carte_eye.png"));
        specialCards.add(new EyeCard(16, "/images/carte_eye.png"));

        gameManager = new GameManager(() -> resetGame());
        gameManager.setUpdateUICallback(this::updateGrid);
        gameManager.initializeGame(8, specialCards);

        cardButtonMap.clear();

        // Créer la grille de cartes
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        int row = 0;
        int col = 0;

        for (Card card : gameManager.getCards()) {
            Button cardButton = new Button();
            cardButton.setGraphic(getCardImage(BACK_IMAGE)); // Par défaut, afficher le dos de la carte
            cardButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); // Rendre le fond transparent
            cardButton.setOnAction(e -> handleCardClick(card, cardButton));

            cardButton.setPrefWidth(200); // Largeur augmentée
            cardButton.setPrefHeight(200); // Hauteur augmentée

            cardButtonMap.put(card, cardButton);

            gridPane.add(cardButton, col, row);
            col++;
            if (col == 8) { // Disposition en grille 6xN
                col = 0;
                row++;
            }
        }

        // Configurer la scène
        root = new BorderPane();
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10;");
        root.setTop(scoreLabel);
        BorderPane.setAlignment(scoreLabel, Pos.CENTER);
        root.setCenter(gridPane);

        Scene scene = new Scene(root, 1920, 1080);

        // Charger le fichier CSS
        scene.getStylesheets().add(getClass().getResource("/com/memory/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(true); // Activer le plein écran
        stage.setTitle("Memory Game");
        stage.show();
    }

    private void updateGrid() {
        for (Card card : gameManager.getCards()) {
            Button cardButton = cardButtonMap.get(card);
            if (cardButton == null) continue;

            if (card.isMatched() || card.isVisible()) {
                cardButton.setGraphic(getCardImage(card.getImage())); // Afficher l'image si visible ou appariée
                cardButton.setDisable(card.isMatched()); // Désactiver si appariée
            } else {
                cardButton.setGraphic(getCardImage(BACK_IMAGE)); // Afficher le dos de la carte
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
        primaryStage.setFullScreen(true);
    }

    private void handleCardClick(Card card, Button cardButton) {
        if (gameManager.isGameOver()) {
            return; // Ne pas permettre d'autres actions si la partie est terminée
        }

        // Empêcher le clic sur une carte déjà appariée ou temporairement visible
        if (card.isMatched() || card.isVisible()) {
            return;
        }

        // Désactiver temporairement tous les boutons pour empêcher d'autres clics
        setGridButtonsDisabled(true);

        // Rendre la carte temporairement visible
        card.setVisible(true);
        cardButton.setGraphic(getCardImage(card.getImage())); // Afficher l'image de la carte
        cardButton.setDisable(true); // Désactiver temporairement le bouton pour éviter un double clic

        // Si aucune première carte n'a été sélectionnée
        if (firstCard == null) {
            firstCard = card;
            firstButton = cardButton;

            // Réactiver les boutons immédiatement car aucune vérification n'est nécessaire
            setGridButtonsDisabled(false);
        }
        // Si une première carte a été sélectionnée, gérer la deuxième carte
        else if (secondCard == null && card != firstCard) {
            secondCard = card;
            secondButton = cardButton;

            // Appeler directement `checkCards` sans délai ici
            checkCards();
        }
    }

    private void setGridButtonsDisabled(boolean disabled) {
        for (Button button : cardButtonMap.values()) {
            button.setDisable(disabled);
        }
    }

    private void checkCards() {
        if (firstCard == null || secondCard == null || firstButton == null || secondButton == null) {
            resetCards(); // Réinitialiser pour éviter d'autres erreurs
            setGridButtonsDisabled(false); // Réactiver les boutons
            return;
        }

        if (gameManager.checkPair(firstCard, secondCard)) {
            // Les cartes correspondent
            firstButton.setDisable(true);
            secondButton.setDisable(true);
            scoreLabel.setText("Score: " + gameManager.getScore());
            resetCards(); // Réinitialiser les cartes sélectionnées

            // Vérifier si le jeu est terminé
            if (gameManager.isGameFinished()) {
                showEndGameMessage("Félicitations ! Vous avez gagné !");
            }

            // Réactiver les boutons immédiatement
            setGridButtonsDisabled(false);
        } else {
            // Les cartes ne correspondent pas
            if (gameManager.isGameOver()) {
                showEndGameMessage("Vous avez perdu !");
                setGridButtonsDisabled(false); // Réactiver les boutons avant de quitter
                return;
            }

            // Masquer les cartes après un délai de 1 seconde
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                firstCard.setVisible(false);
                secondCard.setVisible(false);
                firstButton.setGraphic(getCardImage(BACK_IMAGE)); // Retourner la carte (dos)
                secondButton.setGraphic(getCardImage(BACK_IMAGE)); // Retourner la carte (dos)
                firstButton.setDisable(false); // Réactiver les boutons des cartes
                secondButton.setDisable(false);
                resetCards();

                // Réactiver les boutons après la vérification
                setGridButtonsDisabled(false);
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

    private ImageView getCardImage(String imagePath) {
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            throw new IllegalArgumentException("L'image " + imagePath + " est introuvable !");
        }
        Image image = new Image(imageStream);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Ajuster la largeur
        imageView.setFitHeight(200); // Ajuster la hauteur
        imageView.setPreserveRatio(true); // Conserver les proportions
        return imageView;
    }

    private void showEndGameMessage(String message) {
        // Créer un conteneur pour afficher le message et un bouton
        VBox endGameContainer = new VBox(20);
        endGameContainer.setAlignment(Pos.CENTER);

        Label endGameLabel = new Label(message);
        endGameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button restartButton = new Button("Recommencer");
        restartButton.setOnAction(e -> resetGame());

        endGameContainer.getChildren().addAll(endGameLabel, restartButton);

        // Afficher le conteneur dans la zone centrale
        root.setCenter(endGameContainer);
    }

    public static void main(String[] args) {
        launch(args);
    }
}