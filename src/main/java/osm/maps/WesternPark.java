package osm.maps;

public class WesternPark extends OsmMap {

    private static WesternPark instance = null;

    private WesternPark() {
        parseMap("data/maps/park_zachodni.osm");
    }

    public static WesternPark getInstance() {
        if (instance == null) {
            instance = new WesternPark();
        }
        return instance;
    }
}
