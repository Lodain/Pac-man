package pacman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pacman.entities.LevelReader;

/**
 * Screen for selecting and managing game levels.
 * Provides functionality to:
 * - Select and play existing levels
 * - Import custom levels
 * - Create new levels
 * - Delete existing levels
 *
 * @author Danilo Spera
 */
public class LevelSelectScreen {

    /** Callback function executed when a level is selected */
    private Consumer<String> levelSelectedCallback;

    /**
     * Creates a new LevelSelectScreen instance.
     */
    public LevelSelectScreen() {
        // Constructor can be empty if all initialization is done in field declarations
    }

    /**
     * Sets the callback function for level selection.
     * This callback is triggered when a user double-clicks a level.
     * 
     * @param callback Function to execute when a level is selected
     */
    public void setLevelSelectedCallback(Consumer<String> callback) {
        this.levelSelectedCallback = callback;
    }

    /**
     * Displays the level selection interface.
     * Shows a list of available levels and management buttons.
     * 
     * @param primaryStage The main application window
     */
    public void show(Stage primaryStage) {
        // Get list of available levels using LevelReader
        LevelReader levelReader = new LevelReader();
        List<String> levels = levelReader.getAvailableLevels();

        // Create and populate the level list view, removing .txt extensions
        ListView<String> levelListView = new ListView<>();
        levels.forEach(level -> {
            String displayName = level.replace(".txt", "");
            levelListView.getItems().add(displayName);
        });
        levelListView.getStyleClass().add("level-list-view");

        // Handle double-click on level selection
        levelListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
                if (selectedLevel != null && levelSelectedCallback != null) {
                    // Add .txt back when loading the level
                    levelSelectedCallback.accept(selectedLevel + ".txt");
                }
            }
        });

        // Back button - returns to start screen
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            StartScreen startScreen = new StartScreen();
            startScreen.show(primaryStage, 
                () -> show(primaryStage), 
                () -> {
                    OptionsScreen optionsScreen = new OptionsScreen();
                    optionsScreen.show(primaryStage, () -> show(primaryStage));
                });
        });

        // Create Level button - opens level editor
        Button createLevelButton = new Button("Create Level");
        createLevelButton.setOnAction(event -> {
            CreateLevelScreen createLevelScreen = new CreateLevelScreen();
            createLevelScreen.show(primaryStage, () -> show(primaryStage));
        });

        // Import Level button - allows importing external level files
        Button importLevelButton = new Button("Import Level");
        importLevelButton.setOnAction(event -> {
            // Configure file chooser for .txt files
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Level File");
            fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Text Files", "*.txt")
            );

            // Handle selected file
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    // Ensure levels directory exists
                    File levelsDir = new File("src/main/resources/pacman/levels");
                    if (!levelsDir.exists()) {
                        levelsDir.mkdirs();
                    }

                    // Copy imported file to levels directory
                    File destFile = new File(levelsDir, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destFile.toPath(), 
                              StandardCopyOption.REPLACE_EXISTING);

                    // Refresh level list
                    levelListView.getItems().clear();
                    levelListView.getItems().addAll(new LevelReader().getAvailableLevels());
                } catch (IOException e) {
                    System.err.println("Error importing level: " + e.getMessage());
                }
            }
        });

        // Delete Level button - removes selected level after confirmation
        Button deleteLevelButton = new Button("Delete Level");
        deleteLevelButton.getStyleClass().add("delete-button");
        deleteLevelButton.setOnAction(event -> {
            String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
            if (selectedLevel != null) {
                // Show confirmation dialog
                Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
                confirmDialog.setTitle("Delete Level");
                confirmDialog.setHeaderText("Delete " + selectedLevel);
                confirmDialog.setContentText("Are you sure you want to delete this level?");
                
                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Delete the level file and update list if successful
                    File levelFile = new File("src/main/resources/pacman/levels/" + selectedLevel + ".txt");
                    if (levelFile.delete()) {
                        levelListView.getItems().remove(selectedLevel);
                    } else {
                        // Show error if deletion fails
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText("Could not delete the level file.");
                        errorAlert.showAndWait();
                    }
                }
            }
        });

        // Create layout and add all components
        VBox layout = new VBox(15);
        layout.getChildren().addAll(levelListView, createLevelButton, importLevelButton, 
                                  deleteLevelButton, backButton);
        layout.getStyleClass().add("level-selector-layout");

        // Set up and show the scene
        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());
        primaryStage.setScene(levelScene);
    }
}
