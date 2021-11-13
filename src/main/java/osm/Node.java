package osm;

/**
 * Node
 */
public class Node {

    private Integer id;
    private Float x;
    private Float y;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Node(OsmElement element) {
        this.x = element.getLon();
        this.y = element.getLat();
        this.id = element.getId();
    }

}