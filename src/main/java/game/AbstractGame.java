package game;

import osm.Coordinates;

import java.util.List;

public abstract class AbstractGame {

    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private double webSpeed;
    private double mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    /* if player is farther than this from the node, don't allow stepping off the current way */
    private double onlyBackOrForward = 0.000004;
    private Integer timeToEnd;

    public AbstractGame(int id, GameMap gameMap, List<AbstractPlayer> players, double webSpeed,
        double mobileMaxSpeed, List<Dwarf> dwarfs, Integer timeToEnd) {
        this.id = id;
        this.gameMap = gameMap;
        this.players = players;
        this.webSpeed = webSpeed;
        this.mobileMaxSpeed = mobileMaxSpeed;
        this.dwarfs = dwarfs;
        this.timeToEnd = timeToEnd;
    }
  
    public void setOnlyBackOrForward(double onlyBackOrForward) {
        this.onlyBackOrForward = onlyBackOrForward;
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public Integer getTimeToEnd() {
        return timeToEnd;
    }

    public void webMove(WebPlayer player, WebMove move) {

        Coordinates from = player.getCoords();
        Coordinates to = null;

        Double x = from.getX();
        Double y = from.getY();

        // to far from node so only move back to node or to next node on this road
        if (from.distanceTo(player.getNode().getCoords()) > onlyBackOrForward) {
            for (int i = 0; i < 4; i++) {
                if (move == WebMove.fromInt(i)) {
                    int next = player.getNode().nextNeighbor(from);
                    Coordinates nextNode = player.getNode().getNeighbors().get(next);
                    Double next_x = nextNode.getX();
                    Double next_y = nextNode.getY();

                    double back = Math.toDegrees(Math.atan2(player.getNode().getY() - y, player.getNode().getX() - x)) - (i * 90);
                    double forward = Math.toDegrees(Math.atan2(next_y - y, next_x - x)) - (i * 90);

                    if (back < 0) {
                        back += 360;
                    }
                    if (forward < 0) {
                        forward += 360;
                    }

                    if (Math.abs(forward - 90) <= Math.abs(back - 90)) {
                        if (forward >= 45 && forward <= 135) {
                            to = nextNode;
                        }
                    } else {
                        if (back >= 45 && back <= 135) {
                            to = player.getNode().getCoords();
                        }
                    }
                    break;
                }
            }
        } else {
            // find node closest to direction given by WebMove move
            // null if there are no such nodes in the direction's quarter circle
            for (int i = 0; i < 4; i++) {
                if (move == WebMove.fromInt(i)) {
                    double mini = 1000;
                    for (int j = 0; j < player.getNode().getNeighbors().size(); j++) {
                        Coordinates neighbor = player.getNode().getNeighbors().get(j);
                        double angle = Math.toDegrees(Math.atan2(neighbor.getY() - y, neighbor.getX() - x)) - (i * 90);
                        if (angle < 0) {
                            angle += 360;
                        }
                        if (angle >= 45 && angle <= 135) {
                            if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                                mini = angle;
                                to = player.getNode().getNeighbors().get(j);
                            }
                        }
                    }
                    double angle = Math.toDegrees(Math.atan2(player.getNode().getY() - y, player.getNode().getX() - x)) - (i * 90);
                    if (angle < 0) {
                        angle += 360;
                    }
                    if (angle >= 45 && angle <= 135) {
                        if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                            to = player.getNode().getCoords();
                        }
                    }
                    break;
                }
            }
        }

        // X - lon
        // Y - lat
        if (to != null) {
            double dist = from.distanceTo(to);
            double t = Math.min(1, webSpeed / dist);
            double newX = from.getX() + t * (to.getX() - from.getX());
            double newY = from.getY() + t * (to.getY() - from.getY());
            player.setX(newX);
            player.setY(newY);
        }
    }
}
