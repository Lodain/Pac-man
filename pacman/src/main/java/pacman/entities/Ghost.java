package pacman.entities;

public class Ghost {
    private int row;
    private int col;
    private char last;
    private int color;

    public Ghost(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.last = '.'; // Initialize with '.'
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
} 