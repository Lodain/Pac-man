package pacman;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
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

        VBox layout = new VBox(15);
        layout.getChildren().addAll(levelListView, createLevelButton, backButton);
        layout.getStyleClass().add("level-selector-layout");

        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(levelScene);
    }
}
