package com.example.auckland_roads;


import java.util.*;

public class ArticulationPts {
    public static Set<Node> artPts = new HashSet<>();
    public Stack<ArticulationPtsTuple> stack = new Stack<>();

    int numSubTrees = 0;

    Node root;
    ArticulationPtsTuple rootTuple;

    public ArticulationPts(Node firstNode, int depth, ArticulationPtsTuple parent) {
        numSubTrees = 0;

        firstNode.depth = 0;
        this.root = firstNode;
        rootTuple = new ArticulationPtsTuple(root, depth, parent);
    }

    public void findArtPts() {
        for (Node neighbour : root.neighbours) {
            iterArtPts(neighbour, 1, rootTuple);
            numSubTrees++;
        }
        if (numSubTrees > 1) artPts.add(root);
    }

    private void iterArtPts(Node currNode, int depth, ArticulationPtsTuple rootTuple) {
        stack.push(new ArticulationPtsTuple(currNode, depth, rootTuple));

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

    public Set<Node> getArtPts() {
        return artPts;
    }
}
