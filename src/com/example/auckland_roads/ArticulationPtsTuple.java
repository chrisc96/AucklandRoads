package com.example.auckland_roads;

public class ArticulationPtsTuple {

    // Constructor fields
    private Node currNode;
    private int depth;
    private Node fromNode;

    private int reachBack;

    public ArticulationPtsTuple(Node currNode, int depth, Node fromNode) {
        this.currNode = currNode;
        this.depth = depth;
        this.fromNode = fromNode;
    }

}
