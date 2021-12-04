package game;

import lobby.Lobby;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import osm.Node;
import osm.OsmService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameControllerTest {
    @Test
    void shouldCreateProperDwarfsLocationList() throws ParseException {
        var expectedNumberOfDwarfsLocations = 2;
        int nofPlayers = 2;

        var users = prepareUsersMock(nofPlayers);
        var lobby = prepareLobbyMock(users, expectedNumberOfDwarfsLocations);
        var osmService = new OsmService(lobby.getMapId());
        var gameController = prepareGameControllerMock(lobby, osmService, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdDwarfsLocationDelivery());
        var content = (JSONObject) response.get("content");
        var dwarfsLocArray = (JSONArray) content.get("dwarfs_list");

        assertEquals(expectedNumberOfDwarfsLocations, dwarfsLocArray.size());
    }

    @Test
    void runGame() {
    }

    @Test
    void endGame() {
    }

    private GameController prepareGameControllerMock(Lobby lobby, OsmService osmService, List<User> users) {
        var game = prepareGameMock(lobby, osmService, users);
        var playerToUser = users.stream().collect(Collectors.toMap(User::getServerId, item -> item));
        return new GameController(game, playerToUser);
    }

    private AbstractGame prepareGameMock(Lobby lobby, OsmService osmService, List<User> users) {
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

    private List<User> prepareUsersMock(int nofPlayers) {
        var users = new ArrayList<User>();
        for (int i = 1; i <= nofPlayers; i++) {
            var user = new User(i);
            user.setPlatform(GamePlatform.WEB);
            users.add(user);
        }

        return users;
    }

    private Lobby prepareLobbyMock(List<User> users, int dwarfs) {
        var lobbyMock = new Lobby("SOLO", 1, users.size(), 0, (float) 5.0, (float) 5.0, dwarfs);
        lobbyMock.setCreator(users.get(0));
        lobbyMock.setOsmService(new OsmService(lobbyMock.getMapId()));

        for (int i = 0; i < users.size(); i++) {
            lobbyMock.setNodeForPlayer(users.get(i).getServerId(), new Node((long) i, 0.5435 * i, 0.534534 * i));
        }

        return lobbyMock;
    }
}


