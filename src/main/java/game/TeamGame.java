
package game;

import osm.OsmService;

import java.util.List;
import java.util.Map;

public class TeamGame extends AbstractGame {
    private Map<Integer, List<AbstractPlayer>> teams;
    public TeamGame(int id, GameMap gameMap, OsmService osmService, List<AbstractPlayer> players, double webSpeed,
                    double mobileMaxSpeed, List<Dwarf> dwarfs, Integer timeToEnd, Map<Integer, List<AbstractPlayer>> teams) {
        super(id, gameMap, osmService, players, webSpeed, mobileMaxSpeed, dwarfs, timeToEnd);
        this.teams = teams;
    }

    @Override
    public GameType getType() {
        return GameType.TEAM_GAME;
    }

    @Override
    public Map<Integer, List<AbstractPlayer>> getTeams() {
        return teams;
    }
}