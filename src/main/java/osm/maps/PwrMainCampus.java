package osm.maps;

public class PwrMainCampus extends OsmMap {

    private static PwrMainCampus instance = null;

    private PwrMainCampus() {
        parseMap("data/maps/pwr.osm");
        south = 51.107052;
        west = 17.054451;
        north = 51.111087;
        east = 17.062905;
    }

    public static PwrMainCampus getInstance() {
        if (instance == null) {
            instance = new PwrMainCampus();
        }
        return instance;
    }
}
