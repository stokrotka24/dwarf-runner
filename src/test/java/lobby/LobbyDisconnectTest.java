package lobby;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.GamePlatform;
import game.User;
import lobby.json.JoinLobbyRequest;
import messages.AbstractCommunicationTest;
import messages.Message;
import messages.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyDisconnectTest extends AbstractCommunicationTest {
    private static final ClientMock client1 = new ClientMock("localhost", defaultPort);
    private static final ClientMock client2 = new ClientMock("localhost", defaultPort);

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
        new Thread(client1).start();
        new Thread(client2).start();
        try {
            client1.queue.take();
            client2.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addPlayersToLobby();
    }

    private static void addPlayersToLobby() {
        createLobby();
        addFirstPlayer();
        addSecondPlayer();
    }

    private static void addFirstPlayer() {
        JoinLobbyRequest request = new JoinLobbyRequest(lobbyCounter, 1, 100.0, 100.0);
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request);
        msg.clientId = client1.id;
        client1.sendMsg(gson.toJson(msg));

        String expected4 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1\u0027s lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":1,\"players_amount\":2,\"endgame_cond\":1," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team1\":[\"user1\"],\"team2\":[]}}}";
        try {
            String response1 = client1.queue.take();
            Assertions.assertTrue(response1.contains("\"header\":\"LOBBY_CREATOR_RIGHTS\""));
            Assertions.assertTrue(response1.contains("\"lobby_id\":0"));
            String response2 = client1.queue.take();
            Assertions.assertTrue(response2.contains("\"header\":\"JOIN_LOBBY_RESPONSE\""));
            Assertions.assertTrue(response2.contains("\"response\":true"));
            String response3 = client1.queue.take();
            Assertions.assertTrue(response3.contains("\"header\":\"MAP_BOUNDS\""));
            String response4 = client1.queue.take();
            assertEquals(expected4, response4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addSecondPlayer() {
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
        JoinLobbyRequest request2 = new JoinLobbyRequest(lobbyCounter, 1, 100.0, 100.0);
        lobbyCounter++;
        Message<JoinLobbyRequest> msg = new Message<>(MessageType.JOIN_LOBBY_REQUEST, request2);
        msg.clientId = client2.id;
        client2.sendMsg(gson.toJson(msg));

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user2\",\"failure_reason\":null}}";
        String expected4 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0,\"lobby_name\":\"user1's lobby\"," +
                "\"gametype\":\"team\",\"map\":1,\"curr_players\":2,\"players_amount\":2,\"endgame_cond\":1," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0," +
                "\"teams\":{\"team1\":[\"user1\"],\"team2\":[\"user2\"]}}}";
        try {
            String response1 = client2.queue.take();
            assertEquals(expected1, response1);
            String response2 = client2.queue.take();
            Assertions.assertTrue(response2.contains("\"header\":\"JOIN_LOBBY_RESPONSE\""));
            Assertions.assertTrue(response2.contains("\"response\":true"));
            String response3 = client2.queue.take();
            Assertions.assertTrue(response3.contains("\"header\":\"MAP_BOUNDS\""));
            String response4 = client2.queue.take();
            assertEquals(expected4, response4);
            String response5 = client1.queue.take();
            assertEquals(expected4, response5);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createLobby() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client1.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user1@wp.pl\",\n" +
                "        \"password\": \"user1\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";

        client1.sendMsg(request1);

        String request2 = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\":" + client1.id + ",\n" +
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

        client1.sendMsg(request2);

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user1\",\"failure_reason\":null}}";
        String expected2 = "{\"header\":\"CREATE_LOBBY_RESPONSE\",\"content\":0}";

        try {
            String response1 = client1.queue.take();
            assertEquals(expected1, response1);
            String response2 = client1.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeFromLobby_Messaging() {
        client2.close();
        try {
            String response = client1.queue.take();
            assertTrue(response.contains("\"header\":\"LOBBY_STATUS_UPDATE\""));
            assertTrue(response.contains("\"curr_players\":1"));
            assertTrue(response.contains("\"team1\":[\"user1\"]"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeFromLobby_CleanUp() {
        LobbyManager manager = new LobbyManager();
        var user = new User("User1");
        user.setPlatform(GamePlatform.WEB);

        Message<Lobby> msg = new Message<>(MessageType.CREATE_LOBBY_REQUEST,
                new Lobby("SOLO", 1, 3, 0, 5.0, 5.0, 2));
        manager.createLobby(msg, user);
        // So that test passes regardless of whether server runs in the background or not
        var lobby = manager.getLobbyInfo(lobbyCounter);
        if (lobby == null) {
            lobby = manager.getLobbyInfo(lobbyCounter - 1);
        } else {
            lobbyCounter++;
        }
        manager.addPlayerToLobby(new JoinLobbyRequest(lobby.getId(), 0, 0.0, 0.0), user);

        assertTrue(lobby.getTeams().get(0).contains(user));
        manager.disconnectUser(user);
        assertFalse(lobby.getTeams().get(0).contains(user));
    }
}
