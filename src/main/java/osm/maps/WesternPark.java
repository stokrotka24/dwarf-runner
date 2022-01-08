package osm.maps;

public class WesternPark extends OsmMap {

    private static WesternPark instance = null;

    private WesternPark() {
        parseMap("data/maps/park_zachodni.osm");
        south = 51.129950;
        west = 16.973212;
        north = 51.137153;
        east = 16.987932;
    }

    public static WesternPark getInstance() {
        if (instance == null) {
            instance = new WesternPark();
        }
        return instance;
    }
}
