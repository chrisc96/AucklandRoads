package com.example.auckland_roads;

import java.util.ArrayList;
import java.util.List;

import static com.example.auckland_roads.RoadMap.map;

public class Segment {

    public int roadID;
    private double length;
    public Node nodeID1;
    public Node nodeID2;
    private List<Location> coords = new ArrayList<>();

    public Segment(int roadID, double length, int nodeID1, int nodeID2,  List<Location> coords) {
        this.roadID = roadID;
        this.length = length;
        this.nodeID1 = map.nodeMap.get(nodeID1);
        this.nodeID2 = map.nodeMap.get(nodeID2);
        this.coords = coords;
    }

    public int getRoadID() {
        return roadID;
    }

    public void setRoadID(int roadID) {
        this.roadID = roadID;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<Location> getCoords() {
        return coords;
    }

    public void setCoords(List<Location> coords) {
        this.coords = coords;
    }

    public void addCoord(Location coord) { this.coords.add(coord); }

    @Override
    public String toString() {
        return "Segment{" +
                "roadID=" + roadID +
                ", length=" + length +
                ", nodeID1=" + nodeID1 +
                ", nodeID2=" + nodeID2 +
                ", coords=" + coords +
                '}';
    }
}
