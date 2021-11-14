package game;

import java.util.List;

public abstract class AbstractGame {
    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private float webSpeed;
    private float mobileMaxSpeed;
    private List<Dwarf> dwarfs;

    public AbstractGame(int id, GameMap gameMap, List<AbstractPlayer> players, float webSpeed, float mobileMaxSpeed, List<Dwarf> dwarfs) {
        this.id = id;
        this.gameMap = gameMap;
        this.players = players;
        this.webSpeed = webSpeed;
        this.mobileMaxSpeed = mobileMaxSpeed;
        this.dwarfs = dwarfs;
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }
}
