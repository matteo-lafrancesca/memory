package com.memory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // URL de connexion à votre base de données MySQL
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/memory_game"; // Remplacez "memory_game" par le nom de votre base de données
    private static final String DATABASE_USER = "root"; // Nom d'utilisateur MySQL (par défaut avec XAMPP : "root")
    private static final String DATABASE_PASSWORD = ""; // Mot de passe MySQL (par défaut avec XAMPP : vide)

    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // Méthode principale pour tester la connexion
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn != null) {
                System.out.println("Connexion à la base de données réussie !");
            }
        } catch (SQLException e) {
            System.err.println("Échec de la connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }
}