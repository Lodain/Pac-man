package pacman;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartScreen {

    public void show(Stage primaryStage, Runnable onPlay, Runnable onOptions) {
        // Create title image
        Image titleImage = new Image(getClass().getResourceAsStream("/pacman/images/title.png"));
        ImageView titleImageView = new ImageView(titleImage);
        titleImageView.setFitWidth(400);  // Adjust width as needed
        titleImageView.setPreserveRatio(true);
        
        // Create buttons
        Button playButton = new Button("Play");
        Button optionsButton = new Button("Options");

        // Set button actions
        playButton.setOnAction(event -> onPlay.run());
        optionsButton.setOnAction(event -> onOptions.run());

        // Create a layout and add title and buttons
        VBox layout = new VBox(20);  // Increased spacing
        layout.getStyleClass().add("start-screen");
        layout.getChildren().addAll(titleImageView, playButton, optionsButton);

        // Create a scene with the layout
        Scene scene = new Scene(layout, 700, 700);
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setTitle("Pacman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
