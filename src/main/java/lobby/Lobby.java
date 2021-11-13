package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameType;
import game.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    @SerializedName("lobby_id")
    public int id;

    @SerializedName("lobby_name")
    public String name;

    @SerializedName("gametype")
    public GameType type;

    @SerializedName("map")
    public int mapId;

    @SerializedName("curr_players")
    public int players;

    @SerializedName("max_players")
    public int maxPlayers;

    // TODO end - possibly enum?
    @SerializedName("endgame_cond")
    public int end;

    @SerializedName("web_speed")
    public float speed;

    @SerializedName("mobile_max_speed")
    public float maxSpeed;

    @SerializedName("dwarves_amount")
    public int dwarfs;

    public transient Map<Integer, List<User>> teams = new HashMap<>();
}
