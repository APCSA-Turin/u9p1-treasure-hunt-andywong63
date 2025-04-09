package com.example.project;

//Enemy only need constructor and getCoords() getRowCol()
public class Enemy extends Sprite { //child  of Sprite
    
    public Enemy(int x, int y) {
        super(x, y);
    }


    //the methods below should override the super class 

    @Override
    public String getCoords() { //returns "Enemy:"+coordinates
        return "Enemy:" + super.getCoords();
    }

    @Override
    public String getRowCol(int size) { //return "Enemy:"+row col
        return "Enemy:" + super.getRowCol(size);
    }

    @Override
    public String toString() {
        return "\uD83D\uDC79";
    }
}