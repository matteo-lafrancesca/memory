package com.memory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow {

    private static int userId = -1; // ID utilisateur par défaut (non connecté)
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label messageLabel = new Label();

    public void start(Stage primaryStage) {
        // Configuration de la grille pour l'interface
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Champs de saisie
        grid.add(new Label("Nom d'utilisateur:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Mot de passe:"), 0, 2);
        grid.add(passwordField, 1, 2);

        // Boutons d'action
        Button signInButton = new Button("Se connecter");
        Button signUpButton = new Button("S'inscrire");
        grid.add(signInButton, 1, 3);
        grid.add(signUpButton, 1, 4);
        grid.add(messageLabel, 1, 5);

        // Actions des boutons
        signInButton.setOnAction(e -> handleSignIn(primaryStage));
        signUpButton.setOnAction(e -> handleSignUp());

        // Affichage de la fenêtre
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setTitle("Authentification");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSignIn(Stage stage) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Les champs ne peuvent pas être vides.");
            return;
        }

        userId = authenticateUser(username, password);
        if (userId != -1) {
            stage.close(); // Fermer la fenêtre une fois connecté
        } else {
            messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Les champs ne peuvent pas être vides.");
            return;
        }

        if (registerUser(username, password)) {
            messageLabel.setText("Inscription réussie ! Connectez-vous.");
        } else {
            messageLabel.setText("Erreur : nom d'utilisateur déjà existant.");
        }
    }

    private boolean registerUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO Utilisateurs (nom, mot_de_passe) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Nom d'utilisateur déjà existant : " + username);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    private int authenticateUser(String username, String password) {
        String query = "SELECT id, mot_de_passe FROM Utilisateurs WHERE nom = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return rs.getInt("id"); // Retourner l'ID utilisateur
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Échec de la connexion
    }

    public static int getUserId() {
        return userId;
    }
}