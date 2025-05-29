package com.memory;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Lancer l'écran de connexion
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.start(primaryStage);

        // Ajouter un écouteur pour la transition après la connexion
        primaryStage.setOnHidden(event -> {
            int userId = LoginWindow.getUserId();

            if (userId != -1) {
                // Si connecté, lancer le menu principal
                Stage menuStage = new Stage();
                MenuWindow menuWindow = new MenuWindow(userId);
                menuWindow.start(menuStage);
            } else {
                System.out.println("Aucun utilisateur connecté. Fermeture du programme.");
            }
        });
    }

    public static void main(String[] args) {
        // Initialiser la base de données
        DatabaseManager.initializeDatabase();
        launch(args); // Lancer l'application JavaFX
    }
}