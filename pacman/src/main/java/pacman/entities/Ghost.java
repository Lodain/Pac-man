package pacman.entities;

/**
 * Represents a ghost enemy in the game.
 * Ghosts move around the level and can kill Pacman on contact.
 */
public class Ghost {
    /** Current row position of the ghost */
    private int row;
    
    /** Current column position of the ghost */
    private int col;
    
    /** The color index of the ghost */
    private int color;

    /**
     * Creates a new ghost at the specified position with a given color.
     * @param row Initial row position
     * @param col Initial column position
     * @param color Color index of the ghost
     */
    public Ghost(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    /**
     * Gets the current row position.
     * @return The row position
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row position.
     * @param row The new row position
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Gets the current column position.
     * @return The column position
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the column position.
     * @param col The new column position
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Gets the color index of the ghost.
     * @return The color index
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color index of the ghost.
     * @param color The new color index
     */
    public void setColor(int color) {
        this.color = color;
    }
} 