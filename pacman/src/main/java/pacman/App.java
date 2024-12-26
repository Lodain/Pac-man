package pacman;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author: Danilo Spera
 * Main application class for the Pacman game.
 * Handles the initialization and navigation between different game screens.
 */
public class App extends Application {

    /** The start screen of the game */
    private final StartScreen startScreen = new StartScreen();
    /** The level selection screen */
    private final LevelSelectScreen levelSelectScreen = new LevelSelectScreen();
    /** The main game screen where gameplay occurs */
    private final LevelScreen levelScreen = new LevelScreen();
    /** The options screen for game settings */
    private final OptionsScreen optionsScreen = new OptionsScreen();
    /** The primary stage of the application */
    private Stage primaryStage;

    /**
     * Initializes the game application and sets up screen navigation callbacks.
     * @param primaryStage The primary stage for the application
     */
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

    /**
     * Shows the start screen and sets up navigation callbacks.
     */
    private void showStartScreen() {
        startScreen.show(primaryStage, 
            () -> levelSelectScreen.show(primaryStage), 
            () -> optionsScreen.show(primaryStage, this::showStartScreen));
    }

    /**
     * Main entry point for the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}