package com.example.project;

public class Sprite {
    /**
     * The X position (on a Cartesian plane) <br>
     * - Equal to column index
     */
    private int x;
    /**
     * The Y position (on a Cartesian plane) <br>
     * - Equal to (size - row index - 1) <br>
     * - Can be used to get row index with (size - y - 1)
     */
    private int y;

    /**
     * Creates a new Sprite
     * @param x The X position (in Cartesian plane)
     * @param y The Y position (in Cartesian plane)
     */
    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    /**
     * Get the 2D array row index of the sprite
     * @param size The grid size
     * @return The row index
     */
    public int getRow(int size) {
        return size - y - 1; // Row index is calculated with size - y - 1
    }

    /**
     * Get the coordinates of the sprite, in Cartesian plane format
     * @return The coordinates
     */
    public String getCoords() { //returns the coordinates of the sprite ->"(x,y)"
        return "(" + x + "," + y + ")";
    }

    /**
     * Get the coordinates of the sprite, in 2D array row column index format
     * @param size The size of the grid
     * @return The coordinates
     */
    public String getRowCol(int size){
        return "[" + getRow(size) + "][" + x + "]";
    }

    /**
     * Moves the sprite in a certain direction
     * @param direction The direction, in "w", "a", "s", or "d"
     */
    public void move(String direction) {
        // Default behavior (can be overridden by subclasses)
    }

    public void interact() { //you can leave this empty
        // Default behavior (can be overridden by subclasses)
    }



}
