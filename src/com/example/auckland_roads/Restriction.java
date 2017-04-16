package com.example.auckland_roads;

import java.util.Map;

public class Restriction {

    Node nodeFrom;
    Road roadFrom;
    Node rstrNode;
    Road roadTo;
    Node nodeTo;

    public Restriction(String line, Map<Integer, Node> nodeMap, Map<Integer, Road> roadMap) {
        String[] values = line.split("\t");
        this.nodeFrom = nodeMap.get(Integer.parseInt(values[0]));
        this.roadFrom = roadMap.get(Integer.parseInt(values[1]));
        this.rstrNode = nodeMap.get(Integer.parseInt(values[2]));
        this.roadTo = roadMap.get(Integer.parseInt(values[3]));
        this.nodeTo = nodeMap.get(Integer.parseInt(values[4]));
    }
}