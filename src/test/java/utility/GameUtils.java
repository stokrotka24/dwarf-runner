package utility;

import game.*;
import lobby.Lobby;
import osm.Node;
import osm.OsmService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameUtils {

    public static Lobby prepareLobbyMock(String type, List<User> users, Map<Integer, List<User>> teams, int end, int dwarfs) {
        var lobbyMock = new Lobby(type, 1, users.size(), end, 5.0, 5.0, dwarfs);
        lobbyMock.setCreator(users.get(0));
        lobbyMock.setTeams(teams);
        lobbyMock.setOsmService(new OsmService(lobbyMock.getMapId()));

        for (int i = 0; i < users.size(); i++) {
            lobbyMock.setNodeForPlayer(users.get(i).getServerId(), new Node((long) i, 0.5435 * i, 0.534534 * i));
        }

        return lobbyMock;
    }

    public static List<User> prepareUsersMock(int nofPlayers) {
        var users = new ArrayList<User>();
        for (int i = 1; i <= nofPlayers; i++) {
            var user = new User(i);
            user.setPlatform(GamePlatform.WEB);
            user.setUsername("User"+i);
            users.add(user);
        }

        return users;
    }

    public static GameController prepareGameControllerMock(Lobby lobby, List<User> users, AbstractGame game) {
        var playerToUser = users.stream().collect(Collectors.toMap(User::getServerId, item -> item));
        return new GameController(game, playerToUser);
    }

    public static GameController prepareGameControllerMock(Lobby lobby, List<User> users) {
        var game = prepareGameMock(lobby, users);
        var playerToUser = users.stream().collect(Collectors.toMap(User::getServerId, item -> item));
        return new GameController(game, playerToUser);
    }

    public static AbstractGame prepareGameMock(Lobby lobby, List<User> users) {
        return GameBuilder.aGame()
                .withId(1)
                .withGameMap(lobby.getMap())
                .withOsmService(lobby.getOsmService())
                .withPlayers(users, lobby.getPlayersToInitialNode())
                .withDwarfs(lobby.getDwarfs(), lobby.getOsmService())
                .withMobileMaxSpeed(lobby.getMaxSpeed())
                .withWebSpeed(lobby.getSpeed())
                .withTeams(lobby.getTeams(), lobby.getPlayersToInitialNode())
                .withGameType(lobby.getType())
                .withEndCondition(lobby.getEnd())
                .build();
    }
}
