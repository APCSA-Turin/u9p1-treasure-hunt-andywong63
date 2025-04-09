package com.example.project;

//only needs a constructor
public class Treasure extends Sprite { //child of Sprite
    public Treasure(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "\uD83D\uDCB0";
    }
}