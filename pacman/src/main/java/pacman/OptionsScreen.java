package pacman;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsScreen {

    private double selectedSpeed = 0.3; // Default speed

    public void show(Stage primaryStage, Runnable onBack) {
        // Create UI elements
        Label speedLabel = new Label("Game Speed");
        Label fastLabel = new Label("Fast");
        Label slowLabel = new Label("Slow");
        
        Slider speedSlider = new Slider(0.1, 1.0, selectedSpeed);
        speedSlider.setShowTickLabels(false);  // Hide numbers
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.1);
        speedSlider.setBlockIncrement(0.1);

        // Create a horizontal layout for labels
        HBox speedLabels = new HBox(10);
        speedLabels.setAlignment(Pos.CENTER);
        speedLabels.getChildren().addAll(fastLabel, slowLabel);
        HBox.setHgrow(speedLabels, Priority.ALWAYS);
        // Set the slow label to be right-aligned
        HBox.setMargin(slowLabel, new Insets(0, 0, 0, 150));  // Add margin to push "Slow" to the right

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            selectedSpeed = speedSlider.getValue();
            onBack.run();
        });

        // Apply styles
        speedLabel.getStyleClass().add("options-label");
        fastLabel.getStyleClass().add("speed-label");
        slowLabel.getStyleClass().add("speed-label");
        speedSlider.getStyleClass().add("options-slider");
        backButton.getStyleClass().add("options-button");
        speedLabels.getStyleClass().add("speed-labels-container");

        // Layout
        VBox layout = new VBox(10);
        layout.getStyleClass().add("options-screen");
        layout.getChildren().addAll(speedLabel, speedSlider, speedLabels, backButton);

        // Scene
        Scene scene = new Scene(layout, 700, 700);
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public double getSelectedSpeed() {
        return selectedSpeed;
    }
}
