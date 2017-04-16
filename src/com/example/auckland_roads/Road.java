package com.example.auckland_roads;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Road {

    private final int roadID;
    private final int type;
    private final String name;
    private final String city;
    final boolean oneWay;     // 0 - both ways allowed
                            // 1 - one way (from beginning to end of road)

    private final int speedLim;   // 0 - 5
                            // 1 - 20
                            // 2 - 40
                            // 3 - 60
                            // 4 - 80
                            // 5 - 100
                            // 6 - 110
                            // 7 - no limit

    private final int roadClass;  // 0 - Residential
                            // 1 - Collector
                            // 2 - Arterial
                            // 3 - Principal Highway
                            // 4 - Major Highway

    private final
    boolean carNotAllowed;          // false - car/bike/etc allowed
            private final boolean bikeNotAllowed;         // true - car/bike/etc not allowed
            private final boolean PedestrianNotAllowed;

    private final Color col;
    private final BasicStroke bs;

    private final List<Segment> segments = new ArrayList<>();

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
        this.oneWay = notOneWay != 0;
        this.carNotAllowed = carNotAllowed != 0;
        this.bikeNotAllowed = bikeNotAllowed != 0;
        this.PedestrianNotAllowed = PedestrianNotAllowed != 0;
    }

    BasicStroke getBs() {
        return bs;
    }

    Color getCol() {
        return col;
    }

    String getName() {
        return name;
    }

    String getCity() {
        return city;
    }

    int getRoadClass() {
        return roadClass;
    }

    List<Segment> getSegments() {
        return segments;
    }

    void addToSegments(Segment seg) {
        segments.add(seg);
    }


    private int determineWidthRoad(int roadClass) {
        int width = 0;
        switch (roadClass) {
            case 0:
                width = 2;
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
                width = 4;
                break;
        }
        return width;
    }
}
