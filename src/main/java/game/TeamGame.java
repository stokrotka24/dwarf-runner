
package game;

import java.util.List;
import java.util.Map;

public class TeamGame extends AbstractGame {
    private Map<Integer, List<AbstractPlayer>> teams;

    public TeamGame(int id, GameMap gameMap, List<AbstractPlayer> players, float webSpeed,
                    float mobileMaxSpeed, List<Dwarf> dwarfs, Map<Integer, List<AbstractPlayer>> teams) {
        super(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs);
        this.teams = teams;
    }
}