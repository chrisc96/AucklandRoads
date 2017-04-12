package com.example.auckland_roads;

import java.util.Queue;

class ArticulationPtsTuple {

    // Constructor fields
    public final Node node;
    final ArticulationPtsTuple fromNode;
    final int depth;

    double reachBack;
    Queue<Node> children;

    ArticulationPtsTuple(Node currNode, int depth, ArticulationPtsTuple fromNode) {
        this.node = currNode;
        this.depth = depth;
        this.fromNode = fromNode;
    }
}
