// MemoryGame.java - Main Application Class
package com.example.memory;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.memory.controller.GameController;

public class MemoryGame extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameController gameController = new GameController(primaryStage);
        gameController.showStartScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
