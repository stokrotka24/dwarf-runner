package game.json;

import com.google.gson.annotations.SerializedName;
import game.MoveValidation;

public class MobileMoveResponse {
    @SerializedName("response")
    private MoveValidation response;

    @SerializedName("lon")
    private Double x;

    @SerializedName("lat")
    private Double y;

    @SerializedName("punishment_time")
    private Double punishmentTime;

    public MobileMoveResponse(MoveValidation response, Double x, Double y, Double punishmentTime) {
        this.response = response;
        this.x = x;
        this.y = y;
        this.punishmentTime = punishmentTime;
    }
}
