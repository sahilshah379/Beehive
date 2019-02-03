package bee;

import java.util.*;

public class Node implements Comparable<Node> {
    private int x,y,z;
    private String type;
    private double g,h = 0;
    private Node parent = null;
    private int cycle;
    public Node(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Node(int[] c) {
        this.x = c[0];
        this.y = c[1];
        this.z = c[2];
    }
    public Node(int x, int y, int z, String type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }
    public int getCycle() {
        return cycle;
    }
    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
    public int[] getCoordinates() {
        return new int[] {x, y, z};
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public double getG() {
        return g;
    }
    public double getH() {
        return h;
    }
    public double getF() {
        return g+h;
    }
    public void setG(double g) {
        this.g = g;
    }
    public void setH(double h) {
        this.h = h;
    }
    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public static double EuclideanDistance(Node a, Node b) {
        double distance = Math.sqrt((double)(Math.pow(a.getCoordinates()[0]-b.getCoordinates()[0],2)+Math.pow(a.getCoordinates()[1]-b.getCoordinates()[1],2)+Math.pow(a.getCoordinates()[2]-b.getCoordinates()[2],2)));
        return distance;
    }
    public static double ManhattanDistance(Node a, Node b) {
        double distance = Math.abs(a.getCoordinates()[0]-b.getCoordinates()[0]) + Math.abs(a.getCoordinates()[1]-b.getCoordinates()[1])+Math.abs(a.getCoordinates()[2]-b.getCoordinates()[2]);
        return distance;
    }
    public static double distance(Node a, Node b) {
        double distance = EuclideanDistance(a,b);
        return distance;
    }
    public ArrayList<Node> getNeighbors() {
        ArrayList<Node> neighbors = new ArrayList<>();
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                for (int c = -1; c <= 1; c++) {
                    try {
                        if (!Main.cube[x+a][y+b][z+c].getType().equals("obstacle")) {
                            if (!(a == 0 && b == 0 && c == 0)) {
                                Node n = Main.cube[x+a][y+b][z+c];
                                neighbors.add(n);
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {}
                }
            }
        }
        return neighbors;
    }
    public String toString() {
        return x + " " + y + " " + z + "\n";
    }
    public int compareTo(Node node) {
        return this.getF() > node.getF() ? 1 : this.getF() < node.getF() ? -1 : 0;
    }
 }