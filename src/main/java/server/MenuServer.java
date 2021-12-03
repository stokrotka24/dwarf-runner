package server;

import dbconn.UserAuthenticator;
import dbconn.jsonclasses.LoginCredentials;
import dbconn.jsonclasses.RegisterCredentials;
import game.GameManager;
import game.User;
import lobby.JoinLobbyRequest;
import lobby.Lobby;
import lobby.LobbyListRequest;
import lobby.LobbyManager;
import messages.Message;
import messages.MessageException;
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
    private GameManager gameManager;
    private HashMap<Integer, User> users;
    private final Logger logger = Logger.getInstance();
    private int currID = 1;

    public void go() {
        initComponents();

        while(true) {
            try {
                String msgReceived = inMsgQueue.take();
                logger.info("Got msg" + msgReceived);

                User sender = null;
                try {
                    int clientID = MessageParser.getClientId(msgReceived);
                    sender = users.get(clientID);
                    if (sender == null) {
                        logger.info("Server didn't recognize user with id: " + clientID);
                        continue;
                    }

                    var header = MessageParser.getMsgHeader(msgReceived);

                    switch(header) {
                        case LOBBY_LIST_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            lobbyManager.sendLobbyList(MessageParser.getMsgContent(msgReceived, LobbyListRequest.class),
                                    sender);
                            break;
                        }
                        case CREATE_LOBBY_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            lobbyManager.createLobby(MessageParser.fromJsonString(msgReceived, Lobby.class),
                                    sender);
                            break;
                        }
                        case JOIN_LOBBY_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            lobbyManager.addPlayerToLobby(MessageParser.getMsgContent(msgReceived, JoinLobbyRequest.class),
                                    sender);
                            break;
                        }
                        case CHANGE_TEAM_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            lobbyManager.changeTeam(sender, MessageParser.getMsgContent(msgReceived, Integer.class));
                            break;
                        }
                        case QUIT_LOBBY_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            lobbyManager.removePlayerFromLobby(sender);
                            break;
                        }
                        case LOG_IN_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            UserAuthenticator.handleLoginRequest(MessageParser.fromJsonString(msgReceived, LoginCredentials.class),
                                    sender);
                            break;
                        }
                        case REGISTER_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            UserAuthenticator.handleRegisterRequest(MessageParser.fromJsonString(msgReceived, 
                                    RegisterCredentials.class), sender);
                            break;
                        }
                        case PLAYER_IS_READY: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            sendServerAcknowledge(sender, MessageType.PLAYER_IS_READY);
                            lobbyManager.setPlayerIsReady(sender);
                            break;
                        }
                        case PLAYER_IS_UNREADY: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            sendServerAcknowledge(sender, MessageType.PLAYER_IS_UNREADY);
                            lobbyManager.setPlayerIsUnready(sender);
                            break;
                        }
                        case START_GAME_REQUEST: {
                            logger.info("Handling:" + header + " for user with id: " + clientID);
                            var lobby = lobbyManager.getLobbyIfReady(sender);
                            if (lobby.isPresent() && sender == lobby.get().getCreator()) {
                                var players = lobbyManager.getPlayerList(lobby.get().getId());
                                gameManager.runGame(lobby.get(), players);
                                lobbyManager.removeLobby(lobby.get().getId());
                            }
                            break;
                        }
                        default: {
                            logger.warning("Handling error for user with id: " + clientID);
                            Message<String> msg = new Message<>(MessageType.ERROR, "Header has been read correctly, " +
                                    "but server doesn't currently support this kind of message. Your message was: " + msgReceived);
                            sender.sendMessage(MessageParser.toJsonString(msg));
                            break;
                        }
                    }
                } catch (MessageException e) {
                    logger.warning(e.getMessage());
                    if (sender != null) {
                        Message<String> msg = new Message<>(MessageType.ERROR, e.getMessage() + " Your message was: " + msgReceived);
                        sender.sendMessage(MessageParser.toJsonString(msg));
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void initComponents() {
        inMsgQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        lobbyManager = new LobbyManager();
        gameManager = new GameManager();
        users = new HashMap<>();
        ClientAccepter clientAccepter = new ClientAccepter(this);
        clientAccepter.start();
        logger.setLoggingLevel(LogLevel.ALL);
        logger.setOption(LoggerOption.LOG_TO_FILE);
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

	private void sendServerAcknowledge(User user, MessageType type) {
		Message<MessageType> acknowledgeMsg = new Message<>(MessageType.ACKNOWLEDGE, type);
		var stringMsg = MessageParser.toJsonString(acknowledgeMsg);

		if (user != null) {
			user.sendMessage(stringMsg);
		}
	}
}
