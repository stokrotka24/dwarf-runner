package osm.maps;

public class SzczytnickiPark extends OsmMap {

    private static SzczytnickiPark instance = null;

    private SzczytnickiPark() {
        parseMap("data/maps/park_szczytnicki.osm");
        south = 51.108682;
        west = 17.072775;
        north = 51.117896;
        east = 17.090757;
    }

    public static SzczytnickiPark getInstance() {
        if (instance == null) {
            instance = new SzczytnickiPark();
        }
        return instance;
    }
}
