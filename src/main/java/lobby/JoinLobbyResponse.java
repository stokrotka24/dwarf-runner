package lobby;

import com.google.gson.annotations.SerializedName;
import messages.JsonRequired;

public class JoinLobbyResponse {
    @JsonRequired
    @SerializedName("response")
    private boolean response;

    @JsonRequired
    @SerializedName("lon")
    private Double x;

    @JsonRequired
    @SerializedName("lat")
    private Double y;

    public JoinLobbyResponse(boolean response, Double x, Double y) {
        this.response = response;
        this.x = x;
        this.y = y;
    }
}
