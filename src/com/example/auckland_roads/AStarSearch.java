package com.example.auckland_roads;

import java.util.PriorityQueue;

public class AStarSearch {
    private Node start, goal;
    private PriorityQueue<AStarSearchNode> fringe = new PriorityQueue<>();

    private AStarSearchNode startNode;

    public AStarSearch(Node start, Node end) {
        this.start = start;
        this.goal = end;
        this.start.visited = true;
        this.start.estimate = this.start.getLocation().distance(this.goal.getLocation());
        startNode = new AStarSearchNode(this.start, null, 0, this.start.estimate);
    }

    public Node search() {
        fringe.add(startNode);

        AStarSearchNode priorityNode = null;
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
