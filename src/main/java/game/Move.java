package game;

import game.json.MobileMove;

public class Move {
    private Double x;
    private Double y;
    private WebMove webMove;
    private Double timestamp;

    public Move(MobileMove mobileMove) {
        this.x = mobileMove.getLon();
        this.y = mobileMove.getLat();
        this.timestamp = mobileMove.getTimestamp();
    }

    public Move(WebMove webWebMove) {
        this.webMove = webWebMove;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public WebMove getWebMove() {
        return webMove;
    }

    public Double getTimestamp() {
        return timestamp;
    }
}
