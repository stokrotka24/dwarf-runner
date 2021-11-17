package osm.maps;

public class CentennialHall extends OsmMap {

    private static CentennialHall instance = null;

    private CentennialHall() {
        parseMap("data/maps/hala.osm");
    }

    public static CentennialHall getInstance() {
        if (instance == null) {
            instance = new CentennialHall();
        }
        return instance;
    }
}
