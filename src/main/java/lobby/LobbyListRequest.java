package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameType;

public class LobbyListRequest {
    @SerializedName("gametype")
    public GameType gameMode;

    @SerializedName("map")
    public int mapId;

    @SerializedName("include_full")
    public boolean includeFull;

    @SerializedName("lobby_range_beginning")
    public int rangeBegin;

    @SerializedName("lobby_range_end")
    public int rangeEnd;
}
