package pacman.entities;

public class Ghost {
    private int row;
    private int col;
    private char last;

    public Ghost(int row, int col) {
        this.row = row;
        this.col = col;
        this.last = '.'; // Initialize with '.'
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public char getLast() {
        return last;
    }

    public void setLast(char last) {
        this.last = last;
    }
} 