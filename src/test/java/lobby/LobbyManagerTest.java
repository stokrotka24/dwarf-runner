package lobby;

import game.GameMap;
import game.GameType;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.*;

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
        Lobby lobby = new Lobby();
        lobby.type = GameType.SOLO_GAME;
        lobby.maxPlayers = 16;
        lobby.mapId = GameMap.MAIN_STATION.ordinal();
        lobby.end = 5;
        lobby.speed = 5.0f;
        lobby.maxSpeed = 6.0f;
        lobby.dwarfs = 20;
        Message<Lobby> msg = new Message<>(MessageType.CREATE_LOBBY_REQUEST, lobby);
        msg.clientId = 1;

        client.sendMsg(MessageParser.toJsonString(msg));

        String expected1 = "{\"header\":\"JOIN_LOBBY_RESPONSE\",\"client_id\":0,\"content\":true}";
        String expected2 = "{\"header\":\"LOBBY_STATUS_UPDATE\",\"client_id\":0,\"content\":{\"lobby_id\":0," +
                "\"gametype\":\"SOLO_GAME\",\"map\":2,\"curr_players\":1,\"max_players\":16," +
                "\"endgame_cond\":5,\"web_speed\":5.0,\"mobile_max_speed\":6.0,\"dwarves_amount\":20}}";

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
    void createLobby_ShouldFail() {
        Lobby lobby = new Lobby();
        lobby.type = GameType.SOLO_GAME;
        lobby.maxPlayers = -2;
        lobby.mapId = 333;
        lobby.end = 5;
        lobby.speed = 5.0f;
        lobby.maxSpeed = 6.0f;
        lobby.dwarfs = 20;
        Message<Lobby> msg = new Message<>(MessageType.CREATE_LOBBY_REQUEST, lobby);
        msg.clientId = 1;

        client.sendMsg(MessageParser.toJsonString(msg));

        String expected1 = "{\"header\":\"JOIN_LOBBY_RESPONSE\",\"client_id\":0,\"content\":false}";

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
    void createGame() {
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
        request.gameMode = type;
        request.mapId = mapId;
        request.includeFull = includeFull;
        request.rangeBegin = rangeBegin;
        request.rangeEnd = rangeEnd;

        Message<LobbyListRequest> msg = new Message<>(MessageType.LOBBY_LIST_REQUEST, request);
        msg.clientId = 1;
        client.sendMsg(MessageParser.toJsonString(msg));

        try {
            String response = client.queue.take();
            LobbyListDelivery delivery = MessageParser.getMsgContent(response, LobbyListDelivery.class);
            for (GameMap m : GameMap.values()) {
                if (m.ordinal() != mapId) {

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}