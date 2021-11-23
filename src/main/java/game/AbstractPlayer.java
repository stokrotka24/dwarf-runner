package game;

import osm.Coordinates;
import osm.Node;

public abstract class AbstractPlayer {


    private final int id;
    protected int points = 0;
    private Node node;
    private Coordinates coords;


    public AbstractPlayer(int id, Node node) {
        this.id = id;
        this.node = node;
        this.coords = node.getCoords();
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public void setNode(Node node) {
        this.node = node;
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

    public void setLon(Double lon) {
        coords.setLon(lon);
    }

    public void setLat(Double lat) {
        coords.setLat(lat);
    }

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

}
