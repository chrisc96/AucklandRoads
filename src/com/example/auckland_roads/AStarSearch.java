package com.example.auckland_roads;

import java.util.Comparator;
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

            Node oneEnd = priorityNode.currentNode;

            for (Segment segs : priorityNode.currentNode.getSegmentOut()) {

                Node neighbour = segs.theOtherEnd(oneEnd);

                assert neighbour != null;

                if (!isOneWay(oneEnd, segs, neighbour)) {
                    if (!neighbour.visited) {
                        double costToNeighbour = priorityNode.costFromStart + segs.getLength();
                        double estimatedTotal = costToNeighbour + neighbour.getLocation().distance(goal.getLocation());
                        fringe.offer(new AStarSearchNode(neighbour, priorityNode.currentNode, costToNeighbour, estimatedTotal));
                    }
                }
            }
        }
        return this.goal;
    }


    private static boolean isOneWay(Node oneEnd, Segment seg, Node theOtherEnd) {
        return ((RoadMap.roadMap.get(seg.getRoadID()).oneWay)) && oneEnd.getNodeID() == seg.nodeID2.getNodeID()
                && theOtherEnd.getNodeID() == seg.nodeID1.getNodeID();
    }
}


