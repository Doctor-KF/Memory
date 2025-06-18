package com.example.memory.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIComponents {

    // Color scheme
    public static final String BACKGROUND_COLOR = "#1a1a1a";
    public static final String PANEL_COLOR = "#2a2a2a";
    public static final String CARD_COLOR = "#333";
    public static final String BORDER_COLOR = "#444";

    public static Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                toHexString(color.darker())
        ));

        // Hover effects
        button.setOnMouseEntered(e ->
                button.setStyle(String.format(
                        "-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                        toHexString(color)
                ))
        );

        button.setOnMouseExited(e ->
                button.setStyle(String.format(
                        "-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15 30;",
                        toHexString(color.darker())
                ))
        );

        return button;
    }

    public static Label createTitleLabel(String text, Color color, int fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        label.setEffect(new DropShadow(10, color));
        return label;
    }

    public static Label createInfoLabel(String text, Color color, int fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        return label;
    }

    public static Label createLeaderboardLabel(String text, Color color, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static class CardStyles {
        public static final String HIDDEN =
                "-fx-background-color: #333; -fx-text-fill: #888; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;";

        public static final String HOVER =
                "-fx-background-color: #444; -fx-text-fill: #aaa; -fx-border-color: #777; -fx-border-radius: 10; -fx-background-radius: 10;";

        public static final String REVEALED =
                "-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #777; -fx-border-radius: 10; -fx-background-radius: 10;";

        public static final String MATCHED =
                "-fx-background-color: #2d5a2d; -fx-text-fill: lightgreen; -fx-border-color: lightgreen; -fx-border-radius: 10; -fx-background-radius: 10;";
    }

    public static class Colors {
        public static final Color CYAN = Color.CYAN;
        public static final Color GOLD = Color.GOLD;
        public static final Color SILVER = Color.SILVER;
        public static final Color BRONZE = Color.web("#CD7F32");
        public static final Color LIGHT_GREEN = Color.LIGHTGREEN;
        public static final Color LIGHT_CORAL = Color.LIGHTCORAL;
        public static final Color LIGHT_GRAY = Color.LIGHTGRAY;
        public static final Color YELLOW = Color.YELLOW;
        public static final Color ORANGE = Color.ORANGE;
        public static final Color WHITE = Color.WHITE;
    }
}
