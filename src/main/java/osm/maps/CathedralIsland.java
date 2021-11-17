package osm.maps;

public class CathedralIsland extends OsmMap {

    private static CathedralIsland instance = null;

    private CathedralIsland() {
        parseMap("data/maps/ostrow_tumski.osm");
    }

    public static CathedralIsland getInstance() {
        if (instance == null) {
            instance = new CathedralIsland();
        }
        return instance;
    }
}
