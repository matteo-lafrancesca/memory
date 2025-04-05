package com.memory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDatabaseApp {

    // Création d'un logger pour la classe
    private static final Logger logger = Logger.getLogger(MyDatabaseApp.class.getName());

    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Exemple de requête
            String query = "SELECT * FROM Joueurs";  // Note la capitalisation de "Joueurs"
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println("Nom du joueur: " + rs.getString("nom") + ", Score: " + rs.getInt("score_total"));
                }
            }
        } catch (SQLException e) {
            // Utilisation du logger pour afficher l'exception
            logger.log(Level.SEVERE, "Database connection or query failed", e);
        }
    }
}
