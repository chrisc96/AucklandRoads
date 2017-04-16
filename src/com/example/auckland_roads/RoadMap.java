package com.example.auckland_roads;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class RoadMap extends GUI {

    static RoadMap map; // Object for this class to be accessed from others

    // Main data structures
    static List<Segment> segmentList = new ArrayList<>();
    private static List<Polygon> polygonList = new ArrayList<>();
    static Map<Integer, Node> nodeMap = new HashMap<>();
    static Map<Integer, Road> roadMap = new HashMap<>();
    private static List<Node> selectedNodes = new ArrayList<>();
    private static List<Restriction> restrictionList = new ArrayList<>();

    // Searching by search bar
    static final Trie trie = new Trie();
    private static List<String> searchRoadNames = new ArrayList<>();
    private List<String> smaller = new ArrayList<>();
    static String currString;

    // A* Path finding data structures/fields
    private Node closest = null; // For selected intersection/node
    private final List<Node> nodesTravelled = new ArrayList<>();
    private final List<Segment> segmentsTravelled = new ArrayList<>();

    // Articulation Points data structures/fields
    private ArticulationPts artPts = null;
    boolean artPtsToggle = false; // to toggle display on/off

    // Misc
    private final JTextArea output = getTextOutputArea();
    public Graphics g;
    private boolean first_run = true;

    // Initial scale/origin so that custom scale can be set after redraw is run for first time
    // as onLoad (which sets origin/scale) is run after redraw which requires origin and scale
    private Location origin = new Location(0,0);
    private Double scale = 50.0;
    private Double zoom = 0.0;

    // Used for zooming/panning with current mouse pos
    int mouseX, mouseY, mouseXStart, mouseYStart, mouseXEnd, mouseYEnd, xDif, yDif;


    @Override
    protected void redraw(Graphics g) {
        if (first_run) {
            instructions();
            first_run = false;
        }
        this.g = g;
        drawMap();
    }

    @Override
    protected void onClick(MouseEvent e) {
        Location mousePos = Location.newFromPoint(e.getPoint(), origin, scale);
        closest = null;
        for(Node n: nodeMap.values()) {
            if (n.clickable) {
                double minDist = n.OvalSize / scale;
                double dist = mousePos.distance(n.getLocation());
                if (dist < minDist) {
                    closest = n;
                    drawNodeInfo();
                    if (selectedNodes.size() < 2) {
                        selectedNodes.add(closest);
                    }
                    break;
                }
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
                    for (String road : smaller) {
                        output.append(GUI.toTitleCase(road) + "\n");
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
            outputTraversedInfo();

        }
    }

    private void ArticulationSearch() {

        // We need to do this because some of the islands off New Zealand are evidently disconnected
        // Thus we need to run our algorithm on these islands too. This checks whether all the nodes
        // Have been processed, if they haven't, run the search again with this node as the root
        int count = 0;
        while (count != nodeMap.size()) {
            count = 0;
            for (Node node : nodeMap.values()) {
                if (node.depth == Integer.MAX_VALUE) {
                    artPts = new ArticulationPts(node);
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
                this.origin = Location.newFromPoint(new Point((int) deltaMouseX, (int) deltaMouseY), origin, scale);

                redraw(g);
                break;
            case ZOOM_OUT:
                this.scale = scale/1.3;
                this.zoom -= 0.5;

                deltaMouseX = mouseX/1.3 - mouseX;
                deltaMouseY = mouseY/1.3 - mouseY;
                this.origin = Location.newFromPoint(new Point((int) deltaMouseX, (int) deltaMouseY), origin, scale);
                redraw(g);
                break;
            case ZOOM_IN_BTN:
                this.scale = scale*(1.3);
                this.zoom += 0.5;

                // Used for zooming using mouse wheel. Otherwise using the button zooms from center
                posCentreX = (getDrawingAreaDimension().getWidth()/2)*1.3 - (getDrawingAreaDimension().getWidth()/2);
                posCentreY = (getDrawingAreaDimension().getHeight()/2)*1.3 - (getDrawingAreaDimension().getHeight()/2);
                this.origin = Location.newFromPoint(new Point((int) posCentreX, (int) posCentreY), origin, scale);

                redraw(g);
                break;
            case ZOOM_OUT_BTN:
                this.scale = scale/1.3;
                this.zoom -= 0.5;

                posCentreX = (getDrawingAreaDimension().getWidth()/2)/1.3 - (getDrawingAreaDimension().getWidth()/2);
                posCentreY = (getDrawingAreaDimension().getHeight()/2)/1.3 - (getDrawingAreaDimension().getHeight()/2);
                this.origin = Location.newFromPoint(new Point((int) posCentreX, (int) posCentreY), origin, scale);
                redraw(g);
                break;
        }
    }


    // Drawing methods (segments, nodes)

    private void drawNodes(Location origin, double scale) {
        if (nodeMap != null) {
            for (Node node : nodeMap.values()) {
                Point pt = node.getLocation().asPoint(origin, scale);
                if (checkNodeClipping(pt)) {
                    g.setColor(node.col);
                    g.fillOval(pt.x - node.OvalSize / 2, pt.y - node.OvalSize / 2, node.OvalSize, node.OvalSize);
                }
            }
        }
        if (selectedNodes != null) {
            for (Node n : selectedNodes) {
                Point pt = n.getLocation().asPoint(origin, scale);
                if (checkNodeClipping(pt)) {
                    g.setColor(Color.white);
                    g.fillOval(pt.x - n.OvalSize/2 - 3, pt.y - n.OvalSize/2 - 3, n.OvalSize + 6, n.OvalSize + 6);
                    g.setColor(new Color(47,153,244));
                    g.fillOval(pt.x - n.OvalSize / 2, pt.y - n.OvalSize / 2, n.OvalSize, n.OvalSize);
                    drawNodeInfo();
                }
            }
        }
        if (nodesTravelled != null) {
            for (int i = 0; i < nodesTravelled.size(); i++) {
                Node n = nodesTravelled.get(i);

                Point pt = n.getLocation().asPoint(origin, scale);
                if (checkNodeClipping(pt)) {
                    g.setColor(Color.white);
                    g.fillOval(pt.x - n.OvalSize/2 - 3, pt.y - n.OvalSize/2 - 3, n.OvalSize + 6, n.OvalSize + 6);

                    if (i == 0) g.setColor(new Color(57,255, 73));
                    else if (i == nodesTravelled.size() - 1) g.setColor(new Color(255, 38, 3));
                    else g.setColor(new Color(47,153,244));
                    g.fillOval(pt.x - n.OvalSize / 2, pt.y - n.OvalSize / 2, n.OvalSize, n.OvalSize);
                }
            }
        }
        if (artPtsToggle) {
            if (artPts != null) {
                for (Node n : artPts.getArtPts()) {
                    Point pt = n.getLocation().asPoint(origin, scale);
                    if (checkNodeClipping(pt)) {
                        g.setColor(Color.GREEN);
                        g.fillOval(pt.x - n.OvalSize / 2, pt.y - n.OvalSize / 2, n.OvalSize, n.OvalSize);
                    }
                }
            }
        }
    }

    private void drawSegments(Location origin, double scale) {
        Graphics2D g2 = (Graphics2D) g;
        for (Segment aSegmentList : segmentList) {

            int len = aSegmentList.getCoords().size();
            Road rd = roadMap.get(aSegmentList.roadID);
            g.setColor(rd.getCol());
            g2.setStroke(rd.getBs());
            for (int j = 1; j < len; j++) {
                Point p1 = aSegmentList.getCoords().get(j - 1).asPoint(origin, scale);
                Point p2 = aSegmentList.getCoords().get(j).asPoint(origin, scale);
                if (checkSegmentClipping(p1, p2)) {
                    if (rd.getRoadClass() != 0) {
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                    else {
                        if (zoom >= 1) {
                            aSegmentList.nodeID1.clickable = true;
                            aSegmentList.nodeID2.clickable = true;
                            g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                        else {
                            aSegmentList.nodeID1.clickable = false;
                            aSegmentList.nodeID2.clickable = false;
                        }
                    }
                }
            }
        }
        g.setColor(Color.black);
        if (smaller != null) {
            for (String aSmaller : smaller) {
                List<Road> roads = trie.getRoads(aSmaller);   // Gets all of the roads that match the string
                // in smaller list as result of searching
                for (Road road : roads) {
                    List<Segment> segs = road.getSegments();
                    for (Segment seg : segs) {
                        List<Location> coords = seg.getCoords();
                        for (int l = 1; l < coords.size(); l++) {
                            Point p1 = seg.getCoords().get(l - 1).asPoint(origin, scale);
                            Point p2 = seg.getCoords().get(l).asPoint(origin, scale);
                            if (checkSegmentClipping(p1, p2)) {
                                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                            }
                        }
                    }
                }
            }
        }
        g.setColor(new Color(47,153,244));
        for (Segment aSegmentsTravelled : segmentsTravelled) {
            List<Location> coords = aSegmentsTravelled.getCoords();
            for (int j = 1; j < coords.size(); j++) {
                Point p1 = aSegmentsTravelled.getCoords().get(j - 1).asPoint(origin, scale);
                Point p2 = aSegmentsTravelled.getCoords().get(j).asPoint(origin, scale);
                if (checkSegmentClipping(p1, p2)) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }

    private void drawPolygons(Location origin, double scale) {
        if (polygonList != null) {
            int[] xCoords;
            int[] yCoords;
            for (Polygon aPolygonList : polygonList) {
                xCoords = new int[aPolygonList.getCoords().size()];
                yCoords = new int[aPolygonList.getCoords().size()];
                if (aPolygonList.getColor() != null) {
                    for (int j = 0; j < aPolygonList.getCoords().size(); j++) {
                        Point p1 = aPolygonList.getCoords().get(j).asPoint(origin, scale);
                        xCoords[j] = p1.x;
                        yCoords[j] = p1.y;
                    }
                    g.setColor(aPolygonList.getColor());
                    if (checkPolygonClipping(xCoords, yCoords, xCoords.length)) {
                        g.fillPolygon(xCoords, yCoords, xCoords.length);
                    }
                }
            }
        }
    }


    // Helper/Refactoring methods

    private void drawMap() {
        drawPolygons(origin, scale);
        drawSegments(origin, scale);
        drawNodes(origin, scale);
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

   private boolean checkNodeClipping(Point pt) {
        return (pt.getLocation().getX() >= 0 && pt.getLocation().getX() <= getDrawingAreaDimension().getWidth() &&
                pt.getLocation().getY() >= 0 && pt.getLocation().getY() <= getDrawingAreaDimension().getHeight());
   }

    private boolean checkSegmentClipping(Point p1, Point p2) {
        return (p1.getLocation().getX() >= 0 && p1.getLocation().getX() <= getDrawingAreaDimension().getWidth() &&
                p1.getLocation().getY() >= 0 && p1.getLocation().getY() <= getDrawingAreaDimension().getWidth() ||
                p2.getLocation().getX() >= 0 && p2.getLocation().getX() <= getDrawingAreaDimension().getWidth() &&
                p2.getLocation().getY() >= 0 && p2.getLocation().getY() <= getDrawingAreaDimension().getHeight()
                );
    }

    private boolean checkPolygonClipping(int[] xCoords, int[] yCoords, int length) {
        for (int i = 0; i < length; i++) {
            if (xCoords[i] >= 0 && yCoords[i] >= 0 && xCoords[i] <= getDrawingAreaDimension().width && yCoords[i] <= getDrawingAreaDimension().getHeight()) {
                return true;
            }
        }
        return false;
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
            String roadName = "";
            Road r = roadMap.get(s.getRoadID());

            // Roundabouts have (-,-) output, this deals with that
            if (r.getName().equals("-")) {
                roadName = "Roundabout\t";
            }
            else if (r.getCity().equals("-")) {
                roadName = roadName + toTitleCase(r.getName() + "\t");
            }
            else {
                roadName = toTitleCase(r.getName() + ", " + r.getCity());
            }

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


        DecimalFormat df = new DecimalFormat("#0.000");
        output.append("A* Path Breakdown: \n\n");
        output.append("Start:\t");

        boolean start = true;
        for (String info : roadNameList) {
            if (!start) output.append("Node:\t");
            start = false;

            String str = info + "\t" + df.format(roadNameToLength.get(info)) + " km \n";
            output.append(str);
        }

        output.append("\nTotal Distance: \t\t\t" + df.format(totalDistance) + " km");

        selectedNodes.clear();
    }

    private void drawNodeInfo() {
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

    private void instructions() {
        output.setText(
                "Welcome to my Auckland Maps program designed by Chris Connolly,\n\n" +
                        "CONTROLS:\n" +
                        "      Zoom using the mouse wheel (up/down)\n" +
                        "      Pan using click and drag of map\n" +
                        "      Pan using arrow keys (up, down, left, right\n" +
                        "      Search for roads using the search bar\n" +
                        "      Select nodes (small circles) for info\n" +
                        "      Select two nodes and then click the A* Search Button\n" +
                        "      Click the Articulation Points button to show/hide them\n"
        );
    }

    // Methods to execute on load

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
        DataLoader dl = new DataLoader();
        nodeMap = dl.parseNodes(nodes);
        roadMap = dl.parseRoads(roads);
        segmentList = dl.parseSegments(segments); // Must add segments last

        if (polygons != null) {
            polygonList = dl.parsePoly(polygons);
        }

        if (restrictions != null) {
            restrictionList = dl.parseRestrictions(restrictions);
        }

        ArticulationSearch(); // Calculates Articulation Points on load, button toggles their display
        startMethod();
    }

    private void startMethod() {
        Dimension d = this.getDrawingAreaDimension();
        double windowSize = Math.min(d.getHeight(), d.getWidth());

        // Sets scale based on depth of zoom
        setExtremities();
        // scale = (Math.min(windowSize/(farTop-farBot), windowSize/(farRight-farLeft)));
        origin = Location.newFromLatLon(Location.CENTRE_LAT, Location.CENTRE_LON);  // farLeft, farTop
    }

    private void setExtremities() {
        Double farLeft = Double.POSITIVE_INFINITY;
        Double farRight = Double.NEGATIVE_INFINITY;
        Double farBot = Double.POSITIVE_INFINITY;
        Double farTop = Double.NEGATIVE_INFINITY;

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