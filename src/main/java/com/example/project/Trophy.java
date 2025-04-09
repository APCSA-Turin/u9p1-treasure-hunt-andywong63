package com.example.project;

//only needs a constructor
public class Trophy extends Treasure { //child of trophy
    public Trophy(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "\uD83C\uDFC6";
    }
}
