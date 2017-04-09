package com.example.auckland_roads;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.auckland_roads.RoadMap.roadMap;

public class Polygon {

    private String type;
    private List<Location> coords = new ArrayList<>();
    private Color color;

    public Color getColor() {
        return color;
    }

    public List<Location> getCoords() {
        return coords;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addCoords(Location loc) {
        this.coords.add(loc);
    }

    public void setColor(Color col) {
        this.color = col;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "type='" + type + '\'' +
                ", coords=" + coords +
                '}';
    }
}