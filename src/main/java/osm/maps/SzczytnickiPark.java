package osm.maps;

public class SzczytnickiPark extends OsmMap{
private static SzczytnickiPark instance = null;

    private SzczytnickiPark()
    {
        parseMap("data/maps/park_szczytnicki.osm");
    }

    public static SzczytnickiPark getInstance()
    {
        if (instance == null)
            instance = new SzczytnickiPark();
        return instance;
    }
}
