package dbconn.jsonclasses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UserStatisticsResponse {
    
    @SerializedName("solo_place")
    private int soloPlace;

    @SerializedName("avg_place")
    private int avgPlace;

    @SerializedName("team_place")
    private int teamPlace;

    @SerializedName("winrate")
    private int winrate;

    @SerializedName("maps")
    private List<MapStatistics> maps;

    public UserStatisticsResponse(List<MapStatistics> maps, int soloPlace, int avgPlace, int teamPlace, int winrate) {
        this.soloPlace = soloPlace;
        this.avgPlace = avgPlace;
        this.teamPlace = teamPlace;
        this.winrate = winrate;
        this.maps = maps;
    }

    public int getSoloPlace()
    {
        return soloPlace;
    }

    public void setSoloPlace(int soloPlace)
    {
        this.soloPlace = soloPlace;
    }

    public int getAvgPlace()
    {
        return avgPlace;
    }

    public void setAvgPlace(int avgPlace)
    {
        this.avgPlace = avgPlace;
    }

    public int getTeamPlace()
    {
        return teamPlace;
    }

    public void setTeamPlace(int teamPlace)
    {
        this.teamPlace = teamPlace;
    }

    public int getWinrate()
    {
        return winrate;
    }

    public void setWinrate(int winrate)
    {
        this.winrate = winrate;
    }

    public List<MapStatistics> getMaps()
    {
        return maps;
    }

    public void setMaps(List<MapStatistics> maps)
    {
        this.maps = maps;
    }
}
