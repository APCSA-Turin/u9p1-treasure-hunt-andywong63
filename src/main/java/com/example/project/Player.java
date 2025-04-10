package com.example.project;

//DO NOT DELETE ANY METHODS BELOW
public class Player extends Sprite {
    private int treasureCount;
    private int numLives;
    private boolean win;

    public Player(int x, int y) {
        super(x, y);
        // Initial values
        treasureCount = 0;
        numLives = 2;
        win = false;
    }
    public Player(int x, int y, String levelName) {
        super(x, y);
        if (levelName.equals("easy")) {
            numLives = 3;
        } else {
            numLives = 2;
        }
    }


    public int getTreasureCount() {return treasureCount;}
    public int getLives() {return numLives;}
    public boolean getWin() {return win;}


    //move method should override parent class, sprite
    public void move(String direction) { //move the (x,y) coordinates of the player
        if (direction.equals("w")) {
            setY(getY() + 1); // Up is increase Y
        } else if (direction.equals("a")) {
            setX(getX() - 1); // Left is decrease X
        } else if (direction.equals("s")) {
            setY(getY() - 1); // Down is decrease Y
        } else if (direction.equals("d")) {
            setX(getX() + 1); // Right is increase X
        }
    }


    /**
     * Interact with the sprite the player will move on (called before move() or placeSprite())
     * @param size The grid size
     * @param direction The direction the player will move in
     * @param numTreasures The number of treasures required to win
     * @param obj The sprite to interact with
     */
    public void interact(int size, String direction, int numTreasures, Object obj) { // interact with an object in the position you are moving to 
    //numTreasures is the total treasures at the beginning of the game
        if (isValid(size, direction)) {
            interact(numTreasures, obj);
        }
    }
    // Interact method but without checking validity
    public void interact(int numTreasures, Object obj) {
        if (obj instanceof Enemy) {
            numLives--;
        } else if (obj instanceof Trophy) {
            if (treasureCount >= numTreasures) {
                win = true;
            }
        } else if (obj instanceof Treasure) {
            treasureCount++;
        }
    }

    /**
     * Check if moving in a certain direction is possible without hitting the wall
     * @param size The world size
     * @param direction The direction to move in ("w", "a", "s", or "d")
     * @return Whether the direction is valid or not, or false if invalid input
     */
    public boolean isValid(int size, String direction) {
        if (direction.equals("w")) {
            return !(getY() >= size - 1); // Y position is not at upper limit
        } else if (direction.equals("a")) {
            return !(getX() <= 0); // X position is not at lower limit
        } else if (direction.equals("s")) {
            return !(getY() <= 0); // Y position is not at lower limit
        } else if (direction.equals("d")) {
            return !(getX() >= size - 1); // X position is not at upper limit
        }
        return false; // Invalid input
    }

    @Override
    public String getCoords() {
        return "Player:" + super.getCoords();
    }

    @Override
    public String getRowCol(int size) {
        return "Player:" + super.getRowCol(size);
    }

    @Override
    public String toString() {
        return "\uD83D\uDE00";
    }
}



