package com.example.auckland_roads;


import java.util.*;

class ArticulationPts {
    private static final Set<Node> artPts = new HashSet<>();
    private final Stack<ArticulationPtsTuple> stack = new Stack<>();

    private int numSubTrees = 0;

    private final Node root;
    private final ArticulationPtsTuple rootTuple;

    ArticulationPts(Node firstNode) {
        numSubTrees = 0;

        firstNode.depth = 0;
        this.root = firstNode;
        rootTuple = new ArticulationPtsTuple(root, 0, null);
    }

    void findArtPts() {
        for (Node neighbour : root.neighbours) {
            iterArtPts(neighbour, rootTuple);
            numSubTrees++;
        }
        if (numSubTrees > 1) artPts.add(root);
    }

    private void iterArtPts(Node currNode, ArticulationPtsTuple rootTuple) {
        stack.push(new ArticulationPtsTuple(currNode, 1, rootTuple));

        while (!stack.isEmpty()) {
            ArticulationPtsTuple tuple = stack.peek();
            if (tuple.children == null) {
                tuple.node.depth = tuple.depth;
                tuple.reachBack = tuple.depth;
                tuple.children = new ArrayDeque<>();
                for (Node neighbour : tuple.node.neighbours) {
                    if (neighbour != tuple.fromNode.node) {
                        tuple.children.add(neighbour);
                    }
                }
            }
            else if (!tuple.children.isEmpty()) {
                Node child = tuple.children.poll();
                if (child.depth < Integer.MAX_VALUE) {
                    tuple.reachBack = Math.min(tuple.reachBack, child.depth);
                }
                else {
                    stack.push(new ArticulationPtsTuple(child, tuple.node.depth + 1, tuple));
                }
            }
            else {
                if (tuple.node != currNode) {
                    if (tuple.reachBack >= tuple.fromNode.depth) {
                        artPts.add(tuple.fromNode.node);
                    }
                    tuple.fromNode.reachBack = Math.min(tuple.fromNode.reachBack, tuple.reachBack);
                }
                stack.pop();
            }
        }
    }

    Set<Node> getArtPts() {
        return artPts;
    }
}
