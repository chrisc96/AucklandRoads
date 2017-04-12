package com.example.auckland_roads;

import java.util.*;

public class Trie {

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void add(String word,  Road road) {
        word = word.toLowerCase();
        TrieNode cursor = root;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);

            if (cursor.getChildren().get(character) == null) {
                cursor.getChildren().put(character, new TrieNode(Character.toString(character)));
                cursor = cursor.getChildren().get(character);
            }
            else {
                cursor = cursor.getChildren().get(character);
            }
        }
        // The cursor after running the for loop is at the end of the word,
        cursor.setCompletesAWord();
        cursor.addRoad(road);
    }

    public boolean contains(String word) {
        TrieNode cursor = root;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (cursor.getChildren().get(character) == null) return false;
            cursor = cursor.getChildren().get(character);
        }
        return cursor.isCompletesAWord();
    }

    List<Road> getRoads(String word) {
        TrieNode cursor = root;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (cursor.getChildren().get(character) == null) return null;
            cursor = cursor.getChildren().get(character);
        }
        return cursor.getRoads();
    }

    public List<String> getAll(String prefix) {
        if (prefix.length() == 0) return Collections.emptyList();
        TrieNode cursor = root;
        char lastChar = prefix.charAt(prefix.length()-1);
        List<String> roadNames = new ArrayList<>();

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (cursor.getChildren().get(c) != null) {
                cursor = cursor.getChildren().get(c);
                if (i == prefix.length()-1 && c == lastChar) {
                    roadNames = DFSHelper(cursor, prefix, roadNames);
                    return roadNames;
                }
            }
        }
        return null;
    }

    private List<String> DFSHelper(TrieNode cursor, String prefix, List<String> roadNames) {
        if (cursor.isCompletesAWord()) {
            roadNames.add(prefix);
        }
        for (int i = 0; i < cursor.getChildren().size(); i++) {
            char c = new ArrayList<>(cursor.getChildren().keySet()).get(i);
            DFSHelper(cursor.getChildren().get(c), prefix + cursor.getChildren().get(c).getValue(), roadNames);
        }
        return roadNames;
    }
}