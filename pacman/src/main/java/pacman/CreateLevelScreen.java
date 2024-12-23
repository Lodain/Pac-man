package pacman;

import java.io.FileWriter;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateLevelScreen {
    private static final int TILE_SIZE = 40;
    private char[][] levelGrid;
    private int gridWidth;
    private int gridHeight;
    private char selectedTile = 'W'; // Default selected tile
    private GridPane gameGrid;

    // Load images
    private final Image wallImage = new Image(getClass().getResourceAsStream("/pacman/images/wall.png"));
    private final Image gateImage = new Image(getClass().getResourceAsStream("/pacman/images/gate.png"));
    private final Image keyImage = new Image(getClass().getResourceAsStream("/pacman/images/key.png"));
    private final Image pointImage = new Image(getClass().getResourceAsStream("/pacman/images/point.png"));
    private final Image playerImage = new Image(getClass().getResourceAsStream("/pacman/images/pacman-right/1.png"));
    private final Image ghostImage = new Image(getClass().getResourceAsStream("/pacman/images/ghosts/green.png"));
    private final Image emptyImage = new Image(getClass().getResourceAsStream("/pacman/images/empty.png"));

    public void show(Stage primaryStage, Runnable onBack) {
        VBox layout = new VBox(10);
        layout.getStyleClass().add("create-level-layout");
        layout.setPadding(new Insets(20));

        TextField levelNameField = new TextField();
        levelNameField.setPromptText("Enter level name");

        // Dimension selection
        HBox dimensionBox = new HBox(10);
        TextField widthField = new TextField();
        widthField.setPromptText("Width");
        TextField heightField = new TextField();
        heightField.setPromptText("Height");
        Button createGridButton = new Button("Create Grid");
        dimensionBox.getChildren().addAll(widthField, heightField, createGridButton);

        // Create a StackPane to hold the game grid with maximum height
        StackPane gridContainer = new StackPane();
        gridContainer.setPrefSize(700, 400);
        gridContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gridContainer.setMinHeight(200); // Set minimum height
        gridContainer.setMaxHeight(400); // Set maximum height
        
        // Game grid
        gameGrid = new GridPane();
        gameGrid.setHgap(2);
        gameGrid.setVgap(2);
        gameGrid.getStyleClass().add("game-grid");
        gameGrid.setPadding(new Insets(10));
        
        gridContainer.getChildren().add(gameGrid);
        StackPane.setAlignment(gameGrid, Pos.CENTER);

        // Create a container for the asset toolbar that can be collapsed
        VBox assetContainer = new VBox(5);
        assetContainer.setMinHeight(80);
        
        // Create toggle button for assets
        Button toggleAssetsButton = new Button("▼ Assets");
        toggleAssetsButton.getStyleClass().add("toggle-button");
        
        // Asset selection toolbar
        HBox assetBox = new HBox(10);
        assetBox.setPadding(new Insets(10));
        assetBox.getStyleClass().add("asset-toolbar");
        assetBox.setMinHeight(80);
        assetBox.setAlignment(Pos.CENTER);
        assetBox.setVisible(true); // Initially visible

        // Add toggle functionality
        toggleAssetsButton.setOnAction(e -> {
            assetBox.setVisible(!assetBox.isVisible());
            toggleAssetsButton.setText(assetBox.isVisible() ? "▼ Assets" : "▲ Assets");
        });

        // Add components to asset container
        assetContainer.getChildren().addAll(toggleAssetsButton, assetBox);

        // Create tile buttons
        createTileButton(assetBox, wallImage, 'W');
        createTileButton(assetBox, gateImage, 'G');
        createTileButton(assetBox, keyImage, 'K');
        createTileButton(assetBox, pointImage, 'o');
        createTileButton(assetBox, playerImage, 'P');
        createTileButton(assetBox, ghostImage, 'C');
        createTileButton(assetBox, emptyImage, '.');

        createGridButton.setOnAction(e -> {
            try {
                gridWidth = Integer.parseInt(widthField.getText());
                gridHeight = Integer.parseInt(heightField.getText());
                createGrid();
            } catch (NumberFormatException ex) {
                System.err.println("Please enter valid numbers for dimensions");
            }
        });

        Button saveButton = new Button("Save Level");
        saveButton.setOnAction(event -> {
            String levelName = levelNameField.getText();
            if (!levelName.isEmpty() && levelGrid != null) {
                saveLevel(levelName);
                onBack.run();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> onBack.run());

        layout.getChildren().addAll(levelNameField, dimensionBox, gridContainer, assetContainer, saveButton, backButton);

        Scene scene = new Scene(layout, 800, 800);
        scene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void createGrid() {
        gameGrid.getChildren().clear();
        levelGrid = new char[gridHeight][gridWidth];

        // Calculate the scaling factor based on grid size
        double maxWidth = 600; // Maximum width for the game area
        double maxHeight = 400; // Slightly smaller max height to accommodate UI elements
        
        int totalWidth = gridWidth * TILE_SIZE;
        int totalHeight = gridHeight * TILE_SIZE;
        
        double scaleX = maxWidth / totalWidth;
        double scaleY = maxHeight / totalHeight;
        double scale = Math.min(1.0, Math.min(scaleX, scaleY));

        // Apply scaling to the grid
        gameGrid.setScaleX(scale);
        gameGrid.setScaleY(scale);

        // Initialize grid with empty spaces
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                levelGrid[row][col] = '.';
                
                // Create a StackPane to hold the background and image
                StackPane cell = new StackPane();
                cell.setStyle("-fx-background-color: black; -fx-border-color: #444444; -fx-border-width: 1;");
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                
                ImageView tileView = new ImageView(emptyImage);
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                
                cell.getChildren().add(tileView);
                
                final int finalRow = row;
                final int finalCol = col;
                cell.setOnMouseClicked(e -> {
                    placeTile(finalRow, finalCol);
                });
                
                gameGrid.add(cell, col, row);
            }
        }
    }

    private void createTileButton(HBox container, Image image, char tileType) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        
        Button tileButton = new Button();
        tileButton.setGraphic(imageView);
        tileButton.getStyleClass().add("tile-button");
        tileButton.setOnAction(e -> selectedTile = tileType);
        
        container.getChildren().add(tileButton);
    }

    private void placeTile(int row, int col) {
        if (levelGrid != null) {
            levelGrid[row][col] = selectedTile;
            Image tileImage = getTileImage(selectedTile);
            
            ImageView tileView = new ImageView(tileImage);
            tileView.setFitWidth(TILE_SIZE);
            tileView.setFitHeight(TILE_SIZE);
            
            // Find the StackPane at the specified position and update its content
            gameGrid.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col)
                .findFirst()
                .ifPresent(cell -> {
                    ((StackPane) cell).getChildren().clear();
                    ((StackPane) cell).getChildren().add(tileView);
                });
        }
    }

    private Image getTileImage(char tile) {
        switch (tile) {
            case 'W': return wallImage;
            case 'G': return gateImage;
            case 'K': return keyImage;
            case 'o': return pointImage;
            case 'P': return playerImage;
            case 'C': return ghostImage;
            default: return emptyImage;
        }
    }

    private void saveLevel(String levelName) {
        try (FileWriter writer = new FileWriter("src/main/resources/pacman/levels/" + levelName + ".txt")) {
            // Write dimensions in the first row
            writer.write(gridHeight + " " + gridWidth + "\n");
            
            // Write level design
            for (int row = 0; row < gridHeight; row++) {
                for (int col = 0; col < gridWidth; col++) {
                    writer.write(levelGrid[row][col]);
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving level: " + e.getMessage());
        }
    }
}
