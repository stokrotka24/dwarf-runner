package osm;

import java.util.ArrayList;
import java.util.List;

/**
 * Node
 */
public class Node {

    private Long id;
    private final List<Coordinates> neighbors;
    private final Coordinates coords;

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

    public Node(Long id, Double y, Double x) {
        this.coords = new Coordinates(y, x);
        this.id = id;
        this.neighbors = new ArrayList<>();
    }

}