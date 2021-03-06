package osm;

import game.GameMap;
import osm.maps.*;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * OsmService
 */
public class OsmService {
    // one degree is about 111 kilometers
    public static final double METRE = 1.0 / 111000.0;
    /* if player is farther than this from the node, don't allow stepping off the current way */
    public static double NODE_RADIUS = 0.000036; // value TBD
    public static double BAN_RADIUS = 0.000002; // value TBD
    public static double MAX_DIST_FROM_ROAD = 0.000002; // value TBD
    private OsmMap map;

    public OsmMap getMap() {
        return map;
    }

    public ArrayList<Node> getNodes() {
        return map.nodes;
    }

    public OsmService(Integer mapType) {
        switch (GameMap.fromInt(mapType)) {
            case CATHEDRAL_ISLAND:
                map = CathedralIsland.getInstance();
                break;
            case CENTENNIAL_HALL:
                map = CentennialHall.getInstance();
                break;
            case MAIN_STATION:
                map = MainStation.getInstance();
                break;
            case OLD_TOWN:
                map = OldTown.getInstance();
                break;
            case PWR_ARCHITECTURE_CAMPUS:
                map = PwrArchitectureCampus.getInstance();
                break;
            case PWR_MAIN_CAMPUS:
                map = PwrMainCampus.getInstance();
                break;
            case SZCZYTNICKI_PARK:
                map = SzczytnickiPark.getInstance();
                break;
            case WESTERN_PARK:
                map = WesternPark.getInstance();
                break;
        }
    }

    public List<Node> getUniqueRandomNodes(Integer numberOfNodes) throws InvalidParameterException {
        if (numberOfNodes > map.nodes.size()) {
            throw new InvalidParameterException(
                "demanded number of nodes greater than actual number of nodes");
        }
        List<Node> copy = new ArrayList<>(map.nodes);
        Collections.shuffle(copy);
        return copy.subList(0, numberOfNodes);
    }

    public Node getRandomNode() throws InvalidTargetObjectTypeException {
        if (map.nodes.size() == 0) {
            throw new InvalidTargetObjectTypeException(
                "node list empty, cannot access random node");
        }
        Random r = new Random();
        return map.nodes.get(r.nextInt(map.nodes.size()));
    }

    public Node getTheNearestNode(Coordinates coordinates) throws InvalidTargetObjectTypeException {
        if (map.nodes.size() == 0) {
            throw new InvalidTargetObjectTypeException(
                    "node list empty, cannot access random node");
        }

        Node theNearestNode = map.nodes.get(0);
        double min = theNearestNode.getCoords().distanceTo(coordinates);
        List<Node> nodes = map.nodes.subList(1, map.nodes.size());

        for (Node node: nodes) {
            double distance = node.getCoords().distanceTo(coordinates);
            if (distance < min) {
                min = distance;
                theNearestNode = node;
            }
        }
        return theNearestNode;
    }

    public static void setNodeRadius(double nodeRadius) {
        NODE_RADIUS = nodeRadius;
    }

    public Node getNodeByCoords(Coordinates coords) {
        return map.nodes.stream().filter(node -> node.getCoords().equals(coords)).findFirst().get();
    }
}