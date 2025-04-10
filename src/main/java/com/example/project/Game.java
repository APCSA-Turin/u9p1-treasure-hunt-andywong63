package com.example.project;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game {
    private Grid grid;
    private Player player;
    private Enemy[] enemies;
    private Treasure[] treasures;
    private Trophy trophy;
    private int size;
    private String levelName;
    private int enemyMoveTurns; // Amount of player moves before enemies move

    public Game(String levelName) {
        this.levelName = levelName;
        initialize(levelName);
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

        // Enemy will only move every n moves the player makes (depending on difficulty)
        int enemyMove = 1;

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
                    player.getRowCol(size),
                    "Turns before enemy move: " + (enemyMoveTurns - enemyMove)
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
                    if (moveOnSprite instanceof Wall) {
                        // Do not allow player to move onto wall
                        continue;
                    }
                    player.interact(size, direction, treasures.length, moveOnSprite);
                    player.move(direction);
                    grid.placeSprite(player, direction);

                    if (moveOnSprite instanceof Enemy) {
                        // If player moved onto enemy (and took damage), remove enemy from the list
                        enemies[Arrays.asList(enemies).indexOf(moveOnSprite)] = null;
                    }
                }

                // Enemy movement
                if (enemyMove >= enemyMoveTurns) {
                    for (int i = 0; i < enemies.length; i++) {
                        enemyPathfind(enemies[i], i);
                    }
                    enemyMove = 0;
                }
                enemyMove++;
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
                System.out.println(Util.ansiText("Press enter to go back", "37;3"));
                scanner.nextLine();
                break; // Exit game
            }
            // Check if lose
            if (player.getLives() <= 0) {
                clearScreen();
                grid.gameover();
                System.out.println(Util.ansiText("Press enter to go back", "37;3"));
                scanner.nextLine();
                break; // Exit game
            }
        }
    }

    public void initialize(String levelName) {
        // Load the level from the file
        loadLevel(levelName);

        // Assign variables based on level
        if (levelName.equals("hard")) {
            enemyMoveTurns = 3;
        } else {
            enemyMoveTurns = 2;
        }

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

    public void loadLevel(String levelName) {
        try {
            // Load the level file
            File level = new File("src/main/java/com/example/project/levels/" + levelName + ".txt");
            Scanner fileScanner = new Scanner(level);
            // Initialize with -1 for now
            int y = -1;
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                if (y == -1) { // Get values from first line
                    size = line.length();
                    grid = new Grid(size);
                    y = size - 1;
                }
                int x = 0;
                for (String character : line.split("")) {
                    if (character.equals(".")) {
                        grid.placeSprite(new Dot(x, y));
                    } else if (character.equals("W")) {
                        grid.placeSprite(new Wall(x, y));
                    } else if (character.equals("S") && player == null) {
                        player = new Player(x, y, levelName);
                        grid.placeSprite(player);
                    } else if (character.equals("T") && trophy == null) {
                        trophy = new Trophy(x, y);
                        grid.placeSprite(trophy);
                    }
                    x++;
                }
                y--;
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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


    // Make enemy move one step closer to the player, avoiding obstacles, using A* algorithm
    // Credits: https://medium.com/@nicholas.w.swift/easy-a-star-pathfinding-7e6689c7f7b2
    public void enemyPathfind(Enemy enemy, int index) {
        if (enemy == null) return;
        // Stores nodes to be checked (checks in order from smallest F value)
        ArrayList<PathfindingNode> openList = new ArrayList<>();
        // Stores nodes already checked (so they don't get re-checked)
        ArrayList<PathfindingNode> closedList = new ArrayList<>();

        // Player node
        PathfindingNode playerNode = new PathfindingNode(player.getX(), player.getY());
        // Enemy node
        PathfindingNode enemyNode = new PathfindingNode(enemy.getX(), enemy.getY(), playerNode);

        openList.add(enemyNode);

        while (!openList.isEmpty()) {
            // Get next node to process in open list (smallest F value)
            PathfindingNode currentNode = openList.get(0);
            for (PathfindingNode node : openList) {
                if (node.getF() < currentNode.getF()) {
                    currentNode = node;
                }
            }

            // Remove from open list and add to closed list
            openList.remove(currentNode);
            closedList.add(currentNode);

            if (currentNode.onNode(playerNode)) {
                // Found path to player
                // Get last node in path that has a parent (the first value to move to)
                PathfindingNode pathCurrentNode = currentNode;
                while (pathCurrentNode.getParent().getParent() != null) {
                    pathCurrentNode = pathCurrentNode.getParent();
                }

                // Move enemy onto this node
                grid.placeSprite(new Dot(enemy.getX(), enemy.getY())); // Replace old spot with dot
                enemy.setX(pathCurrentNode.getX());
                enemy.setY(pathCurrentNode.getY());

                if (enemy.onSprite(player)) {
                    // Make player interact with enemy if they are on each other
                    player.interact(treasures.length, enemy);
                    enemies[index] = null;
                    // (Do not place enemy on new spot since it replaces the player)
                } else {
                    // Enemy is not touching player, update grid with new spot
                    grid.placeSprite(enemy);
                }
                return;
            }

            // Get the nodes adjacent to the current node (child nodes)
            int x = currentNode.getX();
            int y = currentNode.getY();
            int[][] adjCoordinates = {{x, y + 1}, {x, y - 1}, {x + 1, y}, {x - 1, y}};
            for (int[] coords : adjCoordinates) {
                // Don't create nodes that are out of bounds
                if (coords[0] < 0 || coords[1] < 0 || coords[0] >= size || coords[1] >= size) continue;

                PathfindingNode childNode = new PathfindingNode(coords[0], coords[1], playerNode, currentNode);

                // Don't add node to open list if the position has already been checked (in closed list)
                boolean inClosedList = false;
                for (PathfindingNode node : closedList) {
                    if (childNode.onNode(node)) {
                        inClosedList = true;
                        break;
                    }
                }
                if (inClosedList) continue;

                // Don't go on top of other sprites
                Sprite childSprite = grid.getSprite(childNode.getX(), childNode.getY());
                if (!(childSprite instanceof Dot || childSprite instanceof Player)) continue;

                // Check if node with same position is already in open list
                boolean addToOpenList = true;
                for (int i = 0; i < openList.size(); i++) {
                    PathfindingNode openListNode = openList.get(i);
                    if (openListNode.onNode(childNode)) {
                        // Node with same X and Y value found, check if G in current child is better
                        if (childNode.getG() < openListNode.getG()) {
                            // Current child node goes to same location but faster (smaller G so fewer steps), so remove old one
                            openList.remove(i);
                        } else {
                            // Current child node takes same number of or more steps, use one already in open list
                            addToOpenList = false;
                        }
                        break;
                    }
                }
                if (addToOpenList) {
                    // Add child node to open list
                    openList.add(childNode);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            clearScreen();
            System.out.println(Util.ansiText("Treasure Hunt", "93;1"));
            System.out.println(" [1] " + Util.ansiText("Easy", "32;4"));
            System.out.println(" [2] " + Util.ansiText("Medium", "33;4"));
            System.out.println(" [3] " + Util.ansiText("Hard", "31;4"));
            System.out.println(" [0] " + Util.ansiText("Exit", "37;4"));
            System.out.println();
            System.out.println(Util.ansiText("Please choose a difficulty", "3"));
            System.out.print("> ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                new Game("easy");
            } else if (choice.equals("2")) {
                new Game("medium");
            } else if (choice.equals("3")) {
                new Game("hard");
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
}