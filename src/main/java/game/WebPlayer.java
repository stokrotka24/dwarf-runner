package game;

import osm.Coordinates;
import osm.Node;
import osm.OsmService;

import java.sql.Timestamp;
import server.Logger;

public class WebPlayer extends AbstractPlayer {
    private Long lastMoveTimestamp = 0L;

    public WebPlayer(int id, Node node) {
        super(id, node);
        this.coords = new Coordinates(node.getCoords());
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
        if (new Timestamp(System.currentTimeMillis()).getTime() - lastMoveTimestamp < 200) {
            // ignore move by 200 ms
            return MoveValidation.WEB_VALID_MOVE;
        }
        //Logger logger = Logger.getInstance();

        Coordinates from = coords;
        Coordinates to = null;

        Double x = from.getX();
        Double y = from.getY();

        Node newNode = null;

        // too far from node so only move back to node or to next node on this road
        if (from.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
            //logger.info("far");
            for (int i = 0; i < 8; i++) {
                if (move.getWebMove() == WebMove.fromInt(i)) {
                    Coordinates nextNode = node.nextNeighbor(from);
                    Double next_x = nextNode.getX();
                    Double next_y = nextNode.getY();

                    double back = Math.toDegrees(Math.atan2(node.getY() - y, node.getX() - x)) - (i * 45);
                    double forward = Math.toDegrees(Math.atan2(next_y - y, next_x - x)) - (i * 45);

                    if (back < 0) {
                        back += 360;
                    }
                    if (forward < 0) {
                        forward += 360;
                    }
                    //logger.info("neighbor: " + forward);
                    //logger.info("node: " + back);

                    if (Math.abs(forward - 45) <= Math.abs(back - 45)) {
                        if (forward >= 22.5 && forward <= 67.5) {
                            to = nextNode;
                            newNode = game.getOsmService().getNodeByCoords(to);
                        }
                    } else {
                        if (back >= 22.5 && back <= 67.5) {
                            to = node.getCoords();
                            newNode = null;
                        }
                    }
                    break;
                }
            }
        } else {
            //logger.info("close");
            // find node closest to direction given by WebMove move
            // null if there are no such nodes in the direction's quarter circle
            for (int i = 0; i < 8; i++) {
                if (move.getWebMove() == WebMove.fromInt(i)) {
                    double mini = 1000;
                    for (int j = 0; j < node.getNeighbors().size(); j++) {
                        Coordinates neighbor = node.getNeighbors().get(j);
                        if (!neighbor.equals(node.getCoords())) {
                            double angle =
                                Math.toDegrees(Math.atan2(neighbor.getY() - y, neighbor.getX() - x))
                                    - (i * 45);
                            if (angle < 0) {
                                angle += 360;
                            }
                            //logger.info("neighbor: " + angle);
                            if (angle >= 22.5 && angle <= 67.5) {
                                if (Math.abs(angle - 45) < Math.abs(mini - 45)) {
                                    mini = angle;
                                    to = node.getNeighbors().get(j);
                                    newNode = game.getOsmService().getNodeByCoords(to);
                                }
                            }
                        }
                    }
                    if (!coords.equals(node.getCoords())) {
                        double angle =
                            Math.toDegrees(Math.atan2(node.getY() - y, node.getX() - x)) - (i * 45);
                        if (angle < 0) {
                            angle += 360;
                        }
                        //logger.info("node: " + angle);
                        if (angle >= 22.5 && angle <= 67.5) {
                            if (Math.abs(angle - 45) < Math.abs(mini - 45)) {
                                to = node.getCoords();
                                newNode = null;
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
            double t = Math.min(1, 0.2 * OsmService.METRE * game.getWebSpeed() / 3.6 / dist);
            double newX = from.getX() + t * (to.getX() - from.getX());
            double newY = from.getY() + t * (to.getY() - from.getY());
            this.setX(newX);
            this.setY(newY);
            if (newNode != null) {
                if (node.getCoords().distanceTo(coords) >
                    newNode.getCoords().distanceTo(coords)) {
                    node = new Node(newNode);
                }
            }
            lastMoveTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
            return MoveValidation.WEB_VALID_MOVE;
        }
        return MoveValidation.WEB_INVALID_MOVE;
    }
}