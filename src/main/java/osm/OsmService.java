package osm;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

/**
 * OsmService
 */
public class OsmService {
    private List<Way> ways;
    private List<Node> nodes;

    public List<Way> getWays() {
        return ways;
    }

    public void setWays(List<Way> ways) {
        this.ways = ways;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public OsmService(Integer mapType) {
        parseMap(mapType);
    }

    public List<Node> getUniqueRandomNodes(Integer numberOfNodes) throws InvalidParameterException {
        if (numberOfNodes > nodes.size()) {
            throw new InvalidParameterException("demanded number of nodes greater than actual number of nodes");
        }
        List<Node> copy = new ArrayList<Node>(nodes);
        Collections.shuffle(copy);
        return copy.subList(0, numberOfNodes);
    }

    public Node getRandomNode() throws InvalidTargetObjectTypeException {
        if (nodes.size() == 0) {
            throw new InvalidTargetObjectTypeException("node list empty, cannot access random node");
        }
        Random r = new Random();
        return nodes.get(r.nextInt(nodes.size()));
    }

    /*
     * TODO: Implement parseMap
     */
    private void parseMap(Integer mapType) {

    }

}