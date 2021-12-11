package lobby;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import game.GameType;
import game.User;
import messages.AbstractCommunicationTest;
import messages.Message;
import messages.MessageType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LobbyManagerTest extends AbstractCommunicationTest {
    private final User creator = Mockito.mock(User.class);
    private static final ClientMock client = new ClientMock("localhost", 2137);
    private static final ClientMock client2 = new ClientMock("localhost", 2137);
    private static final ClientMock client3 = new ClientMock("localhost", 2137);

    private static final ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            return field.getName().equals("teams");
        }
    };

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .addDeserializationExclusionStrategy(strategy)
            .addSerializationExclusionStrategy(strategy)
            .create();

    @BeforeAll
    static void prepareClient() {
        Thread thread = new Thread(client);
        thread.start();
        Thread thread2 = new Thread(client2);
        thread2.start();
        Thread thread3 = new Thread(client3);
        thread3.start();

        try {
            client.queue.take();
            client2.queue.take();
            client3.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(1)
    void createTeamLobby_ShouldSucceed() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user1@wp.pl\",\n" +
                "        \"password\": \"user1\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";

        client.sendMsg(request1);

        String request2 = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"gametype\": \"team\",\n" +
                "        \"players_amount\": 2,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": 1,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";

        client.sendMsg(request2);

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user1\",\"failure_reason\":null}}";
        String expected2 = "{\"header\":\"CREATE_LOBBY_RESPONSE\",\"content\":0}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void createSoloLobby_ShouldSucceed() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client2.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user2@wp.pl\",\n" +
                "        \"password\": \"user2\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";

        client.sendMsg(request1);

        String request2 = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\":" + client2.id + ",\n" +
                "    \"content\": {\n" +
                "        \"gametype\": \"solo\",\n" +
                "        \"players_amount\": 3,\n" +
                "        \"map\": 4,\n" +
                "        \"endgame_cond\": 10,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";

        client2.sendMsg(request2);

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user2\",\"failure_reason\":null}}";
        String expected2 = "{\"header\":\"CREATE_LOBBY_RESPONSE\",\"content\":1}";

        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
            String response2 = client2.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest(name = "{index} => type={0}, playersAmount={1}, mapId={2}, end={3}, webSpeed={4}, mobileSpeed={5}, dwarves={6}")
    @CsvSource(value = {
            "solo; -6; 1; 0; 3; 5; 4",
            "solo; -6; 1313; 0; 3; 5; 4",
            "team; 6; -1; 1; 3; 5; 4",
            "team; 6; 1; 1; -3; 5; 4",
            "solo; 6; 1; 0; 3; -5; 4",
            "solo; 6; 1; 1; 3; 5; 0",
    }, delimiter = ';', nullValues = {"null"})
    void createLobby_ShouldFail(String type, Integer playersAmount, Integer mapId, Integer end, Float webSpeed, Float mobileSpeed, Integer dwarves) {
        Lobby lobby = new Lobby(type, mapId, playersAmount, end, webSpeed, mobileSpeed, dwarves);
        Message<Lobby> msg = new Message<>(MessageType.CREATE_LOBBY_REQUEST, lobby);
        msg.clientId = client.id;
        client.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"CREATE_LOBBY_RESPONSE\",\"content\":-1}";
        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void addCreatorToTeamLobby_ShouldSucceed() {
        JoinLobbyRequest request = new JoinLobbyRequest(0, 1, 100.0, 100.0);
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request);
        msg.clientId = client.id;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.sendMsg(gson.toJson(msg));

        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1\u0027s lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":1,\"players_amount\":2,\"endgame_cond\":1," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team1\":[\"user1\"],\"team2\":[]}}}";
        try {
            String response1 = client.queue.take();
            Assertions.assertTrue(response1.contains("\"header\":\"LOBBY_CREATOR_RIGHTS\""));
            Assertions.assertTrue(response1.contains("\"lobby_id\":0"));
            String response2 = client.queue.take();
            Assertions.assertTrue(response2.contains("\"header\":\"JOIN_LOBBY_RESPONSE\""));
            Assertions.assertTrue(response2.contains("\"response\":true"));
            String response3 = client.queue.take();
            assertEquals(expected2, response3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void addCreatorToSoloLobby_ShouldSucceed() {
        JoinLobbyRequest request = new JoinLobbyRequest(1, 0, 100.0, 100.0);
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":1,\"lobby_name\":\"user2's lobby\"," +
                "\"gametype\":\"solo\",\"map\":4,\"curr_players\":1,\"players_amount\":3,\"endgame_cond\":10," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team0\":[\"user2\"]}}}";
        try {
            String response1 = client2.queue.take();
            Assertions.assertTrue(response1.contains("\"header\":\"LOBBY_CREATOR_RIGHTS\""));
            Assertions.assertTrue(response1.contains("\"lobby_id\":1"));
            String response2 = client2.queue.take();
            Assertions.assertTrue(response2.contains("\"header\":\"JOIN_LOBBY_RESPONSE\""));
            Assertions.assertTrue(response2.contains("\"response\":true"));
            String response3 = client2.queue.take();
            assertEquals(expected2, response3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(3)
    void addPlayerToLobby_ShouldSucceed() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client2.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user2@wp.pl\",\n" +
                "        \"password\": \"user2\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        client2.sendMsg(request1);
        JoinLobbyRequest request2 = new JoinLobbyRequest(0, 1, 100.0, 100.0);
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request2);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user2\",\"failure_reason\":null}}";
        String expected3 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":2,\"players_amount\":2,\"endgame_cond\":1," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team1\":[\"user1\",\"user2\"],\"team2\":[]}}}";
        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
            String response2 = client2.queue.take();
            Assertions.assertTrue(response2.contains("\"header\":\"JOIN_LOBBY_RESPONSE\""));
            Assertions.assertTrue(response2.contains("\"response\":true"));
            String response3 = client2.queue.take();
            assertEquals(expected3, response3);
            String response4 = client.queue.take();
            assertEquals(expected3, response4);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void addPlayerToLobby_ShouldFail() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client3.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user3@wp.pl\",\n" +
                "        \"password\": \"user3\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        client3.sendMsg(request1);

        JoinLobbyRequest request2 = new JoinLobbyRequest(0, 0, 100.0, 100.0);
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request2);
        msg.clientId = client3.id;
        client3.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user3\",\"failure_reason\":null}}";
        String expected2 = "{\"header\":\"JOIN_LOBBY_RESPONSE\",\"content\":{\"response\":false,\"lon\":null,\"lat\":null}}";
        try {
            String response1 = client3.queue.take();
            assertEquals(expected1, response1);
            String response2 = client3.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    void changeTeam_ShouldSucceed() {
        Message<Integer> msg = new Message<>(MessageType.CHANGE_TEAM_REQUEST, 2);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"CHANGE_TEAM_RESPONSE\",\"content\":true}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":2,\"players_amount\":2,\"endgame_cond\":1," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team1\":[\"user1\"],\"team2\":[\"user2\"]}}}";
        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
            String response3 = client2.queue.take();
            assertEquals(expected2, response3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    void changeTeam_ShouldFail() {
        Message<Integer> msg = new Message<>(MessageType.CHANGE_TEAM_REQUEST, 0);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"CHANGE_TEAM_RESPONSE\",\"content\":false}";

        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(7)
    void removePlayerFromLobby() {
        Message<Object> msg = new Message<>(MessageType.QUIT_LOBBY_REQUEST, null);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"QUIT_LOBBY_RESPONSE\",\"content\":true}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":1,\"players_amount\":2,\"endgame_cond\":1,\"web_speed\":3.0," +
                "\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0,\"teams\":{\"team1\":[\"user1\"],\"team2\":[]}}}";
        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeLobby() {
        var manager = new LobbyManager();
        var lobby = manager.getLobbyInfo(1357);
        if (lobby != null) {
            manager.removeLobby(1357);
            assertNull(manager.getLobbyInfo(1357));
        }
    }

    @ParameterizedTest(name = "{index} => type={0}, mapId={1}, includeFull={2}, rangeBegin={3}, rangeEnd={4}")
    @CsvSource(value = {
            "null; null; true; 0; 100",
            "null; null; false; 0; 100",
            "null; null; true; 0; 5",
            "null; null; true; 6; 20",
            "null; null; false; 0; 5",
            "null; null; false; 6; 20",
            "null; 7; true; 11; 20",
            "SOLO_GAME; null; true; 0; 100",
            "TEAM_GAME; 3; false; 0; 100",
            "TEAM_GAME; null; true; 5; 15",
            "SOLO_GAME; 2; true; 5; 10",
    }, delimiter = ';', nullValues = {"null"})
    void sendLobbyList(GameType type, Integer mapId, boolean includeFull, int rangeBegin, int rangeEnd) {
        LobbyListRequest request = new LobbyListRequest();
        request.setGameMode(type);
        if (mapId != null) {
            request.setMapId(mapId);
        }
        request.setIncludeFull(includeFull);
        request.setRangeBegin(rangeBegin);
        request.setRangeEnd(rangeEnd);

        Message<LobbyListRequest> msg = new Message<>(MessageType.LOBBY_LIST_REQUEST, request);
        msg.clientId = client.id;
        client.sendMsg(gson.toJson(msg));

        try {
            String response = client.queue.take();
            Message<LobbyListDelivery> deliveryMsg =
                    gson.fromJson(response, TypeToken.getParameterized(Message.class, LobbyListDelivery.class).getType());
            LobbyListDelivery delivery = deliveryMsg.content;
            assertTrue(delivery.getLobbys().size() <= rangeEnd - rangeBegin + 1);
            if (mapId != null) {
                assertFalse(delivery.getLobbys().stream().anyMatch(l -> l.getMapId() != mapId));
            }

            if (!includeFull) {
                assertFalse(delivery.getLobbys().stream().anyMatch(l -> l.getPlayers() == l.getMaxPlayers()));
            }

            if (type != null) {
                assertFalse(delivery.getLobbys().stream().anyMatch(l -> l.getType() != type));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(8)
    void shouldCreateProperResponseForIsReadyRequest() {
        String request = "{\n" +
                "    \"header\": \"PLAYER_IS_READY\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "    }\n" +
                "}";

        client.sendMsg(request);

        String expected = "{\"header\":\"ACKNOWLEDGE\",\"content\":\"PLAYER_IS_READY\"}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\",\"gametype\":\"team\",\"map\":1,\"curr_players\":1,\"players_amount\":2,\"endgame_cond\":1,\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":1,\"teams\":{\"team1\":[\"user1\"],\"team2\":[]}}}";

        try {
            String response = client.queue.take();
            assertEquals(expected, response);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(9)
    void shouldCreateProperResponseForIsUnReadyRequest() {
        String request = "{\n" +
                "    \"header\": \"PLAYER_IS_UNREADY\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "    }\n" +
                "}";

        client.sendMsg(request);

        String expected1 = "{\"header\":\"ACKNOWLEDGE\",\"content\":\"PLAYER_IS_UNREADY\"}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\",\"gametype\":\"team\",\"map\":1,\"curr_players\":1,\"players_amount\":2,\"endgame_cond\":1,\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0,\"teams\":{\"team1\":[\"user1\"],\"team2\":[]}}}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getLobbyIfReady() {
    }
}