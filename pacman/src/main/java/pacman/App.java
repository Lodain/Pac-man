package pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import pacman.entities.Ghost;
import pacman.entities.LevelReader;
import pacman.entities.Movement;

public class App extends Application {

    private static final int TILE_SIZE = 40;
    private int playerRow, playerCol;
    private String playerDirection = "RIGHT";
    private char[][] levelData = null;
    private char[][] levelData2 = null;
    private String levelName = null;  
    private Timeline movementTimeline;
    private Timeline mouthAnimationTimeline;
    private Stage primaryStage;
    private boolean isPaused = false;
    private int pacmanImageCounter = 1;
    private double speed = 0.3;
    private List<Ghost> ghosts = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Create buttons
        Button playButton = new Button("Play");
        Button optionsButton = new Button("Options");

        // Set button actions
        playButton.setOnAction(event -> showLevelSelection(primaryStage));
        optionsButton.setOnAction(event -> handleOptionsButton());

        // Create a layout and add buttons
        VBox layout = new VBox(10);
        layout.getChildren().addAll(playButton, optionsButton);

        // Create a scene with the layout
        Scene scene = new Scene(layout, 700, 700);
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setTitle("Pacman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLevelSelection(Stage primaryStage) {
        stopMovementTimeline();
        LevelReader levelReader = new LevelReader();
        List<String> levels = levelReader.getAvailableLevels();

        ListView<String> levelListView = new ListView<>();
        levelListView.getItems().addAll(levels);

        levelListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
                if (selectedLevel != null) {
                    levelName = selectedLevel;
                    loadLevel(primaryStage);
                }
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().add(levelListView);

        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(levelScene);
    }

    private void loadLevel(Stage primaryStage) {
        try {
            // Reset game state
            stopMovementTimeline();
            ghosts.clear(); // Clear any existing ghosts
            
            // Read the level file and create the game board
            LevelReader levelReader = new LevelReader();
            levelData = levelReader.readLevelData(levelName);

            // Create a GridPane to represent the game board
            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().add("game-grid");

            // Load images
            Image wallImage = new Image(getClass().getResourceAsStream("/pacman/images/wall.png"));
            Image gateImage = new Image(getClass().getResourceAsStream("/pacman/images/gate.png"));
            Image keyImage = new Image(getClass().getResourceAsStream("/pacman/images/key.png"));
            Image pointImage = new Image(getClass().getResourceAsStream("/pacman/images/point.png"));
            Image playerImage = new Image(getClass().getResourceAsStream("/pacman/images/pacman-right/1.png"));
            Image ghostImage0 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/green.png"));
            Image ghostImage1 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/orange.png"));
            Image ghostImage2 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/pink.png"));
            Image ghostImage3 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/red.png"));

            // Create the game board
            for (int row = 0; row < levelData.length; row++) {
                for (int col = 0; col < levelData[row].length; col++) {
                    char cell = levelData[row][col];
                    ImageView imageView = null;

                    switch (cell) {
                        case 'W':
                            imageView = new ImageView(wallImage);
                            break;
                        case 'G':
                            imageView = new ImageView(gateImage);
                            break;
                        case 'K':
                            imageView = new ImageView(keyImage);
                            break;
                        case 'o':
                            imageView = new ImageView(pointImage);
                            break;
                        case 'P':
                            playerRow = row;
                            playerCol = col;
                            imageView = new ImageView(playerImage);
                            break;
                        case 'C':
                            Ghost ghost = new Ghost(row, col);
                            ghosts.add(ghost);
                            switch (new Random().nextInt(4)) {
                                case 0: imageView = new ImageView(ghostImage0); break;
                                case 1: imageView = new ImageView(ghostImage1); break;
                                case 2: imageView = new ImageView(ghostImage2); break;
                                case 3: imageView = new ImageView(ghostImage3); break;
                            }
                            break;
                        case '.':
                            Rectangle emptyTile = new Rectangle(TILE_SIZE, TILE_SIZE);
                            emptyTile.setFill(Color.BLACK);
                            gridPane.add(emptyTile, col, row);
                            continue;
                    }
                    if (imageView != null) {
                        imageView.setFitWidth(TILE_SIZE);
                        imageView.setFitHeight(TILE_SIZE);
                        gridPane.add(imageView, col, row);
                    }
                }
            }

            // Create root container
            StackPane root = new StackPane(gridPane);
            root.getStyleClass().add("game-root");

            // Create scene
            Scene gameScene = new Scene(root, 700, 700);
            gameScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

            // Set up keyboard handling
            gameScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (isPaused) {
                        StackPane rootPane = (StackPane) primaryStage.getScene().getRoot();
                        rootPane.getChildren().removeIf(node -> node instanceof VBox && 
                            ((VBox) node).getStyleClass().contains("pause-menu"));
                        isPaused = false;
                        if (movementTimeline != null) {
                            movementTimeline.play();
                        }
                        startMouthAnimation(gridPane);
                    } else {
                        showPauseMenu();
                    }
                } else if (!isPaused) {
                    boolean directionChanged = false;
                    switch (event.getCode()) {
                        case UP: 
                            if (!playerDirection.equals("UP")) {
                                playerDirection = "UP";
                                directionChanged = true;
                            }
                            break;
                        case DOWN: 
                            if (!playerDirection.equals("DOWN")) {
                                playerDirection = "DOWN";
                                directionChanged = true;
                            }
                            break;
                        case LEFT: 
                            if (!playerDirection.equals("LEFT")) {
                                playerDirection = "LEFT";
                                directionChanged = true;
                            }
                            break;
                        case RIGHT: 
                            if (!playerDirection.equals("RIGHT")) {
                                playerDirection = "RIGHT";
                                directionChanged = true;
                            }
                            break;
                        default: break;
                    }
                    if (directionChanged) {
                        updatePlayerTexture(gridPane);
                    }
                }
            });

            // Set up movement timeline
            movementTimeline = new Timeline(
                new KeyFrame(Duration.seconds(speed), event -> movePlayer(gridPane, levelData))
            );
            movementTimeline.setCycleCount(Timeline.INDEFINITE);
            movementTimeline.play();

            startMouthAnimation(gridPane);

            // Set the scene
            primaryStage.setScene(gameScene);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading level: " + e.getMessage());
        }
    }

    private void movePlayer(GridPane gridPane, char[][] levelData) {
        try {
            int newRow = playerRow;
            int newCol = playerCol;

            switch (playerDirection) {
                case "UP": newRow--; break;
                case "DOWN": newRow++; break;
                case "LEFT": newCol--; break;
                case "RIGHT": newCol++; break;
            }

            if (Movement.checkMovement(levelData[newRow][newCol]) == 1) {
                // Clear old position
                gridPane.getChildren().removeIf(node -> 
                    GridPane.getRowIndex(node) == playerRow && 
                    GridPane.getColumnIndex(node) == playerCol);

                // Add empty space at old position
                Rectangle emptyTile = new Rectangle(TILE_SIZE, TILE_SIZE);
                emptyTile.setFill(Color.BLACK);
                gridPane.add(emptyTile, playerCol, playerRow);

                // Update player position
                levelData[playerRow][playerCol] = '.';
                levelData[newRow][newCol] = 'P';
                playerRow = newRow;
                playerCol = newCol;

                // Add player at new position
                Image playerImage = new Image(getClass().getResource("/pacman/images/pacman-" + 
                                           playerDirection.toLowerCase() + "/" + pacmanImageCounter + ".png").toExternalForm());
                ImageView playerView = new ImageView(playerImage);
                playerView.setFitWidth(TILE_SIZE);
                playerView.setFitHeight(TILE_SIZE);
                gridPane.add(playerView, playerCol, playerRow);
            }
            else if (Movement.checkMovement(levelData[newRow][newCol]) == 2) {
                showGameOver();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error moving player: " + e.getMessage());
        }
    }

    private void stopMovementTimeline() {
        if (movementTimeline != null) {
            movementTimeline.stop();
        }
        if (mouthAnimationTimeline != null) {
            mouthAnimationTimeline.stop();
        }
    }

    private void showPauseMenu() {
        isPaused = true;
        movementTimeline.pause();
        mouthAnimationTimeline.pause();

        VBox pauseMenu = new VBox(10);
        pauseMenu.getStyleClass().add("pause-menu");

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pause-label");

        Button resumeButton = new Button("Resume");
        Button mainMenuButton = new Button("Main Menu");

        resumeButton.getStyleClass().add("pause-menu-button");
        mainMenuButton.getStyleClass().add("pause-menu-button");

        pauseMenu.getChildren().addAll(pauseLabel, resumeButton, mainMenuButton);

        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(pauseMenu);

        resumeButton.setOnAction(e -> {
            root.getChildren().remove(pauseMenu);
            isPaused = false;
            movementTimeline.play();
            mouthAnimationTimeline.play();
        });

        mainMenuButton.setOnAction(e -> {
            root.getChildren().remove(pauseMenu);
            isPaused = false;
            stopMovementTimeline();
            // Reset level data
            levelData = null;
            playerRow = 0;
            playerCol = 0;
            playerDirection = "RIGHT";
            pacmanImageCounter = 1;
            // Return to main menu
            start(primaryStage);
        });
    }

    private void handleOptionsButton() {
        System.out.println("Options button clicked!");
    }

    private void showGameOver() {
        // Stop the game
        isPaused = true;
        movementTimeline.stop();

        // Create game over menu
        VBox gameOverMenu = new VBox(10);
        gameOverMenu.getStyleClass().add("pause-menu"); // Reuse pause menu styling

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("pause-label"); // Reuse pause label styling

        Button playAgainButton = new Button("Play Again");
        Button mainMenuButton = new Button("Main Menu");

        playAgainButton.getStyleClass().add("pause-menu-button");
        mainMenuButton.getStyleClass().add("pause-menu-button");

        gameOverMenu.getChildren().addAll(gameOverLabel, playAgainButton, mainMenuButton);

        // Add menu to the game screen
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(gameOverMenu);

        // Button actions
        playAgainButton.setOnAction(e -> {
            root.getChildren().remove(gameOverMenu);
            isPaused = false;
            stopMovementTimeline();
            // Reset level data
            levelData = null;
            playerRow = 0;
            playerCol = 0;
            playerDirection = "RIGHT";
            pacmanImageCounter = 1;
            loadLevel(primaryStage); // Reload the current level
        });

        mainMenuButton.setOnAction(e -> {
            root.getChildren().remove(gameOverMenu);
            isPaused = false;
            stopMovementTimeline();
            // Reset level data
            levelData = null;
            playerRow = 0;
            playerCol = 0;
            playerDirection = "RIGHT";
            pacmanImageCounter = 1;
            start(primaryStage); // Return to main menu
        });
    }

    private void updatePlayerTexture(GridPane gridPane) {
        // Remove current player image
        gridPane.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) == playerRow && 
            GridPane.getColumnIndex(node) == playerCol);

        // Add new player image with updated direction
        Image playerImage = new Image(getClass().getResource("/pacman/images/pacman-" + 
                                   playerDirection.toLowerCase() + "/" + pacmanImageCounter + ".png").toExternalForm());
        ImageView playerView = new ImageView(playerImage);
        playerView.setFitWidth(TILE_SIZE);
        playerView.setFitHeight(TILE_SIZE);
        gridPane.add(playerView, playerCol, playerRow);
    }

    private void startMouthAnimation(GridPane gridPane) {
        mouthAnimationTimeline = new Timeline(
            new KeyFrame(Duration.millis(100), event -> {
                pacmanImageCounter = (pacmanImageCounter % 3) + 1;
                updatePlayerTexture(gridPane);
            })
        );
        mouthAnimationTimeline.setCycleCount(Timeline.INDEFINITE);
        mouthAnimationTimeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}