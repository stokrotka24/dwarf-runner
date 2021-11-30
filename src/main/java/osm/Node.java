package osm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Node
 */
public class Node {

    private Long id;
    private final List<Coordinates> neighbors;
    private final Coordinates coords;

    public Node(Node node) {
        this.id = node.getId();
        this.coords = new Coordinates(node.getCoords());
        this.neighbors = new ArrayList<>(node.getNeighbors());
    }

    public void addNeighbor(Coordinates coords) {
        neighbors.add(coords);
    }

    public Double getY() {
        return coords.getY();
    }

    public Double getX() {
        return coords.getX();
    }

    public Coordinates getCoords() {
        return coords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Coordinates> getNeighbors() {
        return neighbors;
    }

    public Node(Long id, Double x, Double y) {
        this.coords = new Coordinates(x, y);
        this.id = id;
        this.neighbors = new ArrayList<>();
    }

    public int nextNeighbor(Coordinates position) {
        List<Double> distances = distLinePoints(coords, position, neighbors);
        return distances.indexOf(Collections.min(distances));
    }

    private List<Double> distLinePoints(Coordinates A, Coordinates B, List<Coordinates> points) {
        List<Double> distances = new ArrayList<>();
        double distAB = A.distanceTo(B);
        double x1 = A.getX();
        double y1 = A.getY();
        double x2 = B.getX();
        double y2 = B.getY();
        for (Coordinates C : points) {
            double x0 = C.getX();
            double y0 = C.getY();
            double numerator = Math.abs((x2 - x1) * (y1 - y0) - (x1 - x0) * (y2 - y1));
            distances.add(numerator/distAB);
        }
        return distances;
    }

}