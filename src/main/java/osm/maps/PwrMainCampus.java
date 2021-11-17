package osm.maps;

public class PwrMainCampus extends OsmMap {

    private static PwrMainCampus instance = null;

    private PwrMainCampus() {
        parseMap("data/maps/pwr.osm");
    }

    public static PwrMainCampus getInstance() {
        if (instance == null) {
            instance = new PwrMainCampus();
        }
        return instance;
    }
}
