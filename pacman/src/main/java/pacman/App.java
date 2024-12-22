package pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final StartScreen startScreen = new StartScreen();
    private final LevelSelectScreen levelSelectScreen = new LevelSelectScreen();
    private final LevelScreen levelScreen = new LevelScreen();

    @Override
    public void start(Stage primaryStage) {
        levelSelectScreen.setLevelSelectedCallback(selectedLevel -> {
            levelScreen.loadLevel(primaryStage, selectedLevel);
        });

        levelScreen.setReturnToMenuCallback(() -> {
            startScreen.show(primaryStage, 
                () -> levelSelectScreen.show(primaryStage), 
                this::handleOptionsButton);
        });

        startScreen.show(primaryStage, 
            () -> levelSelectScreen.show(primaryStage), 
            this::handleOptionsButton);
    }

    private void handleOptionsButton() {
        System.out.println("Options button clicked!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}