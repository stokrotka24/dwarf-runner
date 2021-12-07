package game.json;

import com.google.gson.annotations.SerializedName;
import messages.JsonRequired;

public class MobileMove {
    @JsonRequired
    @SerializedName("lon")
    private Double lon;

    @JsonRequired
    @SerializedName("lat")
    private Double lat;

    @JsonRequired
    @SerializedName("timestamp")
    private Double timestamp;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
