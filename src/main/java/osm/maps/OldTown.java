package osm.maps;

public class OldTown extends OsmMap {

    private static OldTown instance = null;

    private OldTown() {
        parseMap("data/maps/rynek.osm");
    }

    public static OldTown getInstance() {
        if (instance == null) {
            instance = new OldTown();
        }
        return instance;
    }

}
