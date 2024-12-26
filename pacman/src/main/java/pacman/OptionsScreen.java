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

/**
 * @author: Danilo Spera
 * Screen for configuring game options.
 * handles:
 * - Game speed adjustment (affects both player and ghost movement)
 * - Speed range from 0.1 (fastest) to 1.0 (slowest)
 */
public class OptionsScreen {

    /** Current game speed value (0.1 = fastest, 1.0 = slowest) */
    private double selectedSpeed = 0.3;

    /**
     * Displays the options configuration interface.
     * Creates a slider for speed adjustment and saves changes on back button press.
     * 
     * @param primaryStage The main application window
     * @param onBack Callback executed when returning to previous screen
     */
    public void show(Stage primaryStage, Runnable onBack) {
        // Create labels for the speed control
        Label speedLabel = new Label("Game Speed");
        Label fastLabel = new Label("Fast");
        Label slowLabel = new Label("Slow");
        
        // Configure the speed slider
        Slider speedSlider = new Slider(0.1, 1.0, selectedSpeed);  // min, max, default
        speedSlider.setShowTickLabels(false);  // Hide numeric labels
        speedSlider.setShowTickMarks(true);    // Show tick marks
        speedSlider.setMajorTickUnit(0.1);     // Major tick every 0.1
        speedSlider.setBlockIncrement(0.1);    // Arrow key increment

        // Create container for Fast/Slow labels
        HBox speedLabels = new HBox(10);
        speedLabels.setAlignment(Pos.CENTER);
        speedLabels.getChildren().addAll(fastLabel, slowLabel);
        HBox.setHgrow(speedLabels, Priority.ALWAYS);
        // Push "Slow" label to the right
        HBox.setMargin(slowLabel, new Insets(0, 0, 0, 150));

        // Back button - saves speed and returns
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            selectedSpeed = speedSlider.getValue();  // Save selected speed
            onBack.run();                           // Return to previous screen
        });

        // Apply CSS styles to all components
        speedLabel.getStyleClass().add("options-label");
        fastLabel.getStyleClass().add("speed-label");
        slowLabel.getStyleClass().add("speed-label");
        speedSlider.getStyleClass().add("options-slider");
        backButton.getStyleClass().add("options-button");
        speedLabels.getStyleClass().add("speed-labels-container");

        // Create vertical layout and add all components
        VBox layout = new VBox(10);  // 10px spacing
        layout.getStyleClass().add("options-screen");
        layout.getChildren().addAll(speedLabel, speedSlider, speedLabels, backButton);

        // Set up and display the scene
        Scene scene = new Scene(layout, 700, 700);
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    /**
     * Returns the currently selected game speed.
     * Lower values = faster gameplay.
     * 
     * @return The selected speed value between 0.1 and 1.0
     */
    public double getSelectedSpeed() {
        return selectedSpeed;
    }
}
