module pacman {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens pacman to javafx.fxml;
    exports pacman;
}
