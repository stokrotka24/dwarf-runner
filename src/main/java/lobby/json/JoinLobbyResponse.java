package lobby.json;

import com.google.gson.annotations.SerializedName;

public class JoinLobbyResponse {
    @SerializedName("response")
    private boolean response;

    @SerializedName("lon")
    private Double x;

    @SerializedName("lat")
    private Double y;

    public JoinLobbyResponse(boolean response, Double x, Double y) {
        this.response = response;
        this.x = x;
        this.y = y;
    }

    public boolean getResponse() {
        return response;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
