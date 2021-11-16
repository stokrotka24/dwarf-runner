package osm;

/**
 * Node
 */
public class Node {

    private Long id;
    private Double lat;
    private Double lon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return lat;
    }

    public void setX(Double lat) {
        this.lat = lat;
    }

    public Double getY() {
        return lon;
    }

    public void setY(Double lon) {
        this.lon = lon;
    }

    public Node(Long id, Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }

}