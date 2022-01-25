package dbconn.jsonclasses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MapStatistics {
    
    @SerializedName("map_id")
    private int mapId;

    @SerializedName("avg_place")
    private int avgPlace;

    @SerializedName("winrate")
    private int winrate;
    
    public MapStatistics(int mapId, int avgPlace, int winrate) {
        this.mapId = mapId;
        this.avgPlace = avgPlace;
        this.winrate = winrate;
    }

    public int getMapId()
    {
        return mapId;
    }

    public void setMapId(int mapId)
    {
        this.mapId = mapId;
    }

    public int getAvgPlace()
    {
        return avgPlace;
    }

    public void setAvgPlace(int avgPlace)
    {
        this.avgPlace = avgPlace;
    }

    public int getWinrate()
    {
        return winrate;
    }

    public void setWinrate(int winrate)
    {
        this.winrate = winrate;
    }
}
