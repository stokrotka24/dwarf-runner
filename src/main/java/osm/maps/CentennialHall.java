package osm.maps;

public class CentennialHall extends OsmMap {

    private static CentennialHall instance = null;

    private CentennialHall() {
        parseMap("data/maps/hala.osm");
        south = 51.105233;
        west = 17.07108;
        north = 51.112225;
        east = 17.08535;
    }

    public static CentennialHall getInstance() {
        if (instance == null) {
            instance = new CentennialHall();
        }
        return instance;
    }
}
