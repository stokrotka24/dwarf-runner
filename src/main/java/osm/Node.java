package osm;

import game.WebMove;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Double getLat() {
        return coords.getLat();
    }
    public Double getLon() {
        return coords.getLon();
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

    public Node(Long id, Double lat, Double lon) {
        this.coords = new Coordinates(lat, lon);
        this.id = id;
        this.neighbors = new ArrayList<>();
    }

}