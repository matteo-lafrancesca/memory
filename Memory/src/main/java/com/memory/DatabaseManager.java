package com.memory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // Détails de la connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/memory";  // Nom de la base de données que tu as créée (par exemple "memomix")
    private static final String USER = "root";  // Utilisateur par défaut de MySQL sous XAMPP
    private static final String PASSWORD = "";  // Mot de passe vide par défaut dans XAMPP (si tu l'as laissé vide)

    public static Connection getConnection() throws SQLException {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Retourner la connexion à la base de données
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Connection failed", e);
        }
    }
}
