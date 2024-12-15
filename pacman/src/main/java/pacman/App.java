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

        // Load the wall, gate, key, point, and player images
        Image wallImage = new Image(getClass().getResource("/pacman/images/wall.png").toExternalForm());
        Image gateImage = new Image(getClass().getResource("/pacman/images/gate.png").toExternalForm());
        Image keyImage = new Image(getClass().getResource("/pacman/images/key.png").toExternalForm());
        Image pointImage = new Image(getClass().getResource("/pacman/images/point.png").toExternalForm());
        Image playerImage = new Image(getClass().getResource("/pacman/images/pacman-right/1.png").toExternalForm());

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
                    case 'G': // Gate
                        ImageView gateView = new ImageView(gateImage);
                        gateView.setFitWidth(TILE_SIZE);
                        gateView.setFitHeight(TILE_SIZE);
                        gridPane.add(gateView, col, row);
                        break;
                    case 'K': // Key
                        ImageView keyView = new ImageView(keyImage);
                        keyView.setFitWidth(TILE_SIZE);
                        keyView.setFitHeight(TILE_SIZE);
                        gridPane.add(keyView, col, row);
                        break;
                    case 'C': // Ghost
                        String ghostImagePath = levelReader.getRandomGhostImage();
                        Image ghostImage = new Image(getClass().getResource(ghostImagePath).toExternalForm());
                        ImageView ghostView = new ImageView(ghostImage);
                        ghostView.setFitWidth(TILE_SIZE);
                        ghostView.setFitHeight(TILE_SIZE);
                        gridPane.add(ghostView, col, row);
                        break;
                    case 'o': // Point
                        ImageView pointView = new ImageView(pointImage);
                        pointView.setFitWidth(TILE_SIZE);
                        pointView.setFitHeight(TILE_SIZE);
                        gridPane.add(pointView, col, row);
                        break;
                    case 'P': // Player
                        ImageView playerView = new ImageView(playerImage);
                        playerView.setFitWidth(TILE_SIZE);
                        playerView.setFitHeight(TILE_SIZE);
                        gridPane.add(playerView, col, row);
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