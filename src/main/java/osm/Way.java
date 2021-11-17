package osm;

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
}