package com.example.auckland_roads;

public class AStarSearchNode implements Comparable<AStarSearchNode>{

    public Node currentNode = null;
    public Node fromNode = null;
    public double costFromStart = 0;
    public double totalCost = 0;

    public AStarSearchNode(Node currentNode, Node fromNode, double costFromStart, double totalCost) {
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