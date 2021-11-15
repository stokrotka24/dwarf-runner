package server;

import game.AbstractPlayer;
import game.User;
import lobby.Lobby;
import lobby.LobbyListRequest;
import lobby.LobbyManager;
import messages.MessageParser;
import messages.MessageType;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread responsible for creating other threads used for communication with clients, controlling games and controlling lobbies.
 * Thread itself is responsible for parsing requests from clients and passing them to adequate classes that manage them.
 * @author Wojciech Lesniak
 *
 */
public class MenuServer {
	
	/**
	 * Upper limit for messages number on inMsgQueue
	 */
	static final int QUEUE_SIZE = 100000;
	/**
	 * Upper limit for clients count
	 */
	static final int MAX_CLIENTS_COUNT = 100000;
	/**
	 * Queue used for receiving input from clients. All messages from ClientHandlers should go there.
	 */
	public LinkedBlockingQueue<String> inMsgQueue;
	private LobbyManager lobbyManager;
	private HashMap<Integer, User> players;
	private int currID = 1;
	
	public void go() {
		inMsgQueue = new LinkedBlockingQueue<String>(QUEUE_SIZE);
		lobbyManager = new LobbyManager();
		players = new HashMap<Integer, User>();
		ClientAccepter clientAccepter = new ClientAccepter(this);
		clientAccepter.start();
		while(true) {

			try {
				String msgReceived = inMsgQueue.take();
				var header = MessageParser.getMsgHeader(msgReceived);
                int clientID = MessageParser.getClientId(msgReceived);
				if (header == MessageType.LOBBY_LIST_REQUEST) {
					lobbyManager.sendLobbyList(MessageParser.getMsgContent(msgReceived, LobbyListRequest.class),
							players.get(clientID));
				}
			    if (header == MessageType.CREATE_LOBBY_REQUEST) {
			    	lobbyManager.createLobby(MessageParser.fromJsonString(msgReceived, Lobby.class), 
			    			players.get(clientID));
			    }
			} 
			catch (InterruptedException e){
				//TODO: add some handling?
				e.printStackTrace();
			}
		}
	}

    /**
     * Stores handler into map of clients, allowing for further communication with client corresponding to handler
     * @param handler corresponding to client connected to server
     * @return unique ID of client
     */
    public int addInput(ClientHandler handler) {
    	while (players.containsKey(currID)) {
    		currID = currID % MAX_CLIENTS_COUNT + 1;
    	}
    	players.put(currID, new User(handler));
    	int toReturn = currID;
    	currID = currID % MAX_CLIENTS_COUNT + 1;
        return toReturn;
    }
    
    /**
     * deletes entry for client from players map. Should be called after client disconnected
     * @param clientID of client that handler ought to be removed
     */
    public void deleteInput(int clientID) {
    	players.remove(clientID);
    }
}
