package com.example.auckland_roads;

import java.util.PriorityQueue;

class AStarSearch {
    private final Node goal;
    private final PriorityQueue<AStarSearchNode> fringe = new PriorityQueue<>();

    private final AStarSearchNode startNode;

    AStarSearch(Node start, Node end) {
        this.goal = end;
        start.visited = true;
        start.estimate = start.getLocation().distance(this.goal.getLocation());
        startNode = new AStarSearchNode(start, null, 0, start.estimate);
    }

    Node search() {
        fringe.add(startNode);

        AStarSearchNode priorityNode;
        while (!fringe.isEmpty()) {
            priorityNode = fringe.poll();

            if (!priorityNode.currentNode.visited) {
                priorityNode.currentNode.visited = true;
                priorityNode.currentNode.pathFrom = priorityNode.fromNode;
                priorityNode.currentNode.costFromStart = priorityNode.costFromStart;
            }

            if (priorityNode.currentNode == this.goal) break;

            for (Segment segs : priorityNode.currentNode.getSegmentOut()) {
                Node neighbour = null;
                if (priorityNode.currentNode.getNodeID() == segs.nodeID1.getNodeID()) neighbour = segs.nodeID2;
                if (priorityNode.currentNode.getNodeID() == segs.nodeID2.getNodeID()) neighbour = segs.nodeID1;

                assert neighbour != null;
                if (!neighbour.visited) {
                    double costToNeighbour = priorityNode.costFromStart + segs.getLength();
                    double estimatedTotal = costToNeighbour + neighbour.getLocation().distance(goal.getLocation());
                    fringe.offer(new AStarSearchNode(neighbour, priorityNode.currentNode, costToNeighbour, estimatedTotal));
                }
            }
        }
        return this.goal;
    }
}
