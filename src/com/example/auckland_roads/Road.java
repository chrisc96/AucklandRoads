package com.example.auckland_roads;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Road {

    private int roadID;
    private int type;
    private String name;
    private String city;
    private boolean notOneWay;     // 0 - both ways allowed
                            // 1 - one way (from beginning to end of road)

    private int speedLim;   // 0 - 5
                            // 1 - 20
                            // 2 - 40
                            // 3 - 60
                            // 4 - 80
                            // 5 - 100
                            // 6 - 110
                            // 7 - no limit

    private int roadClass;  // 0 - Residential
                            // 1 - Collector
                            // 2 - Arterial
                            // 3 - Principal Highway
                            // 4 - Major Highway

    private
    boolean carNotAllowed,          // false - car/bike/etc allowed
            bikeNotAllowed,         // true - car/bike/etc not allowed
            PedestrianNotAllowed;

    private Color col;
    private BasicStroke bs;

    private List<Segment> segments = new ArrayList<>();

    public Road(int roadID, int type, String name, String city, int notOneWay, int speedLim, int roadClass, int carNotAllowed, int bikeNotAllowed, int PedestrianNotAllowed, Color col) {
        this.roadID = roadID;
        this.type = type;
        this.name = name;
        this.city = city;
        this.speedLim = speedLim;
        this.roadClass = roadClass;
        this.col = col;
        bs = new BasicStroke(determineWidthRoad(roadClass));

        // Converting these to booleans makes more logical sense
        this.notOneWay = (notOneWay == 0) ? false : true;
        this.carNotAllowed = (carNotAllowed == 0) ? false : true;
        this.bikeNotAllowed = (bikeNotAllowed == 0) ? false : true;
        this.PedestrianNotAllowed = (PedestrianNotAllowed == 0) ? false : true;
    }

    public BasicStroke getBs() {
        return bs;
    }

    public Color getCol() {
        return col;
    }

    public int getRoadID() {
        return roadID;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public boolean isNotOneWay() {
        return notOneWay;
    }

    public int getSpeedLim() {
        return speedLim;
    }

    public int getRoadClass() {
        return roadClass;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void addToSegments(Segment seg) {
        segments.add(seg);
    }

    public boolean isCarNotAllowed() {
        return carNotAllowed;
    }

    public boolean isBikeNotAllowed() {
        return bikeNotAllowed;
    }

    public boolean isPedestrianNotAllowed() {
        return PedestrianNotAllowed;
    }

    @Override
    public String toString() {
        return "Road{" +
                "roadID=" + roadID +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", notOneWay=" + notOneWay +
                ", speedLim=" + speedLim +
                ", roadClass=" + roadClass +
                ", carNotAllowed=" + carNotAllowed +
                ", bikeNotAllowed=" + bikeNotAllowed +
                ", PedestrianNotAllowed=" + PedestrianNotAllowed +
                ", segments=" + segments +
                '}';
    }

    public int determineWidthRoad(int roadClass) {
        int width = 0;
        switch (roadClass) {
            case 0:
                width = 1;
                break;
            case 1:
                width = 2;
                break;
            case 2:
                width = 2;
                break;
            case 3:
                width = 3;
                break;
            case 4:
                width = 3;
                break;
        }
        return width;
    }

}
