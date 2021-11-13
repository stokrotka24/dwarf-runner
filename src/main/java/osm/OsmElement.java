package osm;

import java.util.List;

/**
 * OsmElement
 */
public class OsmElement {
    private String type;
    private Integer id;
    private List<Integer> nodes;
    private Float lat;
    private Float lon;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public OsmElement(String type, Integer id, Float lat, Float lon) {
        this.type = type;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public OsmElement() {
        
    }
}