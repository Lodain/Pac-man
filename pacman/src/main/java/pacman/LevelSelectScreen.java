package pacman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pacman.entities.LevelReader;

public class LevelSelectScreen {

    private Consumer<String> levelSelectedCallback;

    public void setLevelSelectedCallback(Consumer<String> callback) {
        this.levelSelectedCallback = callback;
    }

    public void show(Stage primaryStage) {
        LevelReader levelReader = new LevelReader();
        List<String> levels = levelReader.getAvailableLevels();

        ListView<String> levelListView = new ListView<>();
        levelListView.getItems().addAll(levels);
        levelListView.getStyleClass().add("level-list-view");

        levelListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
                if (selectedLevel != null && levelSelectedCallback != null) {
                    levelSelectedCallback.accept(selectedLevel);
                }
            }
        });

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

        Button createLevelButton = new Button("Create Level");
        createLevelButton.setOnAction(event -> {
            CreateLevelScreen createLevelScreen = new CreateLevelScreen();
            createLevelScreen.show(primaryStage, () -> show(primaryStage));
        });

        Button importLevelButton = new Button("Import Level");
        importLevelButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Level File");
            fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Text Files", "*.txt")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    // Create levels directory if it doesn't exist
                    File levelsDir = new File("src/main/resources/pacman/levels");
                    if (!levelsDir.exists()) {
                        levelsDir.mkdirs();
                    }

                    // Copy file to levels directory
                    File destFile = new File(levelsDir, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destFile.toPath(), 
                              StandardCopyOption.REPLACE_EXISTING);

                    // Refresh the list
                    levelListView.getItems().clear();
                    levelListView.getItems().addAll(new LevelReader().getAvailableLevels());
                } catch (IOException e) {
                    System.err.println("Error importing level: " + e.getMessage());
                }
            }
        });

        VBox layout = new VBox(15);
        layout.getChildren().addAll(levelListView, createLevelButton, importLevelButton, backButton);
        layout.getStyleClass().add("level-selector-layout");

        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(levelScene);
    }
}
