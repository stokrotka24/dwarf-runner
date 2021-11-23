package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameType;

public class LobbyListRequest {
    @SerializedName("gametype")
    private String gameMode;

    @SerializedName("map")
    private int mapId;

    @SerializedName("include_full")
    private boolean includeFull;

    @SerializedName("lobby_range_beginning")
    private int rangeBegin;

    @SerializedName("lobby_range_end")
    private int rangeEnd;

    public GameType getGameMode() {
        if (gameMode == null) {
            return null;
        }
        return gameMode.equals("solo") ? GameType.SOLO_GAME : GameType.TEAM_GAME;
    }

    public void setGameMode(GameType type) {
        this.gameMode = type == GameType.SOLO_GAME ? "solo" : "team";
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public boolean isIncludeFull() {
        return includeFull;
    }

    public void setIncludeFull(boolean includeFull) {
        this.includeFull = includeFull;
    }

    public int getRangeBegin() {
        return rangeBegin;
    }

    public void setRangeBegin(int rangeBegin) {
        this.rangeBegin = rangeBegin;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
}
