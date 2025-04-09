package com.example.project;


//DO NOT DELETE ANY METHODS BELOW
public class Grid {
    /**
     * The 2D array representing the grid (not in Cartesian plane index)
     */
    private Sprite[][] grid;
    /**
     * The grid size
     */
    private int size;

    public Grid(int size) { //initialize and create a grid with all DOT objects
        this.size = size;
        grid = new Sprite[size][size]; // Square grid with size length
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                placeSprite(new Dot(i, j)); // Initialize dots on all squares
            }
        }
    }

 
    public Sprite[][] getGrid() {return grid;}

    /**
     * Get the sprite at the Cartesian coordinates
     * @param x X value
     * @param y Y value
     * @return The sprite at the specified coordinates
     */
    public Sprite getSprite(int x, int y) {
        return grid[size - y - 1][x]; // Convert y value to row index with size - y - 1
    }


    public void placeSprite(Sprite s) { //place sprite in new spot
        grid[s.getRow(size)][s.getX()] = s;
    }

    public void placeSprite(Sprite s, String direction) { //place sprite in a new spot based on direction
        // Get previous coordinates the sprite was on (since sprite already moved)
        int prevX = s.getX();
        int prevY = s.getY();
        // Get opposite of the direction moved
        if (direction.equals("w")) {
            prevY--;
        } else if (direction.equals("a")) {
            prevX++;
        } else if (direction.equals("s")) {
            prevY++;
        } else if (direction.equals("d")) {
            prevX--;
        } else {
            return; // Invalid key, don't move any sprites
        }
        // Replace the old spot with a dot
        placeSprite(new Dot(prevX, prevY));
        // Add the player to the new spot
        placeSprite(s);
    }


    public void display() {
        // Loop through each row
        for (Sprite[] row : grid) {
            // Loop through each value in row and print
            for (Sprite sprite : row) {
                System.out.print(sprite);
            }
            System.out.println();
        }
    }

    /**
     * Display the grid with text to the right of the grid
     * @param nextToText An array of strings to show with each value on a new line
     */
    public void display(String[] nextToText) {
        int rowNum = 0;
        // Do same thing as display() with no parameters
        for (Sprite[] row : grid) {
            for (Sprite sprite : row) {
                System.out.print(sprite);
            }
            // If haven't finished displaying everything in nextToText, show value at index of current row
            if (nextToText.length > rowNum && nextToText[rowNum] != null) {
                System.out.print(" " + nextToText[rowNum]);
            }
            System.out.println();
            rowNum++;
        }
    }
    
    public void gameover() {
        // Credits: https://gist.github.com/flaviut/0db1aec4cadf2ef06455
        System.out.println("┌─────────┐");
        System.out.print("│");
        System.out.print(Util.ansiText("You Lose!", "31"));
        System.out.println("│");
        System.out.println("└─────────┘");
    }

    public void win() {
        // Credits: https://gist.github.com/flaviut/0db1aec4cadf2ef06455
        System.out.println("┌────────┐");
        System.out.print("│");
        System.out.print(Util.ansiText("You Win!", "32"));
        System.out.println("│");
        System.out.println("└────────┘");
    }


}