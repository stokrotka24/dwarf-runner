package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameType;

public class LobbyListRequest {
    @SerializedName("game_mode")
    public GameType gameMode;

    @SerializedName("map_id")
    public int mapId;

    @SerializedName("include_full")
    public boolean includeFull;

    @SerializedName("lobby_range_beginning")
    public int rangeBegin;

    @SerializedName("lobby_range_end")
    public int rangeEnd;
}
