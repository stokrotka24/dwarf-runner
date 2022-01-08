package osm.maps;

public class CathedralIsland extends OsmMap {

    private static CathedralIsland instance = null;

    private CathedralIsland() {
        parseMap("data/maps/ostrow_tumski.osm");
        south = 51.111929;
        west = 17.041436;
        north = 51.117398;
        east = 17.051522;
    }

    public static CathedralIsland getInstance() {
        if (instance == null) {
            instance = new CathedralIsland();
        }
        return instance;
    }
}
