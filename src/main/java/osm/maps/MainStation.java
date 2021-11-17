package osm.maps;

public class MainStation extends OsmMap {

    private static MainStation instance = null;

    private MainStation() {
        parseMap("data/maps/pkp.osm");
    }

    public static MainStation getInstance() {
        if (instance == null) {
            instance = new MainStation();
        }
        return instance;
    }
}
