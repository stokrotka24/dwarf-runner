package game;

import game.json.MobileMove;
import game.json.WebMove;

public class Move {
    private Double x;
    private Double y;
    private String arrow;

    public Move(WebMove webMove) {
    }

    public Move(MobileMove mobileMove) {
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }


    public String getArrow() {
        return arrow;
    }

}
