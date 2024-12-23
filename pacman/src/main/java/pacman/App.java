package pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final StartScreen startScreen = new StartScreen();
    private final LevelSelectScreen levelSelectScreen = new LevelSelectScreen();
    private final LevelScreen levelScreen = new LevelScreen();
    private final OptionsScreen optionsScreen = new OptionsScreen();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        levelSelectScreen.setLevelSelectedCallback(selectedLevel -> {
            levelScreen.setSpeed(optionsScreen.getSelectedSpeed());
            levelScreen.loadLevel(primaryStage, selectedLevel);
        });

        levelScreen.setReturnToMenuCallback(() -> showStartScreen());

        showStartScreen();
    }

    private void showStartScreen() {
        startScreen.show(primaryStage, 
            () -> levelSelectScreen.show(primaryStage), 
            () -> optionsScreen.show(primaryStage, this::showStartScreen));
    }

    public static void main(String[] args) {
        launch(args);
    }
}