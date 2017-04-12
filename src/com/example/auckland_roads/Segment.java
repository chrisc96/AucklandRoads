package com.example.auckland_roads;

import java.util.ArrayList;
import java.util.List;

class Segment {

    final int roadID;
    private final double length;
    final Node nodeID1;
    final Node nodeID2;
    private List<Location> coords = new ArrayList<>();

    Segment(int roadID, double length, int nodeID1, int nodeID2, List<Location> coords) {
        this.roadID = roadID;
        this.length = length;
        this.nodeID1 = RoadMap.nodeMap.get(nodeID1);
        this.nodeID2 = RoadMap.nodeMap.get(nodeID2);
        this.coords = coords;
    }

    int getRoadID() {
        return roadID;
    }

    double getLength() {
        return length;
    }

    List<Location> getCoords() {
        return coords;
    }
}
