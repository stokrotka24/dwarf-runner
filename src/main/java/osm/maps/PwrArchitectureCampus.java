package osm.maps;

public class PwrArchitectureCampus extends OsmMap {

    private static PwrArchitectureCampus instance = null;

    private PwrArchitectureCampus() {
        parseMap("data/maps/architektura.osm");
    }

    public static PwrArchitectureCampus getInstance() {
        if (instance == null) {
            instance = new PwrArchitectureCampus();
        }
        return instance;
    }
}
