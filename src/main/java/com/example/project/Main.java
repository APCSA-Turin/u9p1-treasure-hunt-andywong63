package com.example.project;

public class Main {
    public static void main(String[] args) {
        Grid grid = new Grid(10);
        grid.placeSprite(new Player(0, 0));
        grid.placeSprite(new Trophy(8, 8));
        grid.placeSprite(new Treasure(3, 6));
        grid.placeSprite(new Treasure(6, 4));
        grid.display();
    }
}
