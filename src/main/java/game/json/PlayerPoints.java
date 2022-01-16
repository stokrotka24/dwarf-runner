package game.json;

import com.google.gson.annotations.SerializedName;

public class PlayerPoints {
    @SerializedName("username")
    private String username;

    @SerializedName("points")
    private Integer points;

    public PlayerPoints(String username, Integer points) {
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public Integer getPoints() {
        return points;
    }
}
