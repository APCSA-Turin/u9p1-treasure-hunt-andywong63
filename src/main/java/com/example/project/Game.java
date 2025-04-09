package com.example.project;
import java.util.Scanner;

public class Game {
    private Grid grid;
    private Player player;
    private Enemy[] enemies;
    private Treasure[] treasures;
    private Trophy trophy;
    private int size;

    public Game(int size) {
        this.size = size;
        initialize();
        play();
    }

    public static void clearScreen() { //do not modify
        try {
            final String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Unix-based (Linux, macOS)
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        // Store whether the player took damage or collected treasure on last move to show color
        boolean justTookDamage = false;
        boolean justCollectedTreasure = false;

        while (true) {
            clearScreen(); // Clear the screen at the beginning of the while loop

            String healthDisplay = "❤️ " + player.getLives();
            if (justTookDamage) {
                // Make health number red if player just took damage
                healthDisplay = "❤️ " + Util.ansiText("" + player.getLives(), "31"); // 31 = ANSI red
                justTookDamage = false;
            }
            String treasureDisplay = "\uD83D\uDCB0" + player.getTreasureCount() + "/" + treasures.length;
            if (justCollectedTreasure) {
                // Make treasure number yellow if player just collected treasure
                treasureDisplay = "\uD83D\uDCB0️" + Util.ansiText(player.getTreasureCount() + "/" + treasures.length, "33"); // 33 = ANSI yellow
                justCollectedTreasure = false;
            }
            if (player.getTreasureCount() == treasures.length) {
                // Make treasure number always green if player collected all treasures
                treasureDisplay = "\uD83D\uDCB0️" + Util.ansiText(player.getTreasureCount() + "/" + treasures.length, "32"); // 32 = ANSI green
            }

            // HUD next to the grid
            String[] nextToText = {
                    healthDisplay,
                    treasureDisplay,
                    player.getCoords(),
                    player.getRowCol(size)
            };
            grid.display(nextToText);
            System.out.println("Enter WASD to move around, or Q to quit");


            String input = scanner.nextLine().toLowerCase();
            if (input.equals("q")) {
                break; // Exit loop if quitting
            }

            int oldLives = player.getLives(); // Store old lives to detect if damage was taken
            int oldTreasureCount = player.getTreasureCount(); // Store old lives to detect if damage was taken

            boolean escapeSkip = false; // Arrow key is represented by "\u001B" + "[" + (letter), so skip the "["
            boolean arrowMovement = false; // Stores if the next character is arrow movement
            for (String direction : input.split("")) { // Get each character pressed (so "www" moves up 3 times)
                // Add support for arrow key movement
                if (direction.equals("\u001B")) { // \u001B is escape character to indicate arrow key
                    escapeSkip = true;
                    continue;
                }
                if (escapeSkip) {
                    // Current character is "[", skip this and indicate that next character is arrow movement
                    escapeSkip = false;
                    arrowMovement = true;
                    continue;
                }
                if (arrowMovement) {
                    arrowMovement = false;
                    // Up is A, down is B, right ic C, left is D
                    if (direction.equals("a")) {
                        direction = "w";
                    } else if (direction.equals("b")) {
                        direction = "s";
                    } else if (direction.equals("c")) {
                        direction = "d";
                    } else if (direction.equals("d")) {
                        direction = "a";
                    } else {
                        continue;
                    }
                }

                // Do movement
                if (player.isValid(size, direction)) {
                    // Get position of tile to move on to interact with
                    int newX = player.getX();
                    int newY = player.getY();
                    if (direction.equals("w")) {
                        newY++;
                    } else if (direction.equals("a")) {
                        newX--;
                    } else if (direction.equals("s")) {
                        newY--;
                    } else if (direction.equals("d")) {
                        newX++;
                    }
                    Sprite moveOnSprite = grid.getSprite(newX, newY); // Sprite to move onto
                    if (moveOnSprite instanceof Trophy && player.getTreasureCount() < treasures.length) {
                        // If player is trying to move onto trophy without collecting all the treasures, don't move
                        continue;
                    }
                    player.interact(size, direction, treasures.length, moveOnSprite);
                    player.move(direction);
                    grid.placeSprite(player, direction);
                }
            }
            if (player.getLives() < oldLives) {
                // Player took damage
                justTookDamage = true;
            }
            if (player.getTreasureCount() > oldTreasureCount) {
                // Player collected treasure
                justCollectedTreasure = true;
            }


            // Check if win
            if (player.getWin()) {
                clearScreen();
                grid.win();
                break; // Exit game
            }
            // Check if lose
            if (player.getLives() <= 0) {
                clearScreen();
                grid.gameover();
                break; // Exit game
            }

//            try {
//                Thread.sleep(100); // Wait for 1/10 seconds
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void initialize() {
        // Create grid, player, and trophy
        grid = new Grid(size);
        player = new Player(0, 0);
        trophy = new Trophy(size - 2, size - 2);

        // Place player and trophy on grid
        grid.placeSprite(player);
        grid.placeSprite(trophy);

        // Randomly generate treasure and enemies
        treasures = new Treasure[3];
        enemies = new Enemy[3];
        for (int i = 0; i < 3; i++) {
            placeRandomTreasure(i);
        }
        for (int i = 0; i < 3; i++) {
            placeRandomEnemy(i);
        }
    }

    public void placeRandomTreasure(int index) {
        int[] randomSpot = randomEmptySpot();
        Treasure treasure = new Treasure(randomSpot[0], randomSpot[1]);
        grid.placeSprite(treasure);
        treasures[index] = treasure;
    }

    public void placeRandomEnemy(int index) {
        int[] randomSpot = randomEmptySpot();
        Enemy enemy = new Enemy(randomSpot[0], randomSpot[1]);
        grid.placeSprite(enemy);
        enemies[index] = enemy;
    }

    /**
     * Get a random XY pair of an empty spot on the grid
     * @return A list where index 0 is the X value and index 1 is the Y value
     */
    public int[] randomEmptySpot() {
        int randomX = (int) (Math.random() * size);
        int randomY = (int) (Math.random() * size);
        if (grid.getSprite(randomX, randomY) instanceof Dot) {
            // No object on coordinates, return pair
            int[] xyPair = {randomX, randomY};
            return xyPair;
        } else {
            // An object already exists on specified coordinates, retry random
            return randomEmptySpot();
        }
    }

    public static void main(String[] args) {
        Game game = new Game(10);
    }
}