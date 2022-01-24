package osm;

public class Coordinates {

    private Double y;
    private Double x;

    public Coordinates(Double x, Double y) {
        this.y = y;
        this.x = x;
    }

    public Coordinates(Coordinates coords) {
        this.x = coords.getX();
        this.y = coords.getY();
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

    public double distanceTo(Coordinates to) {
        double deltaX = x - to.getX();
        double deltaY = y - to.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public boolean equals(Coordinates coords) {
        boolean eq = (x.equals(coords.getX()) && y.equals(coords.getY()));
        return eq;
    }
}
