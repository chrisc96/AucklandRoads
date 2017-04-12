package com.example.auckland_roads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {

    private final Map<Character, TrieNode> children;
    private String value; // Character data value this node represents
    private final List<Road> roads = new ArrayList<>();

    private boolean completeAWord;
    private boolean visited = false;

    // Setting up root
    TrieNode() {
        children = new HashMap<>();
        completeAWord = false;
    }

    TrieNode(String character) {
        this.value = character;
        children = new HashMap<>();
    }

    // Getters and Setters
    String getValue() {
        return value;
    }

    boolean isCompletesAWord() {
        return completeAWord;
    }

    void setCompletesAWord() {
        this.completeAWord = true;
    }

    Map<Character, TrieNode> getChildren() {
        return children;
    }

    void addRoad(Road road) {
        this.roads.add(road);
    }

    List<Road> getRoads() {
        return roads;
    }
}