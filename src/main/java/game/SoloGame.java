package game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoloGame extends AbstractGame {
    public SoloGame(int id, GameMap gameMap, List<AbstractPlayer> players, double webSpeed, double mobileMaxSpeed, List<Dwarf> dwarfs, Integer timeToEnd) {
        super(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs, timeToEnd);
    }

    @Override
    public GameType getType() {
        return GameType.SOLO_GAME;
    }

    @Override
    public Map<Integer, List<AbstractPlayer>> getTeams() {
        Map<Integer, List<AbstractPlayer>> teams = new HashMap<>();
        teams.put(0, getPlayers());
        return teams;
    }
}
