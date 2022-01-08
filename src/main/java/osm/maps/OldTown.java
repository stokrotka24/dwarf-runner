package osm.maps;

public class OldTown extends OsmMap {

    private static OldTown instance = null;

    private OldTown() {
        parseMap("data/maps/rynek.osm");
        south = 51.10714;
        west = 17.02645;
        north = 51.11473;
        east = 17.03928;
    }

    public static OldTown getInstance() {
        if (instance == null) {
            instance = new OldTown();
        }
        return instance;
    }

}
