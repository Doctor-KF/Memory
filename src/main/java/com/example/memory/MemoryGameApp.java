package com.example.memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MemoryGameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set the application icon
            setWindowIcon(primaryStage);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            primaryStage.setTitle("🧠 Memory Game");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the window icon for the application
     * @param stage The stage to set the icon for
     */
    public static void setWindowIcon(Stage stage) {
        try {
            // Try to load the icon from resources
            // You can use different formats: .png, .jpg, .gif, .bmp
            Image icon = new Image(MemoryGameApp.class.getResourceAsStream("/com/example/memory/icons/memory-game-icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load window icon: " + e.getMessage());
            // Fallback: create a simple programmatic icon or use default
            try {
                // Alternative: try loading from a different location
                Image fallbackIcon = new Image(MemoryGameApp.class.getResourceAsStream("/icons/memory-game-icon.png"));
                stage.getIcons().add(fallbackIcon);
            } catch (Exception ex) {
                System.err.println("Could not load fallback icon either: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}