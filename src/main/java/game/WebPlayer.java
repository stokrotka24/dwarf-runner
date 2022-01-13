package game;

import osm.Coordinates;
import osm.Node;
import osm.OsmService;

public class WebPlayer extends AbstractPlayer {

    public WebPlayer(int id, Node node) {
        super(id, node);
        this.coords = node.getCoords();
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.WEB;
    }

    @Override
    public Double getBanTimeLeft() {
        return null;
    }

    @Override
    public int pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf)) {
            this.points += dwarf.getPoints();
            return 1;
        }

        return 0;
    }

    @Override
    public MoveValidation makeMove(Move move, AbstractGame game) {
        Coordinates from = coords;
        Coordinates to = null;

        Double x = from.getX();
        Double y = from.getY();

        Node newNode = node;

        // too far from node so only move back to node or to next node on this road
        if (from.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
            for (int i = 0; i < 4; i++) {
                if (move.getWebMove() == WebMove.fromInt(i)) {
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
                            newNode = game.getOsmService().getNodeByCoords(to);
                        }
                    } else {
                        if (back >= 45 && back <= 135) {
                            to = node.getCoords();
                            newNode = node;
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
                                newNode = game.getOsmService().getNodeByCoords(to);
                            }
                        }
                    }
                    if (coords != node.getCoords()) {
                        double angle =
                            Math.toDegrees(Math.atan2(node.getY() - y, node.getX() - x)) - (i * 90);
                        if (angle < 0) {
                            angle += 360;
                        }
                        if (angle >= 45 && angle <= 135) {
                            if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                                to = node.getCoords();
                                newNode = node;
                            }
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
            double t = Math.min(1, OsmService.METRE * game.getWebSpeed() / dist);
            double newX = from.getX() + t * (to.getX() - from.getX());
            double newY = from.getY() + t * (to.getY() - from.getY());
            this.setX(newX);
            this.setY(newY);
            if (node.getCoords() != newNode.getCoords()) {
                if (node.getCoords().distanceTo(coords) >
                    newNode.getCoords().distanceTo(coords)) {
                    node = new Node(newNode);
                }
            }
            return MoveValidation.WEB_VALID_MOVE;
        }
        return MoveValidation.WEB_INVALID_MOVE;
    }
}