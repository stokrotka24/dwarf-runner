package game;

import com.google.gson.annotations.SerializedName;
import osm.Node;

//TODO set points
public class Dwarf {
    private transient Node node;
    private transient Integer points = 100;

    @SerializedName("lon")
    private Double x;

    @SerializedName("lat")
    private Double y;

    @SerializedName("id")
    private final int id;

    public Dwarf(Node node, int id) {
        this.node = node;
        this.x = node.getX();
        this.y = node.getY();
        this.id = id;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }
}
