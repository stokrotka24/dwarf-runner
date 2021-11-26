package osm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Way
 */
public class Way {

    private Long id;
    private List<Long> nodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public void setNodes(List<Long> nodes) {
        this.nodes = nodes;
    }

    public Way(Long id, List<Long> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public List<Long> getAdjacent(Long nodeId) {
        int index = nodes.indexOf(nodeId);
        if (index == 0) {
            return List.of(nodes.get(1));
        }
        if (index == nodes.size()-1) {
            return List.of(nodes.get(index-1));
        }
        return List.of(nodes.get(index-1),nodes.get(index+1));
    }
}