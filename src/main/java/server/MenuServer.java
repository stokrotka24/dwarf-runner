package server;

import game.GameManager;
import game.User;
import lobby.Lobby;
import lobby.LobbyListRequest;
import lobby.LobbyManager;
import messages.MessageParser;

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
	private GameManager gameManager;
	private HashMap<Integer, User> users;
	private int currID = 1;
	
	public void go() {
		inMsgQueue = new LinkedBlockingQueue<String>(QUEUE_SIZE);
		lobbyManager = new LobbyManager();
		gameManager = new GameManager();
		users = new HashMap<Integer, User>();
		ClientAccepter clientAccepter = new ClientAccepter(this);
		clientAccepter.start();
		while(true) {

			try {
				String msgReceived = inMsgQueue.take();
				var header = MessageParser.getMsgHeader(msgReceived);
                int clientID = MessageParser.getClientId(msgReceived);
				switch(header) {
					case LOBBY_LIST_REQUEST: {
						lobbyManager.sendLobbyList(MessageParser.getMsgContent(msgReceived, LobbyListRequest.class),
								users.get(clientID));
						break;
					}
					case CREATE_LOBBY_REQUEST: {
						lobbyManager.createLobby(MessageParser.fromJsonString(msgReceived, Lobby.class),
								users.get(clientID));
						break;
					}
					case PLAYER_IS_READY: {
						lobbyManager.setPlayerIsReady(users.get(clientID));
						break;
					}
					case PLAYER_IS_UNREADY: {
						lobbyManager.setPlayerIsUnready(users.get(clientID));
						break;
					}
					case START_GAME_REQUEST: {
						var lobby = lobbyManager.getLobbyIfReady(users.get(clientID));
						if (lobby.isPresent()) {
							var players = lobbyManager.getPlayerList(lobby.get().getId());
							gameManager.runGame(lobby.get(), players);
						}
						break;
					}
					default: {
						//TODO: Error handling
						break;
					}
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
    	while (users.containsKey(currID)) {
    		currID = currID % MAX_CLIENTS_COUNT + 1;
    	}
    	users.put(currID, new User(currID, handler));
    	int toReturn = currID;
    	currID = currID % MAX_CLIENTS_COUNT + 1;
        return toReturn;
    }
    
    /**
     * deletes entry for client from players map. Should be called after client disconnected
     * @param clientID of client that handler ought to be removed
     */
    public void deleteInput(int clientID) {
    	users.remove(clientID);
    }
}
