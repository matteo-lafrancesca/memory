package com.memory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassementWindow {

    public static class ClassementRow {
        private final int rang;
        private final String nom;
        private final int bestScore;
        private final int totalScore;

        public ClassementRow(int rang, String nom, int bestScore, int totalScore) {
            this.rang = rang;
            this.nom = nom;
            this.bestScore = bestScore;
            this.totalScore = totalScore;
        }

        public int getRang() { return rang; }
        public String getNom() { return nom; }
        public int getBestScore() { return bestScore; }
        public int getTotalScore() { return totalScore; }
    }

    public void start(Stage stage) {
        TableView<ClassementRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ClassementRow, Integer> rangCol = new TableColumn<>("🏅 Rang");
        rangCol.setCellValueFactory(new PropertyValueFactory<>("rang"));
        rangCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ClassementRow, String> nomCol = new TableColumn<>("👤 Joueur");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<ClassementRow, Integer> bestCol = new TableColumn<>("🌟 Meilleur score");
        bestCol.setCellValueFactory(new PropertyValueFactory<>("bestScore"));
        bestCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ClassementRow, Integer> totalCol = new TableColumn<>("💰 Total des scores");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        totalCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(rangCol, nomCol, bestCol, totalCol);

        ObservableList<ClassementRow> data = getClassement();
        table.setItems(data);

        Button closeButton = new Button("Retour");
        closeButton.setOnAction(e -> stage.close());
        closeButton.getStyleClass().add("pirate-button");

        HBox buttons = new HBox(closeButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(buttons);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/memory/styles.css").toExternalForm());

        stage.setTitle("🏆 Classement des Pirates");
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<ClassementRow> getClassement() {
        ObservableList<ClassementRow> list = FXCollections.observableArrayList();
        String query = """
            SELECT U.nom, C.best_score, C.total_score
            FROM classement C
            JOIN Utilisateurs U ON U.id = C.user_id
            ORDER BY C.total_score DESC, C.best_score DESC
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int rang = 1;
            while (rs.next()) {
                String nom = rs.getString("nom");
                int best = rs.getInt("best_score");
                int total = rs.getInt("total_score");
                list.add(new ClassementRow(rang++, nom, best, total));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du classement : " + e.getMessage());
        }
        return list;
    }
}