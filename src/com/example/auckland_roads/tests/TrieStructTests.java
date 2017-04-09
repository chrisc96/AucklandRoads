package com.example.auckland_roads.tests;

import com.example.auckland_roads.Trie;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TrieStructTests{

    @Test
    public void addToTrieWord() {
        Trie trie = new Trie();
        trie.add("queen", null);
        assertTrue(trie.contains("queen"));
    }

    @Test
    public void addToTrieCapitals() {
        Trie trie = new Trie();
        trie.add("JervoiS", null);
        assertFalse(trie.contains("Jerv"));
    }

    @Test
    public void addToTrieSpaces() {
        Trie trie = new Trie();
        trie.add("hinemoa street", null);
        assertTrue(trie.contains("hinemoa street"));
    }

    @Test
    public void getAllStartWithPrefix() {
        Trie trie = new Trie();

        trie.add("hi",null);
        trie.add("cheese",null);
        trie.add("dog",null);
        trie.add("bacon",null);
        trie.add("ball",null);
        trie.add("hello",null);
        trie.add("help",null);
        trie.add("helping",null);

        trie.getAll("he");
    }

    public static void main(String[] args) {
        new TrieStructTests();
    }
}
