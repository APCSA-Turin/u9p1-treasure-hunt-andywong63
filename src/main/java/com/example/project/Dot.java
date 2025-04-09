package com.example.project;


//Dot only needs a constructor
public class Dot extends Sprite {
    public Dot(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "..";
    }
}