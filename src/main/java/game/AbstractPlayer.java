package game;

import osm.Node;
import server.ClientHandler;

public abstract class AbstractPlayer {
    private int id;
    protected int points = 0;
    private Node node;
    private float positionX;
    private float positionY;
    private ClientHandler handler;

    public AbstractPlayer(int id, ClientHandler handler) {
        this.id = id;
        this.handler = handler;
    }

    public void sendMessage(String msg) {
        handler.sendMessage(msg);
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
