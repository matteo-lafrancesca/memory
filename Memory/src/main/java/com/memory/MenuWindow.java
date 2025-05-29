package com.memory;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuWindow {

    private final int userId;

    public MenuWindow(int userId) {
        this.userId = userId;
    }

    public void start(Stage primaryStage) {
        Label title = new Label(" Memomix ");
        title.getStyleClass().add("pirate-title");

        Button playButton = new Button("🏴‍☠️ Jouer");
        Button rankingButton = new Button("🏆 Classement");
        Button quitButton = new Button("🚪 Quitter");

        playButton.getStyleClass().add("pirate-button");
        rankingButton.getStyleClass().add("pirate-button");
        quitButton.getStyleClass().add("pirate-button");

        playButton.setOnAction(e -> {
            Stage gameStage = new Stage();
            GameMain gameMain = new GameMain(userId);
            gameMain.start(gameStage);
            primaryStage.close();
        });

        rankingButton.setOnAction(e -> {
            Stage classementStage = new Stage();
            new ClassementWindow().start(classementStage);
        });

        quitButton.setOnAction(e -> {
            primaryStage.close();
        });

        VBox vbox = new VBox(50, title, playButton, rankingButton, quitButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 1536, 900);
        scene.getStylesheets().add(getClass().getResource("/com/memory/pirate.css").toExternalForm());

        primaryStage.setTitle("Memomix - Menu du Pirate");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}