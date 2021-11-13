package game;

import java.util.*;
import java.util.stream.Collectors;

public final class GameBuilder {
    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private float webSpeed;
    private float mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    private GameType gameType;
    private Map<Integer, List<AbstractPlayer>> teams;

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

    public GameBuilder withPlayers(List<User> users) {
        this.players = users.stream()
                .map(this::mapUserToPlayer)
                .collect(Collectors.toList());
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
        Map<Integer, List<AbstractPlayer>> mappedTeams = new HashMap<>();
        for (var teamEntry : teams.entrySet()) {
            mappedTeams.put(teamEntry.getKey(), teamEntry.getValue().stream()
                            .map(this::mapUserToPlayer)
                            .collect(Collectors.toList()));
        }
        this.teams = mappedTeams;
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

    private AbstractPlayer mapUserToPlayer(User user) {
        AbstractPlayer player = null;
        Optional<GamePlatform> gamePlatform = user.getPlatform();

        if (gamePlatform.isPresent()) {
            //TODO add id from user
            if (gamePlatform.get().equals(GamePlatform.MOBILE)) {
                player = new MobilePlayer(0, user.getHandler());
            } else if (gamePlatform.get().equals(GamePlatform.WEB)) {
                player = new WebPlayer(0, user.getHandler());
            }
        }

        return player;
    }
}
