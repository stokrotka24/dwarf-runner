package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GameBuilder {
    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private float webSpeed;
    private float mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    private GameType gameType;
    private Map<Integer, List<User>> teams;

    private GameBuilder() {}

    public static GameBuilder aGame() {
        return new GameBuilder();
    }

    public GameBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public GameBuilder withGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        return this;
    }

    public GameBuilder withPlayers(List<AbstractPlayer> players) {
        this.players = players;
        return this;
    }

    public GameBuilder withWebSpeed(float webSpeed) {
        this.webSpeed = webSpeed;
        return this;
    }

    public GameBuilder withMobileMaxSpeed(float mobileMaxSpeed) {
        this.mobileMaxSpeed = mobileMaxSpeed;
        return this;
    }

    public GameBuilder withDwarfs(int nofDwarfs) {
        //TODO generate random list of dwarfs using OsmService
        this.dwarfs = new ArrayList<>();
        return this;
    }

    public GameBuilder withGameType(GameType gameType) {
        this.gameType = gameType;
        return this;
    }

    public GameBuilder withTeams(Map<Integer, List<User>> teams) {
        this.teams = teams;
        return this;
    }

    public AbstractGame build() {
        AbstractGame game = null;

        if (gameType.equals(GameType.SOLO_GAME)) {
            game = new SoloGame(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs);
        } else if (gameType.equals(GameType.TEAM_GAME)) {
            game = new TeamGame(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs, teams);
        }

        return game;
    }
}
