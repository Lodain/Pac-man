package pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import pacman.entities.Ghost;
import pacman.entities.LevelReader;
import pacman.entities.Movement;

/**
 * Main game screen where the gameplay occurs.
 * Handles game mechanics:
 * - Player movement and animation
 * - Ghost movement
 * - Collision detection
 * - Score tracking
 * - Game state (pause, win, lose)
 *
 * @author Danilo Spera
 */
public class LevelScreen {

    /** Size of each game tile */
    private static final int TILE_SIZE = 40;

    /** Current row position of the player */
    private int playerRow;

    /** Current column position of the player */
    private int playerCol;

    /** Current direction the player is facing: "UP", "DOWN", "LEFT", "RIGHT" */
    private String playerDirection = "RIGHT";

    /** matrix storing the level layout where each char represents a game element:
     * W = Wall, G = Gate, K = Key, o = Point, P = Player, C = Ghost, . = Empty */
    private char[][] levelData = null;

    /** Timeline controlling entity movement speed */
    private Timeline movementTimeline;

    /** Timeline controlling Pacman's mouth animation */
    private Timeline mouthAnimationTimeline;

    /** Flag indicating if game is currently paused */
    private boolean isPaused = false;

    /** Current frame of Pacman's mouth animation (1-3) */
    private int pacmanImageCounter = 1;

    /** Movement speed of player and ghosts (lower = faster) */
    private double speed = 0.3;

    /** List containing all ghost entities in the level */
    private final List<Ghost> ghosts = new ArrayList<>();

    /** matrix with all the ghosts in the level */
    private char[][] ghostGrid = null;

    /** Reference to the primary stage */
    private Stage primaryStage;

    /** Current level name */
    private String levelName;

    /** Callback to return to main menu */
    private Runnable returnToMenuCallback;

    /** Current score */
    private int point = 0;

    /** Flag indicating if player has collected the key */
    private boolean key = false;

    /** Label displaying current score */
    private Label pointsLabel;

    /** Flag indicating if game end menu is currently shown */
    private boolean isGameEndMenuShown = false;

    /** Images of the game elements */
    Image wallImage = new Image(getClass().getResourceAsStream("/pacman/images/wall.png"));
    Image gateImage = new Image(getClass().getResourceAsStream("/pacman/images/gate.png"));
    Image keyImage = new Image(getClass().getResourceAsStream("/pacman/images/key.png"));
    Image pointImage = new Image(getClass().getResourceAsStream("/pacman/images/point.png"));
    Image playerImage = new Image(getClass().getResourceAsStream("/pacman/images/pacman-right/1.png"));
    Image ghostImage0 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/green.png"));
    Image ghostImage1 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/orange.png"));
    Image ghostImage2 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/pink.png"));
    Image ghostImage3 = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/red.png"));

    /** ImageView for the key icon */
    private ImageView keyImageView;

    /**
     * Creates a new LevelScreen instance.
     * Initializes game components and default values.
     */
    public LevelScreen() {
        // Constructor can be empty if all initialization is done in field declarations
    }

    /**
     * Loads and initializes a game level.
     * Sets up the game board, player position, ghosts, and starts game loops.
     *
     * @param primaryStage The primary stage of the application
     * @param levelName Name of the level file to load
     */
    public void loadLevel(Stage primaryStage, String levelName) {
        this.primaryStage = primaryStage;
        this.levelName = levelName;
        
        if (levelName == null) {
            System.err.println("Level name is null!");
            return;
        }
        
        try {
            // Reset game state
            stopMovementTimeline();
            ghosts.clear(); // Clear any existing ghosts

            // Read the level file and create the game board
            LevelReader levelReader = new LevelReader();
            levelData = levelReader.readLevelData(levelName);

            // Initialize the ghost grid with the same dimensions as levelData
            ghostGrid = levelReader.readLevelData(levelName);

            // Create a GridPane to represent the game board
            GridPane gridPane = new GridPane();
            GridPane ghostGridPane = new GridPane();
            gridPane.getStyleClass().add("game-grid");
            ghostGridPane.getStyleClass().add("game-grid");

            // Calculate the scaling factor based on level size
            double maxWidth = 600; // Maximum width for the game area
            double maxHeight = 600; // Maximum height for the game area
            
            int levelWidth = levelData[0].length * TILE_SIZE;
            int levelHeight = levelData.length * TILE_SIZE;
            
            double scaleX = maxWidth / levelWidth;
            double scaleY = maxHeight / levelHeight;
            double scale = Math.min(1.0, Math.min(scaleX, scaleY));

            // Apply scaling to both grid panes
            gridPane.setScaleX(scale);
            gridPane.setScaleY(scale);
            ghostGridPane.setScaleX(scale);
            ghostGridPane.setScaleY(scale);

            // Initialize points label
            pointsLabel = new Label("Points: " + point);
            pointsLabel.getStyleClass().add("points-label");

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
                            levelData[row][col]='.';
                        case '.':
                            Rectangle emptyTile = new Rectangle(TILE_SIZE, TILE_SIZE);
                            emptyTile.setFill(Color.TRANSPARENT);
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

            for (int row = 0; row < levelData.length; row++) {
                for (int col = 0; col < levelData[row].length; col++) {
                    char cell = ghostGrid[row][col];
                    ImageView imageView = null;

                    switch (cell) {
                        case 'C':
                            Ghost ghost = new Ghost(row, col, new Random().nextInt(4));
                            ghosts.add(ghost);
                            switch (ghost.getColor()) {
                                case 0: imageView = new ImageView(ghostImage0); break;
                                case 1: imageView = new ImageView(ghostImage1); break;
                                case 2: imageView = new ImageView(ghostImage2); break;
                                case 3: imageView = new ImageView(ghostImage3); break;
                            }
                            break;
                        case 'W':
                            imageView = new ImageView(wallImage);
                            break;
                        default:
                            continue;
                    }
                    if (imageView != null) {
                        imageView.setFitWidth(TILE_SIZE);
                        imageView.setFitHeight(TILE_SIZE);
                        ghostGridPane.add(imageView, col, row);
                    }
                }
            }

            // Initialize key image view
            keyImageView = new ImageView(keyImage);
            keyImageView.setFitWidth(TILE_SIZE);
            keyImageView.setFitHeight(TILE_SIZE);
            keyImageView.setVisible(false); // Initially hidden

            // Create root container with fixed size
            StackPane root = new StackPane(gridPane, ghostGridPane);
            root.getStyleClass().add("game-root");
            root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            root.setPrefSize(700, 700);
            
            // Center the grid panes
            StackPane.setAlignment(gridPane, Pos.CENTER);
            StackPane.setAlignment(ghostGridPane, Pos.CENTER);

            // Add points label to the root
            pointsLabel = new Label("Points: " + point);
            pointsLabel.getStyleClass().add("points-label");
            StackPane.setAlignment(pointsLabel, Pos.TOP_RIGHT);
            root.getChildren().add(pointsLabel);

            // Add key image view to the root
            keyImageView = new ImageView(keyImage);
            keyImageView.setFitWidth(TILE_SIZE);
            keyImageView.setFitHeight(TILE_SIZE);
            keyImageView.setVisible(false);
            StackPane.setAlignment(keyImageView, Pos.TOP_LEFT);
            root.getChildren().add(keyImageView);

            // Create scene
            Scene gameScene = new Scene(root, 700, 700);
            gameScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

            // Set up keyboard handling
            gameScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (isPaused && !isGameEndMenuShown) {
                        // Resume game
                        StackPane rootPane = (StackPane) primaryStage.getScene().getRoot();
                        rootPane.getChildren().removeIf(node -> node instanceof VBox && 
                            ((VBox) node).getStyleClass().contains("pause-menu"));
                        isPaused = false;
                        if (movementTimeline != null) {
                            movementTimeline.play();
                        }
                        if (mouthAnimationTimeline != null) {
                            mouthAnimationTimeline.play();
                        }
                    } else if (!isGameEndMenuShown) {
                        // Show pause menu
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
                new KeyFrame(Duration.seconds(speed), event -> {
                    movePlayer(gridPane, levelData);
                    moveGhosts(ghostGridPane);
                })
            );
            movementTimeline.setCycleCount(Timeline.INDEFINITE);
            movementTimeline.play();

            startMouthAnimation(gridPane);

            // Set the scene
            primaryStage.setScene(gameScene);

        } catch (Exception e) {
            System.out.println("Error loading level: " + e.getMessage());
        }
    }

    /**
     * Moves the player character based on current direction.
     * @param gridPane The game grid
     * @param levelData Current level data
     */
    private void movePlayer(GridPane gridPane, char[][] levelData) {
        boolean move = false;
        try {
            int newRow = playerRow;
            int newCol = playerCol;
            int nextSquare;

            switch (playerDirection) {
                case "UP": newRow--; break;
                case "DOWN": newRow++; break;
                case "LEFT": newCol--; break;
                case "RIGHT": newCol++; break;
            }
            nextSquare = Movement.checkMovement(levelData[newRow][newCol]);

            if (Movement.checkMovement(ghostGrid[newRow][newCol]) == 2) {
                showGameOver();
            }
            else if (nextSquare == 1) {
                move = true;
            }
            else if (nextSquare == 5){
                move = true;
                point++;
            }
            else if (nextSquare == 3){
                move = true;
                key = true;
                updateKeyImageVisibility();
            }
            else if (nextSquare == 4 && key){
                showYouWin();
            }

            if (move) {
                // Clear old position
                gridPane.getChildren().removeIf(node -> {
                    Integer row = GridPane.getRowIndex(node);
                    Integer col = GridPane.getColumnIndex(node);
                    return row != null && col != null && 
                           row == playerRow && col == playerCol;
                });

                // Add empty space at old position
                Rectangle emptyTile = new Rectangle(TILE_SIZE, TILE_SIZE);
                emptyTile.setFill(Color.TRANSPARENT);
                GridPane.setRowIndex(emptyTile, playerRow);
                GridPane.setColumnIndex(emptyTile, playerCol);
                gridPane.add(emptyTile, playerCol, playerRow);

                // Update player position
                levelData[playerRow][playerCol] = '.';
                levelData[newRow][newCol] = 'P';
                playerRow = newRow;
                playerCol = newCol;

                // Add player at new position
                Image playerImage = new Image(getClass().getResource("/pacman/images/pacman-" + playerDirection.toLowerCase() + "/" + pacmanImageCounter + ".png").toExternalForm());
                ImageView playerView = new ImageView(playerImage);
                playerView.setFitWidth(TILE_SIZE);
                playerView.setFitHeight(TILE_SIZE);
                GridPane.setRowIndex(playerView, playerRow);
                GridPane.setColumnIndex(playerView, playerCol);
                gridPane.add(playerView, playerCol, playerRow);

                // Update points label
                pointsLabel.setText("Points: " + point);
            }
        } catch (Exception e) {
            System.out.println("Error moving player: " + e.getMessage());
        }
    }

    /**
     * Stops all game timelines (movement and animation).
     */
    private void stopMovementTimeline() {
        if (movementTimeline != null) {
            movementTimeline.stop();
        }
        if (mouthAnimationTimeline != null) {
            mouthAnimationTimeline.stop();
        }
    }

    /**
     * Displays the pause menu and handles pause-related actions.
     */
    private void showPauseMenu() {
        if (isGameEndMenuShown) return; // Don't show pause menu if game end menu is shown
        
        isPaused = true;
        if (movementTimeline != null) {
            movementTimeline.pause();
        }
        if (mouthAnimationTimeline != null) {
            mouthAnimationTimeline.pause();
        }

        // Remove any existing pause menu first
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().removeIf(node -> node instanceof VBox && 
            ((VBox) node).getStyleClass().contains("pause-menu"));

        VBox pauseMenu = new VBox(10);
        pauseMenu.getStyleClass().add("pause-menu");

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pause-label");

        Button resumeButton = new Button("Resume");
        Button mainMenuButton = new Button("Main Menu");

        resumeButton.getStyleClass().add("pause-menu-button");
        mainMenuButton.getStyleClass().add("pause-menu-button");

        pauseMenu.getChildren().addAll(pauseLabel, resumeButton, mainMenuButton);

        root.getChildren().add(pauseMenu);

        resumeButton.setOnAction(e -> {
            root.getChildren().remove(pauseMenu);
            isPaused = false;
            if (movementTimeline != null) {
                movementTimeline.play();
            }
            if (mouthAnimationTimeline != null) {
                mouthAnimationTimeline.play();
            }
        });

        mainMenuButton.setOnAction(e -> {
            root.getChildren().remove(pauseMenu);
            isPaused = false;
            stopMovementTimeline();
            resetLevel();
            if (returnToMenuCallback != null) {
                returnToMenuCallback.run();
            }
        });
    }

    /**
     * Shows game over screen with final score and replay options.
     */
    private void showGameOver() {
        isGameEndMenuShown = true;
        isPaused = true;
        movementTimeline.stop();

        // Create game over menu
        VBox gameOverMenu = new VBox(10);
        gameOverMenu.getStyleClass().add("pause-menu"); // Reuse pause menu styling

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("pause-label"); // Reuse pause label styling

        Label scoreLabel = new Label("Score: " + point);
        scoreLabel.getStyleClass().add("score-label");

        Button playAgainButton = new Button("Play Again");
        Button mainMenuButton = new Button("Main Menu");

        playAgainButton.getStyleClass().add("pause-menu-button");
        mainMenuButton.getStyleClass().add("pause-menu-button");

        gameOverMenu.getChildren().addAll(gameOverLabel, scoreLabel, playAgainButton, mainMenuButton);

        // Add menu to the game screen
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(gameOverMenu);

        // Button actions
        playAgainButton.setOnAction(e -> {
            root.getChildren().remove(gameOverMenu);
            isPaused = false;
            stopMovementTimeline();
            resetLevel();
            loadLevel(primaryStage, levelName); // Reload the current level
        });

        mainMenuButton.setOnAction(e -> {
            root.getChildren().remove(gameOverMenu);
            isPaused = false;
            stopMovementTimeline();
            resetLevel();
            if (returnToMenuCallback != null) {
                returnToMenuCallback.run();
            }
        });
    }

    /**
     * Updates player's visual representation based on current direction.
     *
     * @param gridPane The game's visual grid
     */
    private void updatePlayerTexture(GridPane gridPane) {
        // Remove current player image
        gridPane.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            return row != null && col != null && 
                   row == playerRow && col == playerCol;
        });

        // Add new player image with updated direction
        Image playerImage = new Image(getClass().getResource("/pacman/images/pacman-" + playerDirection.toLowerCase() + "/" + pacmanImageCounter + ".png").toExternalForm());
        ImageView playerView = new ImageView(playerImage);
        playerView.setFitWidth(TILE_SIZE);
        playerView.setFitHeight(TILE_SIZE);
        GridPane.setRowIndex(playerView, playerRow);
        GridPane.setColumnIndex(playerView, playerCol);
        gridPane.add(playerView, playerCol, playerRow);
    }

    /**
     * Starts Pacman's mouth opening/closing animation.
     *
     * @param gridPane The game's visual grid
     */
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

    /**
     * Moves all ghosts in random directions.
     * Handles collision detection with walls and player.
     *
     * @param ghostGridPane The ghost layer visual grid
     */
    private void moveGhosts(GridPane ghostGridPane) {
        for (Ghost ghost : ghosts) {
            int newRow = ghost.getRow();
            int newCol = ghost.getCol();

            // Randomly choose a direction
            switch (new Random().nextInt(4)) {
                case 0: newRow--; break; // UP
                case 1: newRow++; break; // DOWN
                case 2: newCol--; break; // LEFT
                case 3: newCol++; break; // RIGHT
            }

            // Check if the new position is valid and not occupied by another ghost
            if (levelData[newRow][newCol] != 'W' && ghostGrid[newRow][newCol] != 'C') {
                if (newRow == playerRow && newCol == playerCol) {
                    showGameOver();
                    return; // Exit the method to stop further ghost movement
                }
                // Clear old position
                ghostGridPane.getChildren().removeIf(node -> {
                    Integer row = GridPane.getRowIndex(node);
                    Integer col = GridPane.getColumnIndex(node);
                    return row != null && col != null && 
                           row == ghost.getRow() && col == ghost.getCol();
                });
                ghostGrid[ghost.getRow()][ghost.getCol()] = '.';

                // Update ghost position
                ghost.setRow(newRow);
                ghost.setCol(newCol);
                ghostGrid[newRow][newCol] = 'C';

                // Add ghost at new position
                Image ghostImage;
                switch (ghost.getColor()) {
                    case 0: ghostImage = ghostImage0; break;
                    case 1: ghostImage = ghostImage1; break;
                    case 2: ghostImage = ghostImage2; break;
                    case 3: ghostImage = ghostImage3; break;
                    default: ghostImage = ghostImage0; break;
                }
                ImageView ghostView = new ImageView(ghostImage);
                ghostView.setFitWidth(TILE_SIZE);
                ghostView.setFitHeight(TILE_SIZE);
                GridPane.setRowIndex(ghostView, newRow);
                GridPane.setColumnIndex(ghostView, newCol);
                ghostGridPane.add(ghostView, newCol, newRow);
            }
        }
    }

    /**
     * Sets callback for returning to main menu.
     *
     * @param callback Runnable to execute when returning to menu
     */
    public void setReturnToMenuCallback(Runnable callback) {
        this.returnToMenuCallback = callback;
    }

    /**
     * Sets movement speed for player and ghosts.
     *
     * @param speed Movement speed (lower = faster)
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Updates visibility of key icon based on collection status.
     */
    private void updateKeyImageVisibility() {
        keyImageView.setVisible(key);
    }

    /**
     * Shows victory screen with final score and replay options.
     */
    private void showYouWin() {
        isGameEndMenuShown = true;
        isPaused = true;
        movementTimeline.stop();

        // Create "You Win" menu
        VBox youWinMenu = new VBox(10);
        youWinMenu.getStyleClass().add("pause-menu"); // Reuse pause menu styling

        Label youWinLabel = new Label("YOU WIN");
        youWinLabel.getStyleClass().add("win-label"); // New style for win label

        Label scoreLabel = new Label("Score: " + point);
        scoreLabel.getStyleClass().add("score-label");

        Button playAgainButton = new Button("Play Again");
        Button mainMenuButton = new Button("Main Menu");

        playAgainButton.getStyleClass().add("pause-menu-button");
        mainMenuButton.getStyleClass().add("pause-menu-button");

        youWinMenu.getChildren().addAll(youWinLabel, scoreLabel, playAgainButton, mainMenuButton);

        // Add menu to the game screen
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(youWinMenu);

        // Button actions
        playAgainButton.setOnAction(e -> {
            root.getChildren().remove(youWinMenu);
            isPaused = false;
            stopMovementTimeline();
            resetLevel();
            loadLevel(primaryStage, levelName); // Reload the current level
        });

        mainMenuButton.setOnAction(e -> {
            root.getChildren().remove(youWinMenu);
            isPaused = false;
            stopMovementTimeline();
            resetLevel();
            if (returnToMenuCallback != null) {
                returnToMenuCallback.run();
            }
        });
    }

    /**
     * Resets all game state variables to initial values.
     */
    public void resetLevel() {
        // Reset level data
        levelData = null;
        playerRow = 0;
        playerCol = 0;
        playerDirection = "RIGHT";
        pacmanImageCounter = 1;
        point = 0;
        key = false;
        isGameEndMenuShown = false;
    }
}
