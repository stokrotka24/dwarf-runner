package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameType;
import game.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    public int id;
    public String name;

    @SerializedName("map_id")
    public int mapId;

    @SerializedName("curr_players")
    public int players;

    @SerializedName("game_mode")
    public GameType type;

    @SerializedName("max_amount")
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
