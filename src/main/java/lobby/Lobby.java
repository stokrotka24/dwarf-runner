package lobby;

import com.google.gson.annotations.SerializedName;
import game.GameMap;
import game.GameType;
import game.User;
import messages.JsonRequired;
import osm.Coordinates;
import osm.OsmService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    @SerializedName("lobby_id")
    private int id;

    @SerializedName("lobby_name")
    private String name;

    @JsonRequired
    @SerializedName("gametype")
    private String type;

    @JsonRequired
    @SerializedName("map")
    private int mapId;

    @SerializedName("curr_players")
    private int players;

    @JsonRequired
    @SerializedName("players_amount")
    private int maxPlayers;

    @JsonRequired
    @SerializedName("endgame_cond")
    private Integer end;

    @JsonRequired
    @SerializedName("web_speed")
    private float speed;

    @JsonRequired
    @SerializedName("mobile_max_speed")
    private float maxSpeed;

    @JsonRequired
    @SerializedName("dwarves_amount")
    private int dwarfs;

    @SerializedName("ready_players")
    private int readyPlayers;

    private Map<Integer, List<User>> teams = new HashMap<>();

    private transient List<Integer> readyPlayersIds = new ArrayList<>();

    private transient User creator;

    private transient Map<Integer, Coordinates> playerToInitialCoords = new HashMap<>();

    private transient OsmService osmService;

    public Lobby() {}

    public Lobby(String type, Integer mapId, Integer maxPlayers, Integer end,
                 Float speed, Float maxSpeed, Integer dwarfs) {
        this.type = type;
        this.mapId = mapId;
        this.maxPlayers = maxPlayers;
        this.end = end;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.dwarfs = dwarfs;
    }

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

    public void removePlayerFromTeam(User player) {
        for (var list : teams.values()) {
            if (list.contains(player)) {
                list.remove(player);
                return;
            }
        }
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public OsmService getOsmService() {
        return osmService;
    }

    public void setOsmService(OsmService osmService) {
        this.osmService = osmService;
    }

    public Coordinates getCoordsForPlayer(int playerId) {
        return playerToInitialCoords.get(playerId);
    }
}
