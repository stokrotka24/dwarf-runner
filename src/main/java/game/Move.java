package game;

import game.json.MobileMove;
import osm.Coordinates;

public class Move {
    private Coordinates coords;
    private WebMove webMove;
    private Double timestamp;

    public Move(MobileMove mobileMove) {
        this.coords = new Coordinates(mobileMove.getLon(), mobileMove.getLat());
        this.timestamp = mobileMove.getTimestamp();
    }

    public Move(WebMove webWebMove) {
        this.webMove = webWebMove;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public WebMove getWebMove() {
        return webMove;
    }

    public Double getTimestamp() {
        return timestamp;
    }
}
