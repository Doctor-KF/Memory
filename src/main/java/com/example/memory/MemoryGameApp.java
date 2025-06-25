package com.example.memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class for the Memory Game application.
 * Launches the JavaFX application and loads the start screen.
 */
public class MemoryGameApp extends Application {

    /**
     * Starts the JavaFX application and displays the main window.
     *
     * @param primaryStage The main application window (Stage).
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            setWindowIcon(primaryStage);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            primaryStage.setTitle("Memory Game");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the window icon for the application.
     *
     * @param stage The Stage for which the icon should be set.
     */
    public static void setWindowIcon(Stage stage) {
        try {
            Image icon = new Image(MemoryGameApp.class.getResourceAsStream("/com/example/memory/icons/memory-game-icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load window icon: " + e.getMessage());
            try {
                Image fallbackIcon = new Image(MemoryGameApp.class.getResourceAsStream("/icons/memory-game-icon.png"));
                stage.getIcons().add(fallbackIcon);
            } catch (Exception ex) {
                System.err.println("Could not load fallback icon either: " + ex.getMessage());
            }
        }
    }

    /**
     * Entry point of the application.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}