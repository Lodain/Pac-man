package pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create buttons
        Button playButton = new Button("Play");
        Button optionsButton = new Button("Options");

        // Set button actions
        playButton.setOnAction(event -> handlePlayButton());
        optionsButton.setOnAction(event -> handleOptionsButton());

        // Create a layout and add buttons
        VBox layout = new VBox(10); // 10 is the spacing between elements
        layout.getChildren().addAll(playButton, optionsButton);

        // Create a scene with the layout
        Scene scene = new Scene(layout, 400, 300);

        // Load and apply the CSS file
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        // Set up the stage
        primaryStage.setTitle("Pacman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handlePlayButton() {
        // Logic to start the game
        System.out.println("Play button clicked!");
        // Transition to the game scene
    }

    private void handleOptionsButton() {
        // Logic to open options menu
        System.out.println("Options button clicked!");
        // Transition to the options scene
    }

    public static void main(String[] args) {
        launch(args);
    }
}