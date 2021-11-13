package lobby;

import game.AbstractPlayer;
import game.GameMap;
import game.GameType;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {
    private final AbstractPlayer creator = Mockito.mock(AbstractPlayer.class);
    private static final ClientMock client = new ClientMock("localhost", 2137);

    @BeforeAll
    static void prepareClient() {
        Thread thread = new Thread(client);
        thread.start();
    }

    @Test
    void createLobby() {
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

        System.out.println(MessageParser.toJsonString(msg));
        client.sendMsg(MessageParser.toJsonString(msg));

        //lobbyManager.createLobby(msg, creator);

        //assertSame(lobby, lobbyManager.getLobbyInfo(1));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addToSubscribed() {
    }

    @Test
    void removeFromSubscribed() {
    }

    @Test
    void addPlayerToLobby() {
    }

    @Test
    void testAddPlayerToLobby() {
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
}