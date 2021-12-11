package game;

import java.util.ArrayList;
import java.util.List;
import osm.Coordinates;
import osm.Node;

import java.sql.Timestamp;
import osm.OsmService;

public class MobilePlayer extends AbstractPlayer {

    private Timestamp banTimestamp = null;
    private final List<Double> distances;
    private final List<Double> timestamps;
    private Coordinates lastPosition = null;

    public MobilePlayer(int id, Node node) {
        super(id, node);
        distances = new ArrayList<>();
        timestamps = new ArrayList<>();
        distances.add(0.0);
        distances.add(0.0);
        distances.add(0.0);
        distances.add(0.0);
        distances.add(0.0);
        timestamps.add(0.0);
        timestamps.add(0.0);
        timestamps.add(0.0);
        timestamps.add(0.0);
        timestamps.add(0.0);
    }

    private void updateDistances(Coordinates newPosition, double newTimestamp) {
        if (lastPosition == null) {
            lastPosition = new Coordinates(newPosition);
        } else {
            distances.remove(0);
            distances.add(lastPosition.distanceTo(newPosition) * 111000.0);
        }
        timestamps.remove(0);
        timestamps.add(newTimestamp);
    }

    public void setBanTimestamp(Timestamp banTimestamp) {
        this.banTimestamp = banTimestamp;
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.MOBILE;
    }

    @Override
    public boolean pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf) && !isBanned()) {
            this.points += dwarf.getPoints();
            return true;
        }
        return false;
    }

    private boolean isBanned() {
        if (banTimestamp == null) {
            return false;
        }
        if (new Timestamp(System.currentTimeMillis()).getTime() - banTimestamp.getTime() >= 100L) {
            //TODO: change 100L to real ban time
            banTimestamp = null;
            return false;
        }
        return true;
    }

    @Override
    public int makeMove(Move move, AbstractGame game) {
        Coordinates position = move.getCoords();
        updateDistances(position, move.getTimestamp());
        // TODO check units for that if condition below (should be m/s for now)
        if (game.getMobileMaxSpeed() * 3.6 <
            distances.stream().mapToDouble(Double::doubleValue).sum() /
                ((timestamps.get(4) - timestamps.get(0)) / 2)) {
            return 1; // max speed exceeded
        }
        // not in node radius so check if on the road
        if (position.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
            Coordinates nextNode = node.nextNeighbor(position);
            double roadDist = node.distLinePoint(nextNode, position);
            if (roadDist > OsmService.MAX_DIST_FROM_ROAD) {
                return 2; // too far from road
            }
            double maxDist = node.getCoords().distanceTo(nextNode);
            double dist = node.getCoords().distanceTo(position);
            if (dist > maxDist) {
                return 2; // too far from node (will probably exceed max speed so may never happen)
            }
            if (dist > maxDist / 2) {
                /* set new node */
                node = new Node(game.getNodeByCoords(nextNode));
            }
            return 0; // legal move
        }
        return 0; // in node radius so legal for sure
    }
}