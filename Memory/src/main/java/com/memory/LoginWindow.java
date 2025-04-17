package com.memory;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Création de la grille pour l'interface
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Champs de saisie
        Label userLabel = new Label("Nom d'utilisateur:");
        grid.add(userLabel, 0, 1);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Mot de passe:");
        grid.add(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        // Boutons d'action
        Button signInButton = new Button("Se connecter");
        grid.add(signInButton, 1, 3);
        Button signUpButton = new Button("S'inscrire");
        grid.add(signUpButton, 1, 4);

        Label messageLabel = new Label();
        grid.add(messageLabel, 1, 5);

        // Action pour le bouton "S'inscrire"
        signUpButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Les champs ne peuvent pas être vides.");
                return;
            }

            if (registerUser(username, password)) {
                messageLabel.setText("Inscription réussie ! Connectez-vous.");
            } else {
                messageLabel.setText("Erreur : nom d'utilisateur déjà existant.");
            }
        });

        // Action pour le bouton "Se connecter"
        signInButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Les champs ne peuvent pas être vides.");
                return;
            }

            if (authenticateUser(username, password)) {
                messageLabel.setText("Connexion réussie !");
                // Lancer le jeu après connexion
                primaryStage.close();
                launchGame(username);
            } else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            }
        });

        // Affichage de la fenêtre
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setTitle("Authentification");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO Utilisateurs (nom, mot_de_passe) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // NOTE : le mot de passe devrait être haché pour des raisons de sécurité
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Code erreur MySQL pour "Duplicate entry"
                System.err.println("Nom d'utilisateur déjà existant : " + username);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM Utilisateurs WHERE nom = ? AND mot_de_passe = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // NOTE : comparer avec un mot de passe haché
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retourne true si l'utilisateur est trouvé

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void launchGame(String username) {
        // Démarrez votre jeu ici
        System.out.println("Bienvenue " + username + "! Le jeu démarre...");
        // Exemple : nouvelle fenêtre ou lancement de la logique principale
    }

    public static void main(String[] args) {
        launch(args);
    }
}