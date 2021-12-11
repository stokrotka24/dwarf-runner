package game;

import java.util.List;
import java.util.Map;
import osm.Coordinates;
import osm.Node;
import osm.maps.CathedralIsland;
import osm.maps.CentennialHall;
import osm.maps.MainStation;
import osm.maps.OldTown;
import osm.maps.OsmMap;
import osm.maps.PwrArchitectureCampus;
import osm.maps.PwrMainCampus;
import osm.maps.SzczytnickiPark;
import osm.maps.WesternPark;

public abstract class AbstractGame {

    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private double webSpeed;
    private double mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    private Integer timeToEnd;
    private OsmMap map;

    public AbstractGame(int id, GameMap gameMap, List<AbstractPlayer> players, double webSpeed,
        double mobileMaxSpeed, List<Dwarf> dwarfs, Integer timeToEnd) {
        this.id = id;
        this.gameMap = gameMap;
        this.players = players;
        this.webSpeed = webSpeed;
        this.mobileMaxSpeed = mobileMaxSpeed;
        this.dwarfs = dwarfs;
        this.timeToEnd = timeToEnd;
        switch (gameMap) {
            case SZCZYTNICKI_PARK:
                map = SzczytnickiPark.getInstance();
                break;
            case CATHEDRAL_ISLAND:
                map = CathedralIsland.getInstance();
                break;
            case PWR_ARCHITECTURE_CAMPUS:
                map = PwrArchitectureCampus.getInstance();
                break;
            case CENTENNIAL_HALL:
                map = CentennialHall.getInstance();
                break;
            case PWR_MAIN_CAMPUS:
                map = PwrMainCampus.getInstance();
                break;
            case WESTERN_PARK:
                map = WesternPark.getInstance();
                break;
            case MAIN_STATION:
                map = MainStation.getInstance();
                break;
            case OLD_TOWN:
                map = OldTown.getInstance();
                break;
            default:
                map = null;
                break;
        }
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

    public Node getNodeByCoords(Coordinates coords) {
        return map.getNodeByCoords(coords);
    }
}
