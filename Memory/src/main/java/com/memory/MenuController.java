package com.memory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MenuController {

    @FXML
    public void handlePlayButtonAction(ActionEvent event) {
        // Affiche un message ou passe à une nouvelle scène pour commencer le jeu
        System.out.println("Le joueur a cliqué sur 'Jouer'.");
        showAlert("Jouer", "Le jeu commence !");
    }

    @FXML
    public void handleRankingButtonAction(ActionEvent event) {
        // Affiche un message ou passe à une nouvelle scène pour voir le classement
        System.out.println("Le joueur a cliqué sur 'Classement'.");
        showAlert("Classement", "Voici le classement des joueurs !");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
