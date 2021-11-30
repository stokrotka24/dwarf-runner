package game;

import osm.Coordinates;
import osm.Node;

public abstract class AbstractPlayer {

    private final int id;
    protected int points = 0;
    private Node node;
    private Coordinates coords;

    public AbstractPlayer(int id) {
        this.id = id;
        coords = new Coordinates(0.0,0.0);
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public void setNode(Node node) {
        this.node = new Node(node);
    }

    public Node getNode() {
        return node;
    }

    public abstract GamePlatform getPlatform();

    public abstract boolean pickUpDwarf(Dwarf dwarf);

    public boolean isNearToDwarf(Dwarf dwarf) {
        //TODO: when OSM will be implemented
        return false;
    }

    public void setX(Double x) {
        coords.setX(x);
    }

    public void setY(Double y) {
        coords.setY(y);
    }

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        setX(coords.getX());
        setY(coords.getY());
    }

}
