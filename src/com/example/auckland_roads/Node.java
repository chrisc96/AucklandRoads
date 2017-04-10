package com.example.auckland_roads;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.auckland_roads.RoadMap.map;

public class Node {

    private int nodeID;
    private Location location;
    private double latitude;
    private double longitude;
    private boolean selected = false;

    public Color col;
    public int OvalSize = (int) ((map.getDrawingAreaDimension().width/(map.getDrawingAreaDimension().height)) * 4);

    // A* Search Fields:
    public boolean visited = false;
    public Node startNode = this;
    public Node pathFrom = null;
    public double costFromStart = 0;
    public double estimate = 0;
    public double totalCost = 0;
    private ArrayList<Segment> segmentOut = new ArrayList<>(); // each node has selection of outgoing edges
    private ArrayList<Segment> segmentIn = new ArrayList<>();



    public Node(int nodeID, Double lat, Double lon, Color col) {
        this.nodeID = nodeID;
        this.latitude = lat;
        this.longitude = lon;
        this.location = Location.newFromLatLon(lat,lon);
        this.col = col;
    }

    public void reset() {
        this.startNode = null;
        this.pathFrom = null;
        this.visited = false;
        this.costFromStart = 0;
        this.estimate = 0;
        this.totalCost = 0;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getNodeID() {
        return this.nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public Location getLocation() {
        return location;
    }

    public ArrayList<Segment> getSegmentOut() {
        return segmentOut;
    }

    public ArrayList<Segment> getSegmentIn() {
        return segmentIn;
    }

    public void addToSegmentOut(Segment seg) {
        segmentOut.add(seg);
    }

    public void addToSegmentIn(Segment seg) {
        segmentIn.add(seg);
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeID=" + nodeID +
                ", location=" + location +
                ", costFromStart=" + costFromStart +
                ", estimate=" + estimate +
                ", totalCost=" + totalCost +
                '}';
    }
}
