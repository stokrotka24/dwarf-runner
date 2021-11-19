package lobby;

import com.google.gson.Gson;
import game.GameType;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LobbyManagerTest {
    private final User creator = Mockito.mock(User.class);
    private static final ClientMock client = new ClientMock("localhost", 2137);

    @BeforeAll
    static void prepareClient() {
        Thread thread = new Thread(client);
        thread.start();
    }

    @Test
    void createLobby_ShouldSucceed() {
        String request = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\": 1,\n" +
                "    \"content\": {\n" +
                "        \"gametype\": \"solo\",\n" +
                "        \"players_amount\": 6,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": \"no_time\",\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";
        client.sendMsg(request);

        String expected1 = "{\"header\":\"JOIN_LOBBY_RESPONSE\",\"content\":true}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"content\":{\"lobby_id\":0," +
                "\"gametype\":\"solo\",\"map\":1,\"curr_players\":1,\"players_amount\":6,\"endgame_cond\":\"no_time\"," +
                "\"web_speed\":3.0,\"mobile_max_speed\":5.0,\"dwarves_amount\":4,\"ready_players\":0}}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest(name = "{index} => msg={0}")
    @ValueSource(strings = { "{\"header\":\"CREATE_LOBBY_REQUEST\",\"client_id\":1,\"content\":" +
            "{\"gametype\":\"solo\",\"players_amount\":-6,\"map\":1,\"endgame_cond\":\"no_time\", \"web_speed\": 3,\"mobile_max_speed\": 5,\"dwarves_amount\": 4}}",
            "{\"header\":\"CREATE_LOBBY_REQUEST\",\"client_id\":1,\"content\"" +
                    ":{\"gametype\":\"solo\",\"players_amount\":6,\"map\":-1,\"endgame_cond\":\"no_time\", \"web_speed\": 3,\"mobile_max_speed\": 5,\"dwarves_amount\": 4}}",
            "{\"header\":\"CREATE_LOBBY_REQUEST\",\"client_id\":1,\"content\":" +
                    "{\"gametype\":\"solo\",\"players_amount\":6,\"map\":1,\"endgame_cond\":\"no_time\", \"web_speed\": -3,\"mobile_max_speed\": 5,\"dwarves_amount\": 4}}" })
    void createLobby_ShouldFail(String msg) {
        client.sendMsg(msg);

        String expected1 = "{\"header\":\"JOIN_LOBBY_RESPONSE\",\"content\":false}";
        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void addPlayerToLobby_ShouldSucceed() {

    }

    @Test
    void addPlayerToLobby_ShouldFail() {

    }

    @Test
    void changeTeam() {
    }

    @Test
    void removePlayerFromLobby() {
    }

    @Test
    void getLobbyInfo() {
    }

    @Test
    void getPlayerList() {
    }

    @Test
    void removeLobby() {
    }

    @ParameterizedTest(name = "{index} => type={0}, mapId={1}, includeFull={2}, rangeBegin={3}, rangeEnd={4}")
    @CsvSource(value = {
            "; -1; true; 0; 100",
    }, delimiter = ';')
    void sendLobbyList(GameType type, int mapId, boolean includeFull, int rangeBegin, int rangeEnd) {
        LobbyListRequest request = new LobbyListRequest();
        request.setGameMode(type);
        request.setMapId(mapId);
        request.setIncludeFull(includeFull);
        request.setRangeBegin(rangeBegin);
        request.setRangeEnd(rangeEnd);

        Message<LobbyListRequest> msg = new Message<>(MessageType.LOBBY_LIST_REQUEST, request);
        msg.clientId = 1;
        client.sendMsg(new Gson().toJson(msg));

        try {
            String response = client.queue.take();
            LobbyListDelivery delivery = MessageParser.getMsgContent(response, LobbyListDelivery.class);

            if (mapId != -1) {
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
}