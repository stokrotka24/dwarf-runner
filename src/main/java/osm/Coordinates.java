package osm;

public class Coordinates {

    private Double y;
    private Double x;

    public Coordinates(Double y, Double x) {
        this.y = y;
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public Double getX() {
        return x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void setX(Double x) {
        this.x = x;
    }
}
