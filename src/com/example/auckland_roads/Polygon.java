package com.example.auckland_roads;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Polygon {

    private String type;
    private final List<Location> coords = new ArrayList<>();
    private Color color;

    Color getColor() {
        return color;
    }

    List<Location> getCoords() {
        return coords;
    }

    void setType(String type) {
        this.type = type;
    }

    void addCoords(Location loc) {
        this.coords.add(loc);
    }

    void setColor(Color col) {
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