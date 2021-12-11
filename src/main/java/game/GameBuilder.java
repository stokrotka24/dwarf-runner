package game;

import osm.Node;
import osm.OsmService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GameBuilder {
    private int id;
    private GameMap gameMap;
    private OsmService osmService;
    private List<AbstractPlayer> players;
    private float webSpeed;
    private float mobileMaxSpeed;
    private List<Dwarf> dwarfs;
    private GameType gameType;
    private Integer timeToEnd;
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

    public GameBuilder withOsmService(OsmService osmService) {
        this.osmService = osmService;
        return this;
    }

    public GameBuilder withPlayers(List<User> users, Map<Integer, Node> playerToInitialNode) {
        this.players = users.stream()
                .map(u -> mapUserToPlayer(u, playerToInitialNode.get(u.getServerId())))
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

    public GameBuilder withDwarfs(int numDwarfs, OsmService osmService) {
        List<Node> nodes = osmService.getUniqueRandomNodes(numDwarfs);
        this.dwarfs = nodes.stream().map(Dwarf::new).collect(Collectors.toList());
        return this;
    }

    public GameBuilder withGameType(GameType gameType) {
        this.gameType = gameType;
        return this;
    }

    public GameBuilder withEndCondition(Integer end) {
        this.timeToEnd = end;
        return this;
    }

    public GameBuilder withTeams(Map<Integer, List<User>> teams, Map<Integer, Node> playerToInitialNode) {
        Map<Integer, List<AbstractPlayer>> mappedTeams = new HashMap<>();
        for (var teamEntry : teams.entrySet()) {
            mappedTeams.put(teamEntry.getKey(), teamEntry.getValue().stream()
                            .map(u -> mapUserToPlayer(u, playerToInitialNode.get(u.getServerId())))
                            .collect(Collectors.toList()));
        }
        this.teams = mappedTeams;
        return this;
    }

    public AbstractGame build() {
        AbstractGame game = null;

        if (gameType.equals(GameType.SOLO_GAME)) {
            game = new SoloGame(id, gameMap, osmService, players, webSpeed, mobileMaxSpeed, dwarfs, timeToEnd);
        } else if (gameType.equals(GameType.TEAM_GAME)) {
            game = new TeamGame(id, gameMap, osmService, players, webSpeed, mobileMaxSpeed, dwarfs, timeToEnd, teams);
        }

        return game;
    }

    private AbstractPlayer mapUserToPlayer(User user, Node node) {
        AbstractPlayer player = null;
        Optional<GamePlatform> gamePlatform = user.getPlatform();

        if (gamePlatform.isPresent()) {
            if (gamePlatform.get().equals(GamePlatform.MOBILE)) {
                player = new MobilePlayer(user.getServerId(), node);
            } else if (gamePlatform.get().equals(GamePlatform.WEB)) {
                player = new WebPlayer(user.getServerId(), node);
            }
        }

        return player;
    }
}
