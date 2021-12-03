package lobby;

import com.google.gson.annotations.SerializedName;
import messages.JsonRequired;

public class JoinLobbyRequest {
    @JsonRequired
    @SerializedName("lobby_id")
    private int lobbyId;

    @SerializedName("team_id")
    private int team;

    @JsonRequired
    @SerializedName("lon")
    private Double x;

    @JsonRequired
    @SerializedName("lat")
    private Double y;

    public JoinLobbyRequest(int lobbyId, int team, Double x, Double y) {
        this.lobbyId = lobbyId;
        this.team = team;
        this.x = x;
        this.y = y;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
