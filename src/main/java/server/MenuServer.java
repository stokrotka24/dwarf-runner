package server;

import dbconn.UserAuthenticator;
import dbconn.jsonclasses.ChangePasswordRequest;
import dbconn.jsonclasses.ChangeUsernameRequest;
import dbconn.jsonclasses.LoginCredentials;
import dbconn.jsonclasses.RegisterCredentials;
import game.*;
import game.json.MobileMove;
import lobby.json.JoinLobbyRequest;
import lobby.Lobby;
import lobby.json.LobbyListRequest;
import lobby.LobbyManager;
import messages.Message;
import messages.MessageException;
import messages.MessageParser;
import messages.MessageType;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        initTickerService();
        
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
                    logger.info("Handling:" + header + " for user with id: " + clientID);

                    switch(header) {
                        case LOBBY_LIST_REQUEST: {
                            lobbyManager.sendLobbyList(MessageParser.getMsgContent(msgReceived, LobbyListRequest.class),
                                    sender);
                            break;
                        }
                        case CREATE_LOBBY_REQUEST: {
                            lobbyManager.createLobby(MessageParser.fromJsonString(msgReceived, Lobby.class),
                                    sender);
                            break;
                        }
                        case JOIN_LOBBY_REQUEST: {
                            lobbyManager.addPlayerToLobby(MessageParser.getMsgContent(msgReceived, JoinLobbyRequest.class),
                                    sender);
                            break;
                        }
                        case CHANGE_TEAM_REQUEST: {
                            lobbyManager.changeTeam(sender, MessageParser.getMsgContent(msgReceived, Integer.class));
                            break;
                        }
                        case QUIT_LOBBY_REQUEST: {
                            lobbyManager.removePlayerFromLobby(sender, true);
                            break;
                        }
                        case LOG_IN_REQUEST: {
                            UserAuthenticator.handleLoginRequest(MessageParser.fromJsonString(msgReceived, LoginCredentials.class),
                                    sender);
                            break;
                        }
                        case REGISTER_REQUEST: {
                            UserAuthenticator.handleRegisterRequest(MessageParser.fromJsonString(msgReceived,
                                    RegisterCredentials.class), sender);
                            break;
                        }
                        case PLAYER_IS_READY: {
                            sendServerAcknowledge(sender, MessageType.PLAYER_IS_READY);
                            lobbyManager.setPlayerIsReady(sender);
                            break;
                        }
                        case PLAYER_IS_UNREADY: {
                            sendServerAcknowledge(sender, MessageType.PLAYER_IS_UNREADY);
                            lobbyManager.setPlayerIsUnready(sender);
                            break;
                        }
                        case START_GAME_REQUEST: {
                            var lobby = lobbyManager.getLobbyIfReady(sender);
                            if (lobby.isPresent()) {
                                var players = lobbyManager.getPlayerList(lobby.get().getId());
                                gameManager.runGame(lobby.get(), players);
                                lobbyManager.removeLobby(lobby.get().getId(), false);
                            }
                            break;
                        }
                        case WEB_MOVE: {
                            sendServerAcknowledge(sender, MessageType.WEB_MOVE);
                            var move = new Move(MessageParser.getMsgContent(msgReceived, WebMove.class));
                            var gameController = gameManager.userToGameController.get(sender.getServerId());
                            if (gameController != null) {
                                gameController.performMove(sender.getServerId(), move);
                            }
                            break;
                        }
                        case MOBILE_MOVE: {
                            var move = new Move(MessageParser.getMsgContent(msgReceived, MobileMove.class));
                            var gameController = gameManager.userToGameController.get(sender.getServerId());
                            if (gameController != null) {
                                gameController.performMove(sender.getServerId(), move);
                            }
                            break;
                        }
                        case PICK_DWARF_REQUEST: {
                            var gameController = gameManager.userToGameController.get(sender.getServerId());
                            if (gameController != null) {
                                 gameController.performDwarfPickUp(sender.getServerId(), MessageParser.getMsgContent(msgReceived, Integer.class));
                            }
                            break;
                        }
                        case LEAVE_GAME: {
                            var gameController = gameManager.userToGameController.get(sender.getServerId());
                            if (gameController != null) {
                                gameController.removePlayer(sender.getServerId());
                                gameManager.userToGameController.remove(sender.getServerId());
                            }
                            sendServerAcknowledge(sender, MessageType.LEAVE_GAME);
                            break;
                        }
                        case CHANGE_PASSWORD_REQUEST: {
                            UserAuthenticator.handleChangePasswordRequest(MessageParser.fromJsonString(msgReceived, 
                                    ChangePasswordRequest.class), sender);
                            break;
                        }
                        case CHANGE_USERNAME_REQUEST: {
                            UserAuthenticator.handleChangeUsernameRequest(MessageParser.fromJsonString(msgReceived, 
                                    ChangeUsernameRequest.class), sender);
                            break;
                        }
                        case DISCONNECT: {
                            disconnectUser(sender);
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

    private void initTickerService() {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.scheduleAtFixedRate(() -> logger.info(getStatString()), 0, 10, TimeUnit.MINUTES);
    }

    private String getStatString() {
        return "\n\tConnected players: " + users.size() +
                "\n\tCurrent lobbys: " + lobbyManager.getNumberOfLobbys() +
                "\n\tActive Games: " + gameManager.getNumberOfGames();
    }

    private void disconnectUser(User sender) {
        logger.info(sender.getUsername() + " disconnect");
        users.remove(sender.getServerId());
        lobbyManager.disconnectUser(sender);
        gameManager.disconnectUser(sender);
        // TODO - log out
        // UserAuthenticator.handleLogOutRequest(sender); ?
    }

    private void initComponents() {
        inMsgQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        lobbyManager = new LobbyManager();
        gameManager = new GameManager();
        users = new HashMap<>();
        new ClientAccepter(this).start();
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
