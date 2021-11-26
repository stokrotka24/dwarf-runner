package lobby;

import com.google.gson.annotations.SerializedName;
import messages.JsonRequired;

public class JoinLobbyRequest {
    @JsonRequired
    @SerializedName("lobby_id")
    private int lobbyId;

    @SerializedName("team_id")
    private int team;

    public JoinLobbyRequest(int lobbyId, int team) {
        this.lobbyId = lobbyId;
        this.team = team;
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
}
