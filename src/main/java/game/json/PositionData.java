package game.json;

import com.google.gson.annotations.SerializedName;

public class PositionData {
    @SerializedName("username")
    private String username;

    @SerializedName("lon")
    private Double x;

    @SerializedName("lat")
    private Double y;

    public PositionData(String username, Double x, Double y) {
        this.username = username;
        this.x = x;
        this.y = y;
    }
}
