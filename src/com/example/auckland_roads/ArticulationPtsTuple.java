package com.example.auckland_roads;

import java.util.Queue;

public class ArticulationPtsTuple {

    // Constructor fields
    public Node node;
    public ArticulationPtsTuple fromNode;
    public int depth;

    public double reachBack;
    public Queue<Node> children;

    public ArticulationPtsTuple(Node currNode, int depth, ArticulationPtsTuple fromNode) {
        this.node = currNode;
        this.depth = depth;
        this.fromNode = fromNode;
    }
}
