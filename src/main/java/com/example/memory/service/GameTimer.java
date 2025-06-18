package com.example.memory.service;

import javafx.application.Platform;
import javafx.scene.control.Label;
import com.example.memory.model.GameModel;

public class GameTimer {
    private Thread timerThread;
    private Label timeLabel;
    private GameModel gameModel;
    private boolean running;

    public GameTimer(Label timeLabel, GameModel gameModel) {
        this.timeLabel = timeLabel;
        this.gameModel = gameModel;
        this.running = false;
    }

    public void start() {
        stop(); // Stop any existing timer

        running = true;
        timerThread = new Thread(() -> {
            while (running && !gameModel.isGameComplete() && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(this::updateTimeDisplay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void stop() {
        running = false;
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
            try {
                timerThread.join(1000); // Wait up to 1 second for thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateTimeDisplay() {
        if (timeLabel != null && gameModel != null) {
            long elapsed = gameModel.getElapsedTime();
            int minutes = (int) elapsed / 60;
            int seconds = (int) elapsed % 60;
            timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        }
    }

    public boolean isRunning() {
        return running;
    }
}
