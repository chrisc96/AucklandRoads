package com.example.auckland_roads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode {

    private Map<Character, TrieNode> children;
    private String value; // Character data value this node represents
    private List<Road> roads = new ArrayList<>();

    private boolean completeAWord;
    private boolean visited = false;

    // Setting up root
    public TrieNode() {
        children = new HashMap<>();
        completeAWord = false;
    }


    public TrieNode(String character) {
        this.value = character;
        children = new HashMap<>();
    }


    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setCompleteAWord(boolean completeAWord) {
        this.completeAWord = completeAWord;
    }

    public boolean isCompleteAWord() {
        return completeAWord;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isCompletesAWord() {
        return completeAWord;
    }

    public void setCompletesAWord(boolean completeAWord) {
        this.completeAWord = completeAWord;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isACompleteWord() {
        return completeAWord;
    }

    public void addRoad(Road road) {
        this.roads.add(road);
    }

    public List<Road> getRoads() {
        return roads;
    }
}
