package osm.maps;

public class PwrArchitectureCampus extends OsmMap {

    private static PwrArchitectureCampus instance = null;

    private PwrArchitectureCampus() {
        parseMap("data/maps/architektura.osm");
        south = 51.118085;
        west = 17.053115;
        north = 51.119455;
        east = 17.056886;
    }

    public static PwrArchitectureCampus getInstance() {
        if (instance == null) {
            instance = new PwrArchitectureCampus();
        }
        return instance;
    }
}
