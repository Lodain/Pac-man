package pacman;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsScreen {

    private double selectedSpeed = 0.3; // Default speed
    private Runnable onBack;

    public void show(Stage primaryStage, Runnable onBack) {
        this.onBack = onBack;

        // Create UI elements
        Label speedLabel = new Label("Select Game Speed:");
        Slider speedSlider = new Slider(0.1, 1.0, selectedSpeed);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.1);
        speedSlider.setBlockIncrement(0.1);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            selectedSpeed = speedSlider.getValue();
            onBack.run();
        });

        // Apply styles
        speedLabel.getStyleClass().add("options-label");
        speedSlider.getStyleClass().add("options-slider");
        backButton.getStyleClass().add("options-button");

        // Layout
        VBox layout = new VBox(10);
        layout.getStyleClass().add("options-screen");
        layout.getChildren().addAll(speedLabel, speedSlider, backButton);

        // Scene
        Scene scene = new Scene(layout, 700, 700); // Ensure the same dimensions
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public double getSelectedSpeed() {
        return selectedSpeed;
    }
}
