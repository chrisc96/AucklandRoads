package com.example.auckland_roads;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class RoadMap extends GUI {

    private Double farLeft, farRight, farTop, farBot; // Used to set map boundaries

    public static RoadMap map; // Object for this class to be accessed from others

    // Main data structures
    public static List<Segment> segmentList = new ArrayList<>();
    public static List<Polygon> polygonList = new ArrayList<>();
    public static Map<Integer, Node> nodeMap = new HashMap<>();
    public static Map<Integer, Road> roadMap = new HashMap<>();
    public static List<Node> selectedNodes = new ArrayList<>();

    // Searching by search bar
    public static Trie trie = new Trie();
    public static List<String> searchRoadNames = new ArrayList<>();
    public static String currString;
    List<String> smaller = new ArrayList<>();

    // A* Path finding data structures/fields
    Node closest = null; // For selected intersection/node
    List<Node> nodesTravelled = new ArrayList<>();
    List<Segment> segmentsTravelled = new ArrayList<>();


    // Articulation Points data structures/fields
    ArticulationPts artPts = null;
    public boolean artPtsToggle = false; // to toggle display on/off

    // Misc
    JTextArea output = getTextOutputArea();
    Graphics g;
    boolean first_run = true;

    // Initial scale/origin so that custom scale can be set after redraw is run for first time
    // as onLoad (which sets origin/scale) is run after redraw which requires origin and scale
    public Location origin = new Location(0,0);
    public Double scale = 50.0;
    public Double zoom = 0.0;

    // Used for zooming/panning with current mouse pos
    int mouseX, mouseY, mouseXStart, mouseYStart, mouseXEnd, mouseYEnd, xDif, yDif;

    @Override
    protected void redraw(Graphics g) {
        if (first_run) {
            instructions();
            this.g = g;
            first_run = false;
        }
        drawMap(g);
    }

    @Override
    protected void onClick(MouseEvent e) {
        Location mousePos = Location.newFromPoint(e.getPoint(), origin, scale);
        closest = null;
        for(Node n: nodeMap.values()) {
            double minDist = n.OvalSize/scale;
            double dist = mousePos.distance(n.getLocation());
            if(dist < minDist) {
                closest = n;
                drawNodeInfo();
                if (selectedNodes.size() < 2) {
                    selectedNodes.add(closest);
                }
                break;
            }
        }
        // Allows you to click outside of the node selected, provided it's not another node
        // and it will deselect the node
        if (closest == null) {
            clearSearches();
        }
    }

    @Override
    protected void onSearch() {
        clearSearches();

        currString = GUI.search.getText().toLowerCase();
        if (currString.length() != 0) {
            searchRoadNames = trie.getAll(currString);
            if (searchRoadNames != null) {
                if (searchRoadNames.size() != 0) {
                    if (searchRoadNames.size() > 10) {
                        smaller = searchRoadNames.subList(0, 10);
                    } else {
                        smaller = searchRoadNames;
                    }
                    for (int i = 0; i < smaller.size(); i++) {
                        output.append(GUI.toTitleCase(smaller.get(i)) + "\n");
                    }
                }
            }
            else {
                output.setText("");
            }
        }
        else {
            smaller.clear();
        }
        redraw(g);
    }

    @Override
    protected void AStarSearch() {
        if (selectedNodes.size() == 2) {
            AStarSearch route = new AStarSearch(selectedNodes.get(0), selectedNodes.get(1));
            Node endNode = route.search();

            nodesTravelled.add(endNode);

            while (endNode.pathFrom != null) {
                for (Segment seg : segmentList) {
                    if (seg.nodeID1.getNodeID() == endNode.pathFrom.getNodeID() && seg.nodeID2.getNodeID() == endNode.getNodeID() ||
                        seg.nodeID1.getNodeID() == endNode.getNodeID() && seg.nodeID2.getNodeID() == endNode.pathFrom.getNodeID()) {
                        nodesTravelled.add(endNode.pathFrom);
                        segmentsTravelled.add(seg);
                    }
                }
                endNode = endNode.pathFrom;
            }
            resetAStarFieldsNodes();
            selectedNodes.clear();
            outputTraversedInfo();
        }
    }

    @Override
    protected void ArticulationSearch() {

        // We need to do this because some of the islands off New Zealand are evidently disconnected
        // Thus we need to run our algorithm on these islands too. This checks whether all the nodes
        // Have been processed, if they haven't, run the search again with this node as the root
        int count = 0;
        while (count != nodeMap.size()) {
            count = 0;
            for (Node node : nodeMap.values()) {
                if (node.depth == Integer.MAX_VALUE) {
                    artPts = new ArticulationPts(node, 0, null);
                    artPts.findArtPts();
                    break;
                }
                else {
                    count++;
                }
            }
        }
    }

    // Switches for GUI operability inc. buttons, mouse panning and zooming.

    @Override
    protected void onMove(Move m) {
        double deltaMouseX, deltaMouseY, posCentreX, posCentreY;
        switch (m) {
            case MOUSE_DRAG:
                origin = origin.moveBy(xDif/(scale*2), yDif/(scale*2));
                redraw(g);
                break;
            case NORTH:
                origin = origin.moveBy(0,2);
                redraw(g);
                break;
            case SOUTH:
                origin = origin.moveBy(0,-2);
                redraw(g);
                break;
            case EAST:
                origin = origin.moveBy(2,0);
                redraw(g);
                break;
            case WEST:
                origin = origin.moveBy(-2,0);
                redraw(g);
                break;
            case ZOOM_IN:
                this.scale = this.scale * 1.3;
                this.zoom += 0.5;

                // Zooms from center
                deltaMouseX = mouseX*1.3 - mouseX;
                deltaMouseY = mouseY*1.3 - mouseY;
                Location mouseZoomIn = Location.newFromPoint(new Point((int) deltaMouseX, (int) deltaMouseY), origin, scale);
                this.origin = mouseZoomIn;

                redraw(g);
                break;
            case ZOOM_OUT:
                this.scale = scale/1.3;
                this.zoom -= 0.5;

                deltaMouseX = mouseX/1.3 - mouseX;
                deltaMouseY = mouseY/1.3 - mouseY;
                Location mouseZoomOut = Location.newFromPoint(new Point((int) deltaMouseX, (int) deltaMouseY), origin, scale);
                this.origin = mouseZoomOut;
                redraw(g);
                break;
            case ZOOM_IN_BTN:
                this.scale = scale*(1.3);
                this.zoom += 0.5;

                // Used for zooming using mouse wheel. Otherwise using the button zooms from center
                posCentreX = (getDrawingAreaDimension().getWidth()/2)*1.3 - (getDrawingAreaDimension().getWidth()/2);
                posCentreY = (getDrawingAreaDimension().getHeight()/2)*1.3 - (getDrawingAreaDimension().getHeight()/2);
                Location btnZoomIn = Location.newFromPoint(new Point((int) posCentreX, (int) posCentreY), origin, scale);
                this.origin = btnZoomIn;

                redraw(g);
                break;
            case ZOOM_OUT_BTN:
                this.scale = scale/1.3;
                this.zoom -= 0.5;

                posCentreX = (getDrawingAreaDimension().getWidth()/2)/1.3 - (getDrawingAreaDimension().getWidth()/2);
                posCentreY = (getDrawingAreaDimension().getHeight()/2)/1.3 - (getDrawingAreaDimension().getHeight()/2);
                Location btnZoomOut = Location.newFromPoint(new Point((int) posCentreX, (int) posCentreY), origin, scale);
                this.origin = btnZoomOut;
                redraw(g);
                break;
        }
    }


    // Drawing methods (segments, nodes)

    public void drawNodes(Graphics g, Location origin, double scale) {
        for (Node node : nodeMap.values()) {
            Point pt = node.getLocation().asPoint(origin,scale);
            g.setColor(node.col);
            g.fillOval(pt.x-node.OvalSize/2, pt.y-node.OvalSize/2,node.OvalSize,node.OvalSize);
        }

        for (Node n: selectedNodes){
            Point pt = n.getLocation().asPoint(origin, scale);
            g.setColor(Color.YELLOW);
            g.fillOval(pt.x - n.OvalSize/2, pt.y - n.OvalSize/2 ,n.OvalSize,n.OvalSize);
            drawNodeInfo();
        }
        for (Node n: nodesTravelled){
            Point pt = n.getLocation().asPoint(origin, scale);
            g.setColor(Color.RED);
            g.fillOval(pt.x - n.OvalSize/2, pt.y - n.OvalSize/2 ,n.OvalSize,n.OvalSize);
        }
        if (artPtsToggle) {
            for (Node n : artPts.getArtPts()) {
                Point pt = n.getLocation().asPoint(origin, scale);
                g.setColor(Color.GREEN);
                g.fillOval(pt.x - n.OvalSize / 2, pt.y - n.OvalSize / 2, n.OvalSize, n.OvalSize);
            }
        }
    }

    public void drawSegments(Graphics g, Location origin, double scale) {
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < segmentList.size(); i++) {
            int len = segmentList.get(i).getCoords().size();
            Road rd = roadMap.get(segmentList.get(i).roadID);
            g.setColor(rd.getCol());
            g2.setStroke(rd.getBs());
            for (int j = 1; j < len; j++) {
                Point p1 = segmentList.get(i).getCoords().get(j - 1).asPoint(origin, scale);
                Point p2 = segmentList.get(i).getCoords().get(j).asPoint(origin, scale);
                if (rd.getRoadClass() != 0) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
                else {
                    if (zoom >= 1) {
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        }
        g.setColor(Color.black);
        if (smaller != null) {
            for (int i = 0; i < smaller.size(); i++) {
                List<Road> roads = trie.getRoads(smaller.get(i));   // Gets all of the roads that match the string
                                                                    // in smaller list as result of searching
                for (int j = 0; j < roads.size(); j++) {
                    List<Segment> segs = roads.get(j).getSegments();
                    for (int k = 0; k < segs.size(); k++) {
                        List<Location> coords = segs.get(k).getCoords();
                        for (int l = 1; l < coords.size(); l++) {
                            Point p1 = segs.get(k).getCoords().get(l-1).asPoint(origin,scale);
                            Point p2 = segs.get(k).getCoords().get(l).asPoint(origin,scale);
                            g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }
            }
        }
        g.setColor(Color.RED);
        for (int i = 0; i < segmentsTravelled.size(); i++) {
            List<Location> coords = segmentsTravelled.get(i).getCoords();
            for (int j = 1; j < coords.size(); j++) {
                Point p1 = segmentsTravelled.get(i).getCoords().get(j-1).asPoint(origin,scale);
                Point p2 = segmentsTravelled.get(i).getCoords().get(j).asPoint(origin,scale);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    public void drawPolygons(Graphics g, Location origin, double scale) {
        if (polygonList != null) {
            int[] xCoords;
            int[] yCoords;
            for (int i = 0; i < polygonList.size(); i++) {
                xCoords = new int[polygonList.get(i).getCoords().size()];
                yCoords = new int[polygonList.get(i).getCoords().size()];
                if (polygonList.get(i).getColor() != null) {
                    for (int j = 0; j < polygonList.get(i).getCoords().size(); j++) {
                        Point p1 = polygonList.get(i).getCoords().get(j).asPoint(origin, scale);
                        xCoords[j] = p1.x;
                        yCoords[j] = p1.y;
                    }
                    g.setColor(polygonList.get(i).getColor());
                    g.fillPolygon(xCoords, yCoords, xCoords.length);
                }
            }
        }
    }


    // Helper/Refactoring methods

    private void drawMap(Graphics g) {
        drawPolygons(g, origin, scale);
        drawNodes(g, origin, scale);
        drawSegments(g, origin, scale);
    }

    private void resetAStarFieldsNodes() {
        for (Node n : nodesTravelled) {
            n.reset();
        }
    }

    private void clearSearches() {
        output.setText("");
        segmentsTravelled.clear();
        nodesTravelled.clear();
        selectedNodes.clear();
    }

    // Displaying information/text

    private void outputTraversedInfo() {
        Collections.reverse(nodesTravelled);
        Collections.reverse(segmentsTravelled);

        HashMap<String, Double> roadNameToLength = new HashMap<>(); // Won't work if going through two streets with same name
        List<String> roadNameList = new ArrayList<>();
        double totalDistance = 0;
        output.setText("");

        for (Segment s : segmentsTravelled) {
            Road r = roadMap.get(s.getRoadID());
            String roadName = toTitleCase(r.getName() + ", " + r.getCity());

            if (!roadNameToLength.containsKey(roadName)) {
                roadNameList.add(roadName);
                roadNameToLength.put(roadName, s.getLength()); // Length is in km's
            }
            else {
                // Overwrites key with old value of length + new segment length
                roadNameToLength.put(roadName, roadNameToLength.get(roadName) + s.getLength());
            }
            totalDistance += s.getLength();
        }

        output.append("A* Path Breakdown: \n\n");
        output.append("Start:\t");
        boolean start = true;
        for (String info : roadNameList) {
            if (!start) output.append("Node:\t");
            start = false;
            String str = info + "\t" + (double) Math.round(roadNameToLength.get(info)*1000)/1000 + " km \n";
            output.append(str);
        }

        output.append("\nGoal reached. Total Distance: \t\t" + (double) Math.round(totalDistance*1000) / 1000 + "km");
    }

    public void drawNodeInfo() {
        output.setText( "Intersection ID: " + closest.getNodeID() + "\n");

        List<Segment> segsIn = closest.getSegmentIn();
        List<Segment> segsOut = closest.getSegmentOut();
        segsIn.addAll(segsOut); // Combine lists together

        for (int i = 0 ; i < segsIn.size(); i++) {
            Road rdI = roadMap.get(segsIn.get(i).getRoadID());
            for (int j = i+1; j < segsIn.size(); j++) {
                Road rdJ = roadMap.get(segsIn.get(j).getRoadID());
                // If the names are the same, remove the second instance of it
                if (rdI.getName().equals(rdJ.getName())) {
                    segsIn.remove(j);
                    j--;
                }
            }
        }

        for (Segment seg : segsIn) {
            Road rd = roadMap.get(seg.getRoadID());
            output.append("Road Name: " + toTitleCase(rd.getName()) + ", " + toTitleCase(rd.getCity()) + "\n");
        }
    }

    public void instructions() {
        output.setText(
                "Welcome to my Auckland Maps program designed by Chris Connolly,\n\n" +
                        "CONTROLS:\n" +
                        "      Zoom using the mouse wheel (up/down)\n" +
                        "      Pan using click and drag of map\n" +
                        "      Pan using arrow keys (up, down, left, right\n" +
                        "      Search for roads using the search bar\n" +
                        "      Select nodes (small circles) for info\n"
        );
    }

    // Methods to execute on load

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        DataLoader dl = new DataLoader();
        nodeMap = dl.parseNodes(nodes);
        roadMap = dl.parseRoads(roads);
        segmentList = dl.parseSegments(segments); // Must add segments last

        if (polygons != null) {
            polygonList = dl.parsePoly(polygons);
        }

        ArticulationSearch(); // Calculates Articulation Points on load, button toggles their display
        startMethod();
    }

    public void startMethod() {
        Dimension d = this.getDrawingAreaDimension();
        double windowSize = Math.min(d.getHeight(), d.getWidth());

        // Sets scale based on depth of zoom
        setExtremities();
        // scale = (Math.min(windowSize/(farTop-farBot), windowSize/(farRight-farLeft)));
        origin = Location.newFromLatLon(Location.CENTRE_LAT, Location.CENTRE_LON);  // farLeft, farTop
    }

    public void setExtremities() {
        farLeft = Double.POSITIVE_INFINITY;
        farRight = Double.NEGATIVE_INFINITY;
        farBot = Double.POSITIVE_INFINITY;
        farTop = Double.NEGATIVE_INFINITY;

        for (Node node : nodeMap.values()) {
            if (node.getLocation().x < farLeft) farLeft = node.getLocation().x;
            if (node.getLocation().x > farRight) farRight = node.getLocation().x;
            if (node.getLocation().y > farTop) farTop = node.getLocation().y;
            if (node.getLocation().y < farBot) farBot = node.getLocation().y;
        }
    }

    public static void main(String[] args) {
        map = new RoadMap();
    }
}