package game.json;

import com.google.gson.annotations.SerializedName;

public class PositionData {
    @SerializedName("lon")
    private Double x;

    @SerializedName("lat")
    private Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
