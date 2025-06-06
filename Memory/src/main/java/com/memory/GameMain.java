package com.memory;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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

/**
 * Fenêtre principale du jeu. Ajoute le CSS pirate, un bouton "Menu" à gauche du score et un bouton "Quitter" à droite du score.
 * Les deux boutons arrêtent la partie : "Menu" retourne au menu principal, "Quitter" ferme le programme.
 */
public class GameMain extends Application {
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
    private int userId; // ID utilisateur

    // Constructeur par défaut requis par JavaFX
    public GameMain() {
    }

    // Constructeur pour initialiser l'ID utilisateur
    public GameMain(int userId) {
        this.userId = userId;
    }

    // Setter pour définir l'ID utilisateur si le constructeur par défaut est utilisé
    public void setUserId(int userId) {
        this.userId = userId;
    }

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

        gameManager = new GameManager(userId, this::resetGame);
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
            cardButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            cardButton.setOnAction(e -> handleCardClick(card, cardButton));

            cardButton.setPrefWidth(200);
            cardButton.setPrefHeight(200);

            cardButtonMap.put(card, cardButton);

            gridPane.add(cardButton, col, row);
            col++;
            if (col == 8) {
                col = 0;
                row++;
            }
        }

        // Barre du haut : Menu | Score | Quitter
        Button menuButton = new Button("☰ Menu");
        menuButton.getStyleClass().add("pirate-button");
        menuButton.setOnAction(e -> {
            // Arrête la partie et retourne au menu principal
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage menuStage = new Stage();
            new MenuWindow(userId).start(menuStage);
        });

        Button quitButton = new Button("Quitter");
        quitButton.getStyleClass().add("pirate-button");
        quitButton.setOnAction(e -> {
            // Arrête la partie et ferme l'application
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            System.exit(0);
        });

        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10;");
        scoreLabel.getStyleClass().add("pirate-title"); // Pour un style bien visible

        HBox topBar = new HBox(40, menuButton, scoreLabel, quitButton);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-padding: 25px 0;");

        // Layout principal
        root = new BorderPane();

        // Charger l'image de fond
        Image backgroundImage = new Image(getClass().getResource("/images/game_background.png").toExternalForm());
        BackgroundImage bImg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(bImg));
        root.setTop(topBar);
        root.setCenter(gridPane);

        Scene scene = new Scene(root, 1920, 1080);

        // Charger le CSS pirate
        scene.getStylesheets().add(getClass().getResource("/com/memory/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setTitle("Memory Game");
        stage.show();
    }

    private void updateGrid() {
        for (Card card : gameManager.getCards()) {
            Button cardButton = cardButtonMap.get(card);
            if (cardButton == null) continue;

            if (card.isMatched() || card.isVisible()) {
                cardButton.setGraphic(getCardImage(card.getImage()));
                cardButton.setDisable(card.isMatched());
            } else {
                cardButton.setGraphic(getCardImage(BACK_IMAGE));
                cardButton.setDisable(false);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initializeUI(primaryStage);

        // Arrêter le programme JavaFX lorsque la fenêtre principale est fermée
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de la fenêtre. Arrêt du programme.");
            System.exit(0);
        });
    }

    private void handleCardClick(Card card, Button cardButton) {
        if (gameManager.isGameOver()) {
            return;
        }

        if (card.isMatched() || card.isVisible()) {
            return;
        }

        card.setVisible(true);
        cardButton.setGraphic(getCardImage(card.getImage()));
        cardButton.setDisable(true);

        if (firstCard == null) {
            firstCard = card;
            firstButton = cardButton;
        } else if (secondCard == null && card != firstCard) {
            secondCard = card;
            secondButton = cardButton;
            setGridButtonsDisabled(true);
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
            resetCards();
            setGridButtonsDisabled(false);
            return;
        }

        if (gameManager.checkPair(firstCard, secondCard)) {
            firstButton.setDisable(true);
            secondButton.setDisable(true);
            scoreLabel.setText("Score: " + gameManager.getScore());
            resetCards();

            if (gameManager.isGameFinished()) {
                showEndGameMessage("Félicitations ! Vous avez gagné !");
            }
            setGridButtonsDisabled(false);
        } else {
            if (gameManager.isGameOver()) {
                showEndGameMessage("Vous avez perdu !");
                setGridButtonsDisabled(false);
                return;
            }

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                firstCard.setVisible(false);
                secondCard.setVisible(false);
                firstButton.setGraphic(getCardImage(BACK_IMAGE));
                secondButton.setGraphic(getCardImage(BACK_IMAGE));
                firstButton.setDisable(false);
                secondButton.setDisable(false);
                resetCards();
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
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void showEndGameMessage(String message) {
        VBox endGameContainer = new VBox(20);
        endGameContainer.setAlignment(Pos.CENTER);

        Label endGameLabel = new Label(message);
        endGameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button restartButton = new Button("Recommencer");
        restartButton.getStyleClass().add("pirate-button");
        restartButton.setOnAction(e -> resetGame());

        endGameContainer.getChildren().addAll(endGameLabel, restartButton);
        root.setCenter(endGameContainer);
    }
}