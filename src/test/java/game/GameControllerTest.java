package game;

import com.google.gson.Gson;
import game.json.PositionData;
import lobby.Lobby;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import osm.Coordinates;
import osm.Node;
import osm.OsmService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameControllerTest {
    @Test
    void shouldCreateProperDwarfsLocationList() throws ParseException {
        var expectedNumberOfDwarfsLocations = 2;
        int nofPlayers = 2;

        var users = prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = prepareLobbyMock("SOLO", users, teams, 0, expectedNumberOfDwarfsLocations);
        var gameController = prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdDwarfsLocationDelivery());
        var content = (JSONObject) response.get("content");
        var dwarfsLocArray = (JSONArray) content.get("dwarfs_list");

        assertEquals(expectedNumberOfDwarfsLocations, dwarfsLocArray.size());
    }

    @Test
    void shouldCreatePositionDataUpdateForSoloGame() throws ParseException {
        int nofPlayers = 3;
        var users = prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = prepareLobbyMock("SOLO", users, teams, 0,2);
        var gameController = prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPositionDataUpdate());
        var content = (JSONObject) response.get("content");
        var team0 = (JSONArray) content.get("team0");

        System.out.println(response);

        assertEquals(nofPlayers, team0.size());

        Gson gson = new Gson();
        Map<Integer, Node> playerToNode = lobby.getPlayersToInitialNode();
        for (int i = 0; i < team0.size(); i++) {
            User user = users.get(i);
            PositionData positionData = gson.fromJson(team0.get(i).toString(), PositionData.class);
            assertEquals(user.getUsername(), positionData.getUsername());
            assertEquals(playerToNode.get(user.getServerId()).getX(), positionData.getX());
            assertEquals(playerToNode.get(user.getServerId()).getY(), positionData.getY());
        }
    }

    @Test
    void shouldCreatePositionDataUpdateForTeamGame() throws ParseException {
        int nofPlayers = 7;
        int sizeTeam1 = 4;
        var users = prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(1, users.subList(0, sizeTeam1));
        teams.put(2, users.subList(sizeTeam1, 7));
        var lobby = prepareLobbyMock("TEAM", users, teams, 0,2);
        var gameController = prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPositionDataUpdate());
        var content = (JSONObject) response.get("content");
        var team1 = (JSONArray) content.get("team1");
        var team2 = (JSONArray) content.get("team2");

        System.out.println(response);

        assertEquals(sizeTeam1, team1.size());
        assertEquals(nofPlayers - sizeTeam1, team2.size());

        Gson gson = new Gson();
        Map<Integer, Node> playerToNode = lobby.getPlayersToInitialNode();
        for (int i = 0; i < team1.size(); i++) {
            User user = users.get(i);
            PositionData positionData = gson.fromJson(team1.get(i).toString(), PositionData.class);
            assertEquals(user.getUsername(), positionData.getUsername());
            assertEquals(playerToNode.get(user.getServerId()).getX(), positionData.getX());
            assertEquals(playerToNode.get(user.getServerId()).getY(), positionData.getY());
        }
        for (int i = 0; i < team2.size(); i++) {
            User user = users.get(sizeTeam1 + i);
            PositionData positionData = gson.fromJson(team2.get(i).toString(), PositionData.class);
            assertEquals(user.getUsername(), positionData.getUsername());
            assertEquals(playerToNode.get(user.getServerId()).getX(), positionData.getX());
            assertEquals(playerToNode.get(user.getServerId()).getY(), positionData.getY());
        }
    }

    @Test
    void shouldCreateProperMsgAfterUnsuccessfulAttemptToDwarfPickUp() throws ParseException {
        var numberOfDwarfsLocations = 2;
        int nofPlayers = 2;
        int expectedStatus = 0;

        var users = prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = prepareLobbyMock("SOLO", users, teams, 0, numberOfDwarfsLocations);
        var game = prepareGameMock(lobby, users);
        var gameController = prepareGameControllerMock(lobby, users, game);
        var player = game.getPlayer(1);
        var resultMsg = gameController.pickUpDwarf(player, 0);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(resultMsg);
        var content = (JSONObject) response.get("content");
        var status = (Long) content.get("status");
        var points = content.get("points");


        assertEquals(expectedStatus, status);
        assertNull(points);
    }
    @Test
    void shouldCreateProperMsgAfterSuccessfulAttemptToDwarfPickUp() throws ParseException {
        var numberOfDwarfsLocations = 2;
        int nofPlayers = 2;
        int expectedStatus = 1;

        var users = prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = prepareLobbyMock("SOLO", users, teams, 0, numberOfDwarfsLocations);
        var game = prepareGameMock(lobby, users);
        var gameController = prepareGameControllerMock(lobby, users, game);
        var player = game.getPlayer(1);
        var dwarf = game.getDwarfById(0);
        player.setCoords(new Coordinates(dwarf.getX(), dwarf.getY()));
        var resultMsg = gameController.pickUpDwarf(player, 0);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(resultMsg);
        var content = (JSONObject) response.get("content");
        var status = (Long) content.get("status");
        var points = (Long) content.get("points");


        assertEquals(expectedStatus, status);
        assertEquals(dwarf.getPoints(), points.intValue());
    }

    @Test
    void runGame() {
    }

    @Test
    void endGame() {
    }

    private GameController prepareGameControllerMock(Lobby lobby, List<User> users, AbstractGame game) {
        var playerToUser = users.stream().collect(Collectors.toMap(User::getServerId, item -> item));
        return new GameController(game, playerToUser);
    }

    private GameController prepareGameControllerMock(Lobby lobby, List<User> users) {
        var game = prepareGameMock(lobby, users);
        var playerToUser = users.stream().collect(Collectors.toMap(User::getServerId, item -> item));
        return new GameController(game, playerToUser);
    }

    private AbstractGame prepareGameMock(Lobby lobby, List<User> users) {
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
            user.setUsername("User"+i);
            users.add(user);
        }

        return users;
    }

    private Lobby prepareLobbyMock(String type, List<User> users, Map<Integer, List<User>> teams, int end, int dwarfs) {
        var lobbyMock = new Lobby(type, 1, users.size(), end, 5.0, 5.0, dwarfs);
        lobbyMock.setCreator(users.get(0));
        lobbyMock.setTeams(teams);
        lobbyMock.setOsmService(new OsmService(lobbyMock.getMapId()));

        for (int i = 0; i < users.size(); i++) {
            lobbyMock.setNodeForPlayer(users.get(i).getServerId(), new Node((long) i, 0.5435 * i, 0.534534 * i));
        }

        return lobbyMock;
    }
}


