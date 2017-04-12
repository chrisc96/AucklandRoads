package com.example.auckland_roads;

import java.awt.*;
import java.util.*;

import static com.example.auckland_roads.RoadMap.map;

public class Node {

    private final int nodeID;
    private final Location location;

    final Color col;
    final int OvalSize = (int) ((map.getDrawingAreaDimension().width/(map.getDrawingAreaDimension().height)) * 4);

    // A* Search Fields:
    boolean visited = false;
    Node pathFrom = null;
    double costFromStart = 0;
    double estimate = 0;
    private final ArrayList<Segment> segmentOut = new ArrayList<>(); // each node has selection of outgoing edges
    private final ArrayList<Segment> segmentIn = new ArrayList<>();

    // Articulation points Fields:
    final ArrayList<Node> neighbours = new ArrayList<>();
    public Queue<Node> unvisitedNeighbours;
    int depth = Integer.MAX_VALUE;
    public int reachBack;

    Node(int nodeID, Double lat, Double lon, Color col) {
        this.nodeID = nodeID;
        this.location = Location.newFromLatLon(lat,lon);
        this.col = col;
    }

    void reset() {
        this.pathFrom = null;
        this.visited = false;
        this.costFromStart = 0;
        this.estimate = 0;
    }

    int getNodeID() {
        return this.nodeID;
    }

    Location getLocation() {
        return location;
    }

    ArrayList<Segment> getSegmentOut() {
        return segmentOut;
    }

    ArrayList<Segment> getSegmentIn() {
        return segmentIn;
    }

    void addToSegmentOut(Segment seg) {
        segmentOut.add(seg);
    }

    void addToSegmentIn(Segment seg) {
        segmentIn.add(seg);
    }
}
