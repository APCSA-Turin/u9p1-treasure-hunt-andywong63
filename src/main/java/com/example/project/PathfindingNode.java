package com.example.project;

// Credits: https://medium.com/@nicholas.w.swift/easy-a-star-pathfinding-7e6689c7f7b2

public class PathfindingNode {
    /**
     * Amount of steps to get from start node to current node (less is better)
     */
    private int g;
    /**
     * Heuristic (estimated distance from current node to end node, using Manhattan distance)
     */
    private double h;
    /**
     * Total cost of node (f = g + h)
     */
    private double f;

    private int x;
    private int y;

    private PathfindingNode parentNode;

    public PathfindingNode(int x, int y, PathfindingNode endNode, PathfindingNode parentNode) {
        this.x = x;
        this.y = y;
        g = parentNode.getG() + 1;
        h = Math.abs(endNode.getX() - x) + Math.abs(endNode.getY() - y);
        f = g + h;
        this.parentNode = parentNode;
    }

    public PathfindingNode(int x, int y) {
        this.x = x;
        this.y = y;
        g = 0;
        h = 0;
        f = 0;
        this.parentNode = null;
    }

    public PathfindingNode(int x, int y, PathfindingNode endNode) {
        this.x = x;
        this.y = y;
        g = 0;
        h = Math.abs(endNode.getX() - x) + Math.abs(endNode.getY() - y);
        f = g + h;
        this.parentNode = null;
    }

    public int getG() {
        return g;
    }
    public double getH() {
        return h;
    }
    public double getF() {
        return f;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public PathfindingNode getParent() {
        return parentNode;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Check if another node is on the same node, or has the same X and Y coordinates
     * @param node The other node to check
     * @return Whether the nodes are on each other
     */
    public boolean onNode(PathfindingNode node) {
        return node.getX() == x && node.getY() == y;
    }
}