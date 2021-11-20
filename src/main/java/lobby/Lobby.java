package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameMap;
import game.GameType;
import game.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    @SerializedName("lobby_id")
    private int id;

    @SerializedName("lobby_name")
    private String name;

    @SerializedName("gametype")
    private String type;

    @SerializedName("map")
    private int mapId;

    @SerializedName("curr_players")
    private int players;

    @SerializedName("players_amount")
    private int maxPlayers;

    @SerializedName("endgame_cond")
    private Integer end;

    @SerializedName("web_speed")
    private float speed;

    @SerializedName("mobile_max_speed")
    private float maxSpeed;

    @SerializedName("dwarves_amount")
    private int dwarfs;

    @SerializedName("ready_players")
    private int readyPlayers;

    private transient Map<Integer, List<User>> teams = new HashMap<>();

    private transient List<Integer> readyPlayersIds = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameType getType() {
        return type.equals("solo") ? GameType.SOLO_GAME : GameType.TEAM_GAME;
    }

    public void setType(GameType type) {
        this.type = type == GameType.SOLO_GAME ? "solo" : "team";
    }

    public GameMap getMap() {
        return GameMap.fromInt(mapId);
    }

    public void setMap(GameMap map) {
        this.mapId = map.ordinal();
    }

    public int getMapId() {
        return mapId;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getDwarfs() {
        return dwarfs;
    }

    public void setDwarfs(int dwarfs) {
        this.dwarfs = dwarfs;
    }

    public Map<Integer, List<User>> getTeams() {
        return teams;
    }

    public void setTeams(Map<Integer, List<User>> teams) {
        this.teams = teams;
    }

    public int getReadyPlayers() {
        return readyPlayers;
    }

    public void setReadyPlayers(int readyPlayers) {
        this.readyPlayers = readyPlayers;
    }

    public void addPlayerToReadyPlayers(Integer id) {
        if (!readyPlayersIds.contains(id)) {
            readyPlayersIds.add(id);
            this.readyPlayers++;
        }
    }

    public void removePlayerFromReadyPlayers(Integer id) {
        if (readyPlayersIds.contains(id)) {
            readyPlayersIds.remove(id);
            this.readyPlayers--;
        }
    }
}
