package server;


import lobby.Lobby;
import lobby.LobbyManager;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

public class MenuServer {
    //    Demo shows how LobbyManager could be used
    //    assume you extracted stringMsg from Queue
    //
    //    var header = MessageParser.getMsgHeader(stringMsg);
    //    MessageType type = MessageType.fromInt(header.type);
    //    if (type == MessageType.CREATE_LOBBY_REQUEST) {
    //        lobbyManager.createLobby(MessageParser.fromJsonString(stringMsg, Lobby.class),
    //            findPlayerByIdOrSomethingLikeThat(header.senderId));
    //    }
    //    if (type == MessageType.SHOW_LOBBYS_REQUEST) {
    //        lobbyManager.addToSubscribed(findPlayerByIdOrSomethingLikeThat(header.senderId));
    //    }
    public int addInput(ClientHandler handler) {
        return 0;
    }
}
