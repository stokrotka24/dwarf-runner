package lobby.json;

import com.google.gson.annotations.SerializedName;

public class MapBounds {
    @SerializedName("map_id")
    private int mapId;

    @SerializedName("north")
    private double north;

    @SerializedName("south")
    private double south;

    @SerializedName("east")
    private double east;

    @SerializedName("west")
    private double west;

    public MapBounds(int mapId, double north, double south, double east, double west) {
        this.mapId = mapId;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }
}
