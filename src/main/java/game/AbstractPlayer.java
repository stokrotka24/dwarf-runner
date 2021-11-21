package game;

import osm.Node;

public abstract class AbstractPlayer {
    private int id;
    protected int points = 0;
    private Node node;
    private float positionX;
    private float positionY;

    public AbstractPlayer(int id) {
        this.id = id;
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

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getPositionY() {
        return positionY;
    }

    public abstract GamePlatform getPlatform();

    public abstract boolean pickUpDwarf(Dwarf dwarf);

    public boolean isNearToDwarf(Dwarf dwarf) {
        //TODO: when OSM will be implemented
        return false;
    }
}
