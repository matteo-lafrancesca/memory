package com.memory;

import java.sql.*;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/db/memory_game.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase() {
        String createUserTableQuery = """
            CREATE TABLE IF NOT EXISTS Utilisateurs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT UNIQUE NOT NULL,
                mot_de_passe TEXT NOT NULL
            );
        """;

        String createScoreTableQuery = """
        CREATE TABLE IF NOT EXISTS score (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            points INTEGER NOT NULL,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES Utilisateurs(id)
        );
    """;

        String createClassementTableQuery = """
        CREATE TABLE IF NOT EXISTS classement (
            user_id INTEGER PRIMARY KEY,
            best_score INTEGER DEFAULT 0,
            total_score INTEGER DEFAULT 0,
            FOREIGN KEY (user_id) REFERENCES Utilisateurs(id)
        );
    """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableQuery);
            stmt.execute(createScoreTableQuery);
            stmt.execute(createClassementTableQuery);
            System.out.println("Base de données initialisée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveScore(int userId, int points) {
        String query = "INSERT INTO score (user_id, points) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, points);
            stmt.executeUpdate();
            System.out.println("Score ajouté avec succès pour l'utilisateur : " + userId);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du score : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateClassement(int userId, int points) {
        String selectQuery = "SELECT best_score, total_score FROM classement WHERE user_id = ?";
        String insertOrUpdateQuery = """
        INSERT INTO classement (user_id, best_score, total_score)
        VALUES (?, ?, ?)
        ON CONFLICT(user_id) DO UPDATE SET
            best_score = CASE WHEN excluded.best_score > best_score THEN excluded.best_score ELSE best_score END,
            total_score = total_score + excluded.total_score;
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement insertOrUpdateStmt = conn.prepareStatement(insertOrUpdateQuery)) {

            // Vérifier si l'utilisateur existe déjà dans le classement
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();

            int bestScore = points;
            int totalScore = points;

            if (rs.next()) {
                bestScore = Math.max(points, rs.getInt("best_score"));
                totalScore = rs.getInt("total_score") + points;
            }

            // Insérer ou mettre à jour le classement
            insertOrUpdateStmt.setInt(1, userId);
            insertOrUpdateStmt.setInt(2, bestScore);
            insertOrUpdateStmt.setInt(3, points); // Ajouter uniquement les nouveaux points
            insertOrUpdateStmt.executeUpdate();

            System.out.println("Classement mis à jour pour l'utilisateur : " + userId);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du classement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase(); // Initialiser la base de données lors du test
    }
}