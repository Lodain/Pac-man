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

    public void show(Stage primaryStage, Runnable onBack) {
        LevelReader levelReader = new LevelReader();
        List<String> levels = levelReader.getAvailableLevels();

        ListView<String> levelListView = new ListView<>();
        levelListView.getItems().addAll(levels);

        levelListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedLevel = levelListView.getSelectionModel().getSelectedItem();
                if (selectedLevel != null && levelSelectedCallback != null) {
                    levelSelectedCallback.accept(selectedLevel);
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> onBack.run());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(levelListView, backButton);

        Scene levelScene = new Scene(layout, 700, 700);
        levelScene.getStylesheets().add(getClass().getResource("/pacman/style/startScreen.css").toExternalForm());

        primaryStage.setScene(levelScene);
    }
}
