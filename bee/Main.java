package bee;

import java.util.*;
import java.io.*;

public class Main {
    private static String file;
    public static Node[][][] cube;
    private static ArrayList<Node> bee = new ArrayList<Node>();
    private static ArrayList<Node> hive = new ArrayList<Node>();
    public static void main(String[] args) {
        for (int f = 1; f <= 3; f++) {
            System.out.println("File " + f);
            file = "beesetup" + f + ".txt";
            setup();
            runAStar(f);
            runDjikstra(f);
            System.out.println();
        }
    }
    public static void runAStar(int file) {
        int total = 0;
        for (int b = 0; b < bee.size(); b++) {
            double beeTotal = Math.pow(cube.length,3);
            ArrayList<Node> beePath = new ArrayList<Node>();
            for (int h = 0; h < hive.size(); h++) {
                ArrayList<Node> path = aStar(bee.get(b),hive.get(h));
                int hiveTotal = path.size()-1;
                if (hiveTotal < beeTotal) {
                    beeTotal = hiveTotal;
                    beePath = path;
                }
                refresh();
            }
            makeFile(file, "A Star", (b+1), beePath);
            total += beeTotal;
        }
        System.out.println("  A Star: " + total);
    }
    public static void runDjikstra(int file) {
        int total = 0;
        for (int b = 0; b < bee.size(); b++) {
            double beeTotal = Math.pow(cube.length,3);
            ArrayList<Node> beePath = new ArrayList<Node>();
            for (int h = 0; h < hive.size(); h++) {
                ArrayList<Node> path = dijkstra(bee.get(b),hive.get(h));
                int hiveTotal = path.size()-1;
                if (hiveTotal < beeTotal) {
                    beeTotal = hiveTotal;
                    beePath = path;
                }
                refresh();
            }
            makeFile(file, "Djikstra", (b+1), beePath);
            total += beeTotal;
        }
        System.out.println("  Djikstra: " + total);
    }
    public static void makeFile(int f, String algorithm, int bee, ArrayList<Node> path) {
        String fileName = "paths/File " + f + "/" + algorithm + "/Bee " + bee + ".txt";
        File file = new File(fileName);
        try {
            if (file.exists() == false) {
                file.createNewFile();
            }
            try {
                FileWriter fileWriter = new FileWriter(fileName, false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("Total: " + (path.size()-1));
                bufferedWriter.newLine();
                bufferedWriter.write("Start: (" + path.get(0).getCoordinates()[0] + "," + path.get(0).getCoordinates()[1] + "," + path.get(0).getCoordinates()[2] + ")");
                bufferedWriter.newLine();
                bufferedWriter.write("End: (" + path.get(path.size()-1).getCoordinates()[0] + "," + path.get(path.size()-1).getCoordinates()[1] + "," + path.get(path.size()-1).getCoordinates()[2] + ")");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("Path: ");
                bufferedWriter.newLine();
                for (Node n : path) {
                    bufferedWriter.write("  (" + n.getCoordinates()[0] + "," + n.getCoordinates()[1] + "," + n.getCoordinates()[2] + ")");
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void setup() {
        Scanner s;
        try {
            s = new Scanner(new File(file));
            int fileNumber = s.nextInt();
            int size = Integer.valueOf(s.next().split(",")[0]);
            cube = new Node[size][size][size];
            for (int a = 0; a < cube.length; a++) {
                for (int b = 0; b < cube[a].length; b++) {
                    for (int c = 0; c < cube[a][b].length; c++) {
                        cube[a][b][c] = new Node(a,b,c,"empty");
                    }
                }
            }
            bee.clear();
            hive.clear();
            for (int x = 0; x < 15; x++) {
                int[] hivePoint = Arrays.stream(s.next().split(",")).mapToInt(Integer::parseInt).toArray();
                cube[hivePoint[0]][hivePoint[1]][hivePoint[2]].setType("hive");
                hive.add(cube[hivePoint[0]][hivePoint[1]][hivePoint[2]]);
            }

            for (int x = 0; x < 15; x++) {
                int[] beePoint = Arrays.stream(s.next().split(",")).mapToInt(Integer::parseInt).toArray();
                cube[beePoint[0]][beePoint[1]][beePoint[2]].setType("bee");
                bee.add(cube[beePoint[0]][beePoint[1]][beePoint[2]]);
            }

            int obstacleCount = s.nextInt();
            for (int x = 0; x < obstacleCount; x++) {
                int[] obstaclePoint = Arrays.stream(s.next().split(",")).mapToInt(Integer::parseInt).toArray();
                cube[obstaclePoint[0]][obstaclePoint[1]][obstaclePoint[2]].setType("obstacle");
            }
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
    public static ArrayList<Node> dijkstra(Node start, Node end) {
        ArrayList<Node> originalGeneration = new ArrayList<>();
        int cycle = 0;
        start.setCycle(cycle);
        originalGeneration.add(start);
        while (true) {
            ArrayList<Node> newGeneration = new ArrayList<>();
            cycle++;
            for (Node originalPoint : originalGeneration) {
                for (Node newPoint : originalPoint.getNeighbors()) {
                    if (newPoint.getParent() == null) {
                        newPoint.setCycle(cycle);
                        newPoint.setParent(originalPoint);
                        newGeneration.add(newPoint);
                        if (Arrays.equals(newPoint.getCoordinates(),end.getCoordinates())) {
                            Node endPosition = newPoint;
                            ArrayList<Node> path = new ArrayList<>();
                            path.add(endPosition);
                            while (endPosition.getParent() != start) {
                                endPosition = endPosition.getParent();
                                path.add(endPosition);
                            }
                            path.add(start);
                            Collections.reverse(path);
                            return path;
                        }
                    }
                }
            }
            originalGeneration = newGeneration;
        }
    }
    public static ArrayList<Node> aStar(Node start, Node end) {
        List<Node> open = new ArrayList<Node>();
        Set<Node> closed = new HashSet<Node>();
        start.setG(0);
        start.setH(Node.distance(start, end));
        open.add(start);
        while (open.size() > 0) {
            Collections.sort(open);
            Node current = open.get(0);
            if (Arrays.equals(current.getCoordinates(),end.getCoordinates())) {
                ArrayList<Node> path = new ArrayList<>();
                path.add(current);
                while (current.getParent() != start) {
                    current = current.getParent();
                    path.add(current);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }
            open.remove(0);
            closed.add(current);
            for (Node neighbor : current.getNeighbors()) {
                if (!closed.contains(neighbor)) {
                    if (((current.getG() + 1) < neighbor.getG()) || !open.contains(neighbor)) {
                        neighbor.setG(current.getG()+1);
                        neighbor.setH(Node.distance(neighbor, end));
                        neighbor.setParent(current);
                        if (!open.contains(neighbor)) {
                            open.add(neighbor);
                        }
                    }
                }
            }
        }
        return new ArrayList<Node>();
    }
    public static void refresh() {
        for (int a = 0; a < cube.length; a++) {
            for (int b = 0; b < cube[a].length; b++) {
                for (int c = 0; c < cube[a][b].length; c++) {
                    cube[a][b][c].setG(0);
                    cube[a][b][c].setH(0);
                    cube[a][b][c].setParent(null);
                    cube[a][b][c].setCycle(0);
                }
            }
        }
        for (int x = 0; x < bee.size(); x++) {
            bee.get(x).setG(0);
            bee.get(x).setH(0);
            bee.get(x).setParent(null);
            bee.get(x).setCycle(0);
        }
        for (int x = 0; x < hive.size(); x++) {
            hive.get(x).setG(0);
            hive.get(x).setH(0);
            hive.get(x).setParent(null);
            hive.get(x).setCycle(0);
        }
    }
}