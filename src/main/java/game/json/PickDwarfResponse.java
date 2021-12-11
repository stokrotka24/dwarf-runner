package game.json;

import com.google.gson.annotations.SerializedName;

public class PickDwarfResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("points")
    private Integer points;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
