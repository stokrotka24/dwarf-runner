package game;

import osm.Coordinates;
import osm.Node;
import osm.OsmService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MobilePlayer extends AbstractPlayer {

    private Double banTimestamp = null;
    private Double banDuration = null;
    private boolean positionBan = false;
    private boolean speedBan = false;
    private Coordinates speedBanCoords;
    private final List<Double> distances;
    private final List<Double> timestamps;
    private Coordinates lastPosition = null;

    public MobilePlayer(int id, Node node) {
        super(id, node);
        this.coords = new Coordinates(0.0, 0.0);
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

    public void setBanTimestamp(Double banTimestamp) {
        this.banTimestamp = banTimestamp;
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.MOBILE;
    }

    @Override
    public Double getBanTimeLeft() {
        return banDuration - (new Timestamp(System.currentTimeMillis()).getTime() - banTimestamp);
    }

    @Override
    public int pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf) && !isBanned()) {
            this.points += dwarf.getPoints();
            return 1;
        }
        return 0;
    }

    private boolean isBanned() {
        if (banTimestamp == null) {
            return false;
        }
        if (new Timestamp(System.currentTimeMillis()).getTime() - banTimestamp >= banDuration) {
            banTimestamp = null;
            return false;
        }
        return true;
    }

    private void beginSpeedBan(Double timestamp, Coordinates coords, double speed, double maxSpeed) {
        if (speed < 2 * maxSpeed) {
            banDuration = 20000.0;
        } else if (speed < 3 * maxSpeed) {
            banDuration = 40000.0;
        } else {
            banDuration = 90000.0;
        }
        speedBan = true;
        setBanTimestamp(timestamp);
        speedBanCoords = coords;
    }

    @Override
    public MoveValidation makeMove(Move move, AbstractGame game) {
        Coordinates position = move.getCoords();
        coords = move.getCoords();
        if (speedBan) { // must stay in place for a set amount of time
            if (position.distanceTo(speedBanCoords) > OsmService.BAN_RADIUS) {
                setBanTimestamp(move.getTimestamp());
                return MoveValidation.SPEED_BAN_CONTINUE; // moved away so reset ban timer
            }
            if (isBanned()) {
                return MoveValidation.SPEED_BAN_CONTINUE; // not long enough in place yet
            }
            speedBan = false;
        }
        if (positionBan) { // must return to node
            if (position.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
                return MoveValidation.POSITION_BAN_CONTINUE; // not in node radius yet
            }
            positionBan = false;
        }
        updateDistances(position, move.getTimestamp());
        // TODO check units for that if condition below (should be m/s for now)
        double speed = distances.stream().mapToDouble(Double::doubleValue).sum() /
            ((timestamps.get(4) - timestamps.get(0)) / 1000) / 3.6;
        double maxSpeed = game.getMobileMaxSpeed();
        if (maxSpeed < speed) {
            beginSpeedBan(move.getTimestamp(), position, speed, maxSpeed);
            return MoveValidation.SPEED_BAN; // max speed exceeded
        }
        // not in node radius so check if on the road
        if (position.distanceTo(node.getCoords()) > OsmService.NODE_RADIUS) {
            Coordinates nextNode = node.nextNeighbor(position);
            double roadDist = node.distLinePoint(nextNode, position);
            if (roadDist > OsmService.MAX_DIST_FROM_ROAD) {
                positionBan = true;
                return MoveValidation.POSITION_BAN; // too far from road
            }
            double maxDist = node.getCoords().distanceTo(nextNode);
            double dist = node.getCoords().distanceTo(position);
            if (dist > maxDist) {
                positionBan = true;
                return MoveValidation.POSITION_BAN; // too far from node (will probably exceed max speed so may never happen)
            }
            if (dist > maxDist / 2) {
                /* set new node */
                node = new Node(game.getOsmService().getNodeByCoords(nextNode));
            }
            return MoveValidation.MOBILE_VALID_MOVE; // legal move
        }
        return MoveValidation.MOBILE_VALID_MOVE; // in node radius so legal for sure
    }
}