package game;

import osm.OsmService;

import java.util.List;
import java.util.Map;

public abstract class AbstractGame {

    private int id;
    private GameMap gameMap;
    private OsmService osmService;
    private List<AbstractPlayer> players;
    private double webSpeed;
    private double mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    private Integer timeToEnd;

    public AbstractGame(int id, GameMap gameMap, OsmService osmService, List<AbstractPlayer> players, double webSpeed,
                        double mobileMaxSpeed, List<Dwarf> dwarfs, Integer timeToEnd) {
        this.id = id;
        this.gameMap = gameMap;
        this.osmService = osmService;
        this.players = players;
        this.webSpeed = webSpeed;
        this.mobileMaxSpeed = mobileMaxSpeed;
        this.dwarfs = dwarfs;
        this.timeToEnd = timeToEnd;
    }

    public abstract GameType getType();

    public abstract Map<Integer, List<AbstractPlayer>> getTeams();

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public Integer getTimeToEnd() {
        return timeToEnd;
    }

    public List<Dwarf> getDwarfs() {
        return dwarfs;
    }

    public void setDwarfs(List<Dwarf> dwarfs) {
        this.dwarfs = dwarfs;
    }

    public AbstractPlayer getPlayer(Integer playerId) {
        for (var p: players) {
            if (p.getId() == playerId) {
                return p;
            }
        }
        return null;
    }

    public double getWebSpeed() {
        return webSpeed;
    }

    public double getMobileMaxSpeed() {
        return mobileMaxSpeed;
    }
}
