package game;

import com.google.gson.Gson;
import game.json.PlayerPoints;
import game.json.PositionData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import osm.Coordinates;
import osm.Node;
import utility.GameUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameControllerTest {
    @Test
    void shouldCreateProperDwarfsLocationList() throws ParseException {
        var expectedNumberOfDwarfsLocations = 2;
        int nofPlayers = 2;

        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0, expectedNumberOfDwarfsLocations);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdDwarfsLocationDelivery());
        var content = (JSONObject) response.get("content");
        var dwarfsLocArray = (JSONArray) content.get("dwarfs_list");

        assertEquals(expectedNumberOfDwarfsLocations, dwarfsLocArray.size());
    }

    @Test
    void shouldCreatePositionDataUpdateForSoloGame() throws ParseException {
        int nofPlayers = 3;
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

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
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(1, users.subList(0, sizeTeam1));
        teams.put(2, users.subList(sizeTeam1, 7));
        var lobby = GameUtils.prepareLobbyMock("TEAM", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

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
    void shouldCreatePlayersPointsUpdateForSoloGame() throws ParseException {
        int nofPlayers = 3;
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPlayersPointsUpdate(false));
        var content = (JSONObject) response.get("content");
        var team0 = (JSONArray) content.get("team0");

        System.out.println(response);

        assertEquals(nofPlayers, team0.size());

        Gson gson = new Gson();
        for (int i = 0; i < team0.size(); i++) {
            User user = users.get(i);
            var playerPoints = gson.fromJson(team0.get(i).toString(), PlayerPoints.class);
            assertEquals(user.getUsername(), playerPoints.getUsername());
            assertEquals(0, playerPoints.getPoints());
        }
    }

    @Test
    void shouldCreatePlayersPointsUpdateForTeamGame() throws ParseException {
        int nofPlayers = 7;
        int sizeTeam1 = 4;
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(1, users.subList(0, sizeTeam1));
        teams.put(2, users.subList(sizeTeam1, 7));
        var lobby = GameUtils.prepareLobbyMock("TEAM", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPlayersPointsUpdate(false));
        var content = (JSONObject) response.get("content");
        var team1 = (JSONArray) content.get("team1");
        var team2 = (JSONArray) content.get("team2");

        System.out.println(response);

        assertEquals(sizeTeam1, team1.size());
        assertEquals(nofPlayers - sizeTeam1, team2.size());

        Gson gson = new Gson();
        for (int i = 0; i < team1.size(); i++) {
            User user = users.get(i);
            var playerPoints = gson.fromJson(team1.get(i).toString(), PlayerPoints.class);
            assertEquals(user.getUsername(), playerPoints.getUsername());
            assertEquals(0, playerPoints.getPoints());
        }
        for (int i = 0; i < team2.size(); i++) {
            User user = users.get(sizeTeam1 + i);
            var playerPoints = gson.fromJson(team2.get(i).toString(), PlayerPoints.class);
            assertEquals(user.getUsername(), playerPoints.getUsername());
            assertEquals(0, playerPoints.getPoints());
        }
    }

    @Test
    void shouldCreateProperMsgAfterUnsuccessfulAttemptToDwarfPickUp() throws ParseException {
        var numberOfDwarfsLocations = 2;
        int nofPlayers = 2;
        int expectedStatus = 0;

        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0, numberOfDwarfsLocations);
        var game = GameUtils.prepareGameMock(lobby, users);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users, game);
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

        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0, numberOfDwarfsLocations);
        var game = GameUtils.prepareGameMock(lobby, users);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users, game);
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

    @Test
    void disconnectUserInSoloGame() throws ParseException {
        int nofPlayers = 3;
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        gameController.removePlayer(users.get(nofPlayers - 1).getServerId());

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPositionDataUpdate());
        var content = (JSONObject) response.get("content");
        var team0 = (JSONArray) content.get("team0");

        System.out.println(response);

        assertEquals(nofPlayers - 1, team0.size());

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
    void disconnectUserInTeamGame() throws ParseException {
        int nofPlayers = 7;
        int sizeTeam1 = 4;
        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(1, users.subList(0, sizeTeam1));
        teams.put(2, users.subList(sizeTeam1, 7));
        var lobby = GameUtils.prepareLobbyMock("TEAM", users, teams, 0,2);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        gameController.removePlayer(teams.get(1).get(sizeTeam1 - 1).getServerId());
        gameController.removePlayer(teams.get(2).get(0).getServerId());

        var parser = new JSONParser();
        var response = (JSONObject) parser.parse(gameController.createdPositionDataUpdate());
        var content = (JSONObject) response.get("content");
        var team1 = (JSONArray) content.get("team1");
        var team2 = (JSONArray) content.get("team2");

        System.out.println(response);

        assertEquals(sizeTeam1 - 1, team1.size());
        assertEquals(nofPlayers - sizeTeam1 - 1, team2.size());

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
            User user = users.get(sizeTeam1 + i + 1);
            PositionData positionData = gson.fromJson(team2.get(i).toString(), PositionData.class);
            assertEquals(user.getUsername(), positionData.getUsername());
            assertEquals(playerToNode.get(user.getServerId()).getX(), positionData.getX());
            assertEquals(playerToNode.get(user.getServerId()).getY(), positionData.getY());
        }
    }
}


