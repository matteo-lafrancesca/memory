package com.memory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML
    private Button jouerBtn;

    @FXML
    private Button classementBtn;

    @FXML
    public void initialize() {
        jouerBtn.setOnAction(e -> {
            System.out.println("Jouer !");
            // TODO : changer de scène
        });

        classementBtn.setOnAction(e -> {
            System.out.println("Classement !");
            // TODO : afficher classement
        });
    }
}
