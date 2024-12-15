package pacman.entities;

import javafx.scene.layout.GridPane;

public class Player {
    private int row;
    private int col;
    private String direction = "RIGHT"; // Initial direction

    public Player(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void move(GridPane gridPane, char[][] levelData) {
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case "UP":
                newRow--;
                break;
            case "DOWN":
                newRow++;
                break;
            case "LEFT":
                newCol--;
                break;
            case "RIGHT":
                newCol++;
                break;
        }

        // Check if movement is valid
        if (Movement.checkMovement(levelData[newRow][newCol]) == 1) {
            levelData[row][col] = '.';
            levelData[newRow][newCol] = 'P';
            row = newRow;
            col = newCol;

            // Redraw the grid
            gridPane.getChildren().clear();
            // Assuming loadLevel is a method to redraw the grid
            // This might need to be adjusted based on your actual implementation
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}