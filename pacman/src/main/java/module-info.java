/**
 * Main module for the Pacman game application.
 * Provides game functionality and JavaFX UI components.
 */
module pacman {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens pacman to javafx.fxml;
    exports pacman;
}
