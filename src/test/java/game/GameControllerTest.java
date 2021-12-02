package game;

import lobby.Lobby;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import osm.OsmService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    @Test
    void shouldCreateProperDwarfsLocationList() throws ParseException {
        var expectedNumberOfDwarfsLocations = 2;
        int nofPlayers = 2;

        var users = prepareUsersMock(nofPlayers);
        var lobby = prepareSLobbyMock(users, expectedNumberOfDwarfsLocations);
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
        return new GameController(game, osmService, playerToUser);
    }

    private AbstractGame prepareGameMock(Lobby lobby, OsmService osmService, List<User> users) {
        return GameBuilder.aGame()
                .withId(1)
                .withGameMap(lobby.getMap())
                .withPlayers(users)
                .withDwarfs(osmService.getUniqueRandomNodes(lobby.getDwarfs()))
                .withMobileMaxSpeed(lobby.getMaxSpeed())
                .withWebSpeed(lobby.getSpeed())
                .withTeams(lobby.getTeams())
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

    private Lobby prepareSLobbyMock(List<User> users, int dwarfs) {
        var lobbyMock = new Lobby("SOLO", 1, users.size(), 0, (float) 5.0, (float) 5.0, dwarfs);
        lobbyMock.setCreator(users.get(0));

        return lobbyMock;
    }
}


