package osm;

import java.util.List;

/**
 * OsmObject
 */
public class OsmObject {
    private List<OsmElement> elements;

    public List<OsmElement> getElements() {
        return elements;
    }

    public void setElements(List<OsmElement> elements) {
        this.elements = elements;
    }

    public OsmObject(List<OsmElement> elements) {
        this.elements = elements;
    }

    public OsmObject() {
    }

}