package pacman;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pacman.entities.LevelReader;

public class App extends Application {

    private static final int TILE_SIZE = 40; // Size of each tile in pixels

    @Override
    public void start(Stage primaryStage) {
        // Create buttons
        Button playButton = new Button("Play");
        Button optionsButton = new Button("Options");

        // Set button actions
        playButton.setOnAction(event -> showLevelSelection(primaryStage));
        optionsButton.setOnAction(event -> handleOptionsButton());

        // Create a layout and add buttons
        VBox layout = new VBox(10); // 10 is the spacing between elements
        layout.getChildren().addAll(playButton, optionsButton);

        // Create a scene with the layout
        Scene scene = new Scene(layout, 700, 700);

        // Load and apply the CSS file
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        // Set up the stage
        primaryStage.setTitle("Pacman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLevelSelection(Stage primaryStage) {
        LevelReader levelReader = new LevelReader();
        List<String> levels = levelReader.getAvailableLevels();

        ListView<String> levelListView = new ListView<>();
        levelListView.getItems().addAll(levels);

        levelListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to select a level
                String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
                if (selectedLevel != null) {
                    loadLevel(primaryStage, selectedLevel);
                }
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().add(levelListView);

        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(levelScene);
    }

    private void loadLevel(Stage primaryStage, String levelFileName) {
        // Read the level file and create the game board
        LevelReader levelReader = new LevelReader();
        char[][] levelData = levelReader.readLevelData(levelFileName);

        // Load the wall image
        Image wallImage = new Image(getClass().getResource("/pacman/images/wall.png").toExternalForm());

        // Create a GridPane to represent the game board
        GridPane gridPane = new GridPane();

        for (int row = 0; row < levelData.length; row++) {
            for (int col = 0; col < levelData[row].length; col++) {
                char cell = levelData[row][col];
                switch (cell) {
                    case 'W': // Wall
                        ImageView wallView = new ImageView(wallImage);
                        wallView.setFitWidth(TILE_SIZE);
                        wallView.setFitHeight(TILE_SIZE);
                        gridPane.add(wallView, col, row);
                        break;
                    case '.': // Empty field
                        Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                        tile.setFill(Color.BLACK);
                        gridPane.add(tile, col, row);
                        break;
                    // Add more cases for other cell types if needed
                    default:
                        Rectangle defaultTile = new Rectangle(TILE_SIZE, TILE_SIZE);
                        defaultTile.setFill(Color.BLACK);
                        gridPane.add(defaultTile, col, row);
                        break;
                }
            }
        }

        // Create a new scene to display the level
        Scene gameScene = new Scene(gridPane, 700, 700);
        gameScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(gameScene);
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