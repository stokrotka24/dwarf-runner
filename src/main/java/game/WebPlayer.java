package game;

import osm.Coordinates;
import osm.Node;
import osm.OsmService;

public class WebPlayer extends AbstractPlayer {

    public WebPlayer(int id, Node node) {
        super(id, node);
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.WEB;
    }

    @Override
    public boolean pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf)) {
            this.points += dwarf.getPoints();
            return true;
        }
        return false;
    }

    @Override
    public int makeMove(Move move, AbstractGame game) {
        Coordinates from = coords;
        Coordinates to = null;

        Double x = from.getX();
        Double y = from.getY();

        // too far from node so only move back to node or to next node on this road
        if (from.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
            for (int i = 0; i < 4; i++) {
                if (move.getWebMove() == WebMove.fromInt(i)) {
                    // int next = node.nextNeighbor(from);
                    // was changed, if doesn't work revert (should be fine tho)
                    Coordinates nextNode = node.nextNeighbor(from);
                    Double next_x = nextNode.getX();
                    Double next_y = nextNode.getY();

                    double back = Math.toDegrees(Math.atan2(node.getY() - y, node.getX() - x)) - (i * 90);
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
                            to = node.getCoords();
                        }
                    }
                    break;
                }
            }
        } else {
            // find node closest to direction given by WebMove move
            // null if there are no such nodes in the direction's quarter circle
            for (int i = 0; i < 4; i++) {
                if (move.getWebMove() == WebMove.fromInt(i)) {
                    double mini = 1000;
                    for (int j = 0; j < node.getNeighbors().size(); j++) {
                        Coordinates neighbor = node.getNeighbors().get(j);
                        double angle = Math.toDegrees(Math.atan2(neighbor.getY() - y, neighbor.getX() - x)) - (i * 90);
                        if (angle < 0) {
                            angle += 360;
                        }
                        if (angle >= 45 && angle <= 135) {
                            if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                                mini = angle;
                                to = node.getNeighbors().get(j);
                            }
                        }
                    }
                    double angle = Math.toDegrees(Math.atan2(node.getY() - y, node.getX() - x)) - (i * 90);
                    if (angle < 0) {
                        angle += 360;
                    }
                    if (angle >= 45 && angle <= 135) {
                        if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                            to = node.getCoords();
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
            double t = Math.min(1, game.getWebSpeed() / dist);
            double newX = from.getX() + t * (to.getX() - from.getX());
            double newY = from.getY() + t * (to.getY() - from.getY());
            this.setX(newX);
            this.setY(newY);
            return 0;
        }
        return 1;
    }
}