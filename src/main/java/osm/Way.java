package osm;

import java.util.List;

/**
 * Way
 */
public class Way {
    private Integer id;
    private List<Integer> nodes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getNodes() {
        return nodes;
    }

    public void setNodes(List<Integer> nodes) {
        this.nodes = nodes;
    }

    public Way(OsmElement element) {
        this.id = element.getId();
        this.nodes = element.getNodes();
    }
    
}