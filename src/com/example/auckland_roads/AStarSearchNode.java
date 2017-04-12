package com.example.auckland_roads;

class AStarSearchNode implements Comparable<AStarSearchNode>{

    Node currentNode = null;
    Node fromNode = null;
    double costFromStart = 0;
    private double totalCost = 0;

    AStarSearchNode(Node currentNode, Node fromNode, double costFromStart, double totalCost) {
        this.currentNode = currentNode;
        this.fromNode = fromNode;
        this.costFromStart = costFromStart;
        this.totalCost = totalCost;
    }

    @Override
    public int compareTo(AStarSearchNode other) {
        if (this.totalCost < other.totalCost) { return -1; }
        else if (this.totalCost > other.totalCost) { return 1; }
        else { return 0; }
    }
}