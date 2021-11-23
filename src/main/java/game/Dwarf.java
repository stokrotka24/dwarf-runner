package game;

import osm.Node;

//TODO set points
public class Dwarf {
    private Node node;
    private int points;

    public Dwarf(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
