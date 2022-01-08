package osm.maps;

public class MainStation extends OsmMap {

    private static MainStation instance = null;

    private MainStation() {
        parseMap("data/maps/pkp.osm");
        south = 51.098317;
        west = 17.033529;
        north = 51.099887;
        east = 17.038985;
    }

    public static MainStation getInstance() {
        if (instance == null) {
            instance = new MainStation();
        }
        return instance;
    }
}
