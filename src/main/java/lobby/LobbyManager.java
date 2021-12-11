package lobby;

import game.GameMap;
import game.GamePlatform;
import game.GameType;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import server.Logger;
import osm.Coordinates;
import osm.OsmService;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class LobbyManager {
    private final List<Lobby> lobbys;
    private final Map<Integer, List<User>> lobbyToPlayers;
    private static final Logger logger = Logger.getInstance();
    private final ReentrantLock mutex = new ReentrantLock();
    private static int idCounter = 0;

    public LobbyManager() {
        lobbys = new ArrayList<>();
        lobbyToPlayers = new HashMap<>();
        // TODO rm for release
        generateDummyLobbys(20);
    }

    /**
     * Creates new Lobby, adds its creator to it
     * and notify subscribers
     * @param msg msg with required data
     * @param creator Player who creates lobby
     */
    public void createLobby(Message<Lobby> msg, User creator) {
        Lobby lobby = msg.content;
        if (!LobbyValidator.validateLobby(lobby)) {
            onCreateLobbyRequest(creator, -1);
            return;
        }

        setupNewLobby(lobby);
        onCreateLobbyRequest(creator, lobby.getId());
        createWatcher(lobby);
    }

    /**
     * Adds player to lobby
     * Intended for TEAM_GAME
     * @param player player to add to lobby
     * @param request consist of:
     *      id of lobby
     *      id of chosen team - 0 or 1
     */
    public void addPlayerToLobby(JoinLobbyRequest request, User player) {
        addPlayerToLobby(player, request.getLobbyId(), request.getTeam(), request.getX(), request.getY());
    }

    /**
     * moves player to chosen team
     * @param player player to move
     * @param teamId team id - 0 or 1
     */
    public void changeTeam(User player, int teamId) {
        Message<Boolean> msg = new Message<>(MessageType.CHANGE_TEAM_RESPONSE);
        msg.content = false;
        Lobby lobby;
        try {
            lobby = getLobbyForUser(player);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            player.sendMessage(MessageParser.toJsonString(msg));
            return;
        }

        if (lobby.getType() == GameType.SOLO_GAME ||
                (teamId != 1 && teamId != 2)) {
            player.sendMessage(MessageParser.toJsonString(msg));
            return;
        }

        lobby.removePlayerFromTeam(player);
        lobby.getTeams().computeIfAbsent(teamId, k -> new ArrayList<>());
        if (!lobby.getTeams().get(teamId).contains(player)) {
            lobby.getTeams().get(teamId).add(player);
        }
        msg.content = true;

        player.sendMessage(MessageParser.toJsonString(msg));
        notifyLobby(lobby);
    }

    /**
     * removes player from lobby
     * @param player player to remove
     */
    public void removePlayerFromLobby(User player, boolean sendMessage) {
        Lobby lobby;
        try {
            lobby = getLobbyForUser(player);
            removePlayerFromLobby(player, lobby, sendMessage);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * @param lobbyId id of lobby
     * @return Lobby object
     */
    public Lobby getLobbyInfo(int lobbyId) {
        for (Lobby l : lobbys) {
            if (l.getId() == lobbyId) {
                return l;
            }
        }

        return null;
    }

    /**
     * @param lobbyId id of lobby
     * @return list of players in specified lobby
     */
    public List<User> getPlayerList(int lobbyId) {
        return lobbyToPlayers.get(lobbyId);
    }

    /**
     * removes lobby and redirect players back to
     * lobby browsing view
     * @param lobbyId id of lobby to remove
     */
    public void removeLobby(int lobbyId, boolean sendMessage) {
        Lobby lobby = getLobbyInfo(lobbyId);
        var players = lobbyToPlayers.get(lobbyId);

        while (!players.isEmpty()) {
            removePlayerFromLobby(players.get(0), lobby, sendMessage);
        }

        lobbys.remove(lobby);
    }

    public void sendLobbyList(LobbyListRequest request, User player) {
        if (player == null) {
            return;
        }
        List<Lobby> lobbyList = new ArrayList<>();
        List<Lobby> tmp = filterLobbies(request, lobbys);

        int i = request.getRangeBegin();
        while (i < tmp.size() && lobbyList.size() <= (request.getRangeEnd() - request.getRangeBegin())) {
            lobbyList.add(tmp.get(i));
            i++;
        }
        LobbyListDelivery delivery = new LobbyListDelivery(lobbyList, tmp.size());
        Message<LobbyListDelivery> msg = new Message<>(MessageType.LOBBY_LIST_DELIVERY, delivery);
        player.sendMessage(MessageParser.toJsonString(msg));
    }

    public void setPlayerIsReady(User user) {
        Lobby lobby;
        try {
            lobby = getLobbyForUser(user);
            lobby.addPlayerToReadyPlayers(user.getServerId());
            notifyLobby(lobby);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    public void setPlayerIsUnready(User user) {
        Lobby lobby;
        try {
            lobby = getLobbyForUser(user);
            lobby.removePlayerFromReadyPlayers(user.getServerId());
            notifyLobby(lobby);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    public Optional<Lobby> getLobbyIfReady(User user) {
        Lobby lobby;
        try {
            lobby = getLobbyForUser(user);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            onStartGameRequest(user, false);
            return Optional.empty();
        }
        boolean playersAreReady = lobby.getPlayers() == lobby.getReadyPlayers() + 1;
        onStartGameRequest(user, playersAreReady);

        if (playersAreReady) {
            return Optional.of(lobby);
        }

        return Optional.empty();
    }

    private void setupNewLobby(Lobby lobby) {
        assignId(lobby);
        lobby.setPlayers(0);
        lobby.setReadyPlayers(0);
        lobby.setOsmService(new OsmService(lobby.getMapId()));
        lobbys.add(lobby);
        lobbyToPlayers.computeIfAbsent(lobby.getId(), k -> new ArrayList<>());
        var teams = lobby.getTeams();

        if (lobby.getType() == GameType.TEAM_GAME) {
            teams.put(1, new ArrayList<>());
            teams.put(2, new ArrayList<>());
        } else {
            teams.put(0, new ArrayList<>());
        }
    }

    private void createWatcher(Lobby lobby) {
        Timer timer = new Timer();
        timer.schedule(new LobbyWatcher(this, lobby, mutex), 120000);
    }

    private void addPlayerToLobby(User player, int lobbyId, int teamId, Double x, Double y) {
        Lobby lobby = getLobbyInfo(lobbyId);
        mutex.lock();
        if (lobby == null) {
            mutex.unlock();
            sendJoinLobbyFailed(player);
            return;
        }
        synchronized (lobby) {
            mutex.unlock();
            if(!LobbyValidator.isJoinPossible(teamId, lobby)) {
                sendJoinLobbyFailed(player);
                return;
            }
            checkCreator(player, lobby);
            addPlayerToTeam(player, teamId, lobby);
        }

        if (!initPlayerLocalization(player, x, y, lobby)) {
            sendJoinLobbyFailed(player);
            return;
        }
        addToLobby(player, lobbyId, lobby);
    }

    private void addPlayerToTeam(User player, int teamId, Lobby lobby) {
        lobby.getTeams().computeIfAbsent(teamId, k -> new ArrayList<>());
        if (!lobby.getTeams().get(teamId).contains(player)) {
            lobby.getTeams().get(teamId).add(player);
        }
    }

    private void checkCreator(User player, Lobby lobby) {
        if (lobby.getPlayers() == 0) {
            lobby.setCreator(player);
            lobby.setName(player.getUsername() + "'s lobby");
            Message<CreatorRightsMsg> msg =
                    new Message<>(MessageType.LOBBY_CREATOR_RIGHTS, new CreatorRightsMsg(lobby.getId()));
            player.sendMessage(MessageParser.toJsonString(msg));
        }
    }

    private boolean initPlayerLocalization(User player, Double x, Double y, Lobby lobby) {
        var platform = player.getPlatform();
        if (platform.isEmpty()) {
            logger.info("user with id="+ player.getServerId()+" doesn't have platform!");
            return false;
        } else if (platform.get() == GamePlatform.MOBILE) {
            try {
                var theNearestNode = lobby.getOsmService().getTheNearestNode(new Coordinates(x, y));
                lobby.setNodeForPlayer(player.getServerId(), theNearestNode);
            } catch (InvalidTargetObjectTypeException e) {
                logger.warning(e.getMessage());
                return false;
            }
        } else {
            try {
                lobby.setNodeForPlayer(player.getServerId(), lobby.getOsmService().getRandomNode());
            } catch (InvalidTargetObjectTypeException e) {
                logger.warning(e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void addToLobby(User player, int lobbyId, Lobby lobby) {
        lobby.setPlayers(lobby.getPlayers() + 1);
        lobbyToPlayers.get(lobbyId).add(player);

        var node = lobby.getNodeForPlayer(player.getServerId());
        if (node == null) {
            sendJoinLobbyFailed(player);
        } else {
            sendJoinLobbySucceed(player, node.getX(), node.getY());
        }

        notifyLobby(lobby);
    }

    private void notifyLobby(Lobby lobby) {
        Message<Lobby> lobbyMsg = new Message<>(MessageType.LOBBY_STATUS_UPDATE, lobby);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        for (User p: lobbyToPlayers.get(lobby.getId())) {
            p.sendMessage(stringMsg);
        }
    }

    /**
     * Send response for create lobby request
     * @param creator lobby's creator
     * @param lobbyId created lobby id, -1 otherwise
     */
    private void onCreateLobbyRequest(User creator, int lobbyId) {
        Message<Integer> lobbyMsg = new Message<>(MessageType.CREATE_LOBBY_RESPONSE, lobbyId);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        if (creator != null) {
            creator.sendMessage(stringMsg);
        }
    }

    private void sendJoinLobbyFailed(User player) {
        JoinLobbyResponse responseData = new JoinLobbyResponse(false, null, null);
        Message<JoinLobbyResponse> lobbyMsg = new Message<>(MessageType.JOIN_LOBBY_RESPONSE, responseData);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        if (player != null) {
            player.sendMessage(stringMsg);
        }
    }

    private void sendJoinLobbySucceed(User player, Double x, Double y) {
        JoinLobbyResponse responseData = new JoinLobbyResponse(true, x, y);
        Message<JoinLobbyResponse> lobbyMsg = new Message<>(MessageType.JOIN_LOBBY_RESPONSE, responseData);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        if (player != null) {
            player.sendMessage(stringMsg);
        }
    }

    private void removePlayerFromLobby(User player, Lobby lobby, boolean sendMessage) {
        Message<Boolean> msg = new Message<>(MessageType.QUIT_LOBBY_RESPONSE, false);
        mutex.lock();
        if (lobby == null) {
            mutex.unlock();
            if (sendMessage) {
                player.sendMessage(MessageParser.toJsonString(msg));
            }
            return;
        }
        synchronized (lobby) {
            mutex.unlock();
            if (lobbyToPlayers.get(lobby.getId()).remove(player)) {
                lobby.setPlayers(lobby.getPlayers() - 1);
                lobby.removePlayerFromTeam(player);
                lobby.removePlayerFromReadyPlayers(player.getServerId());
                if (player == lobby.getCreator()) {
                    if (lobby.getPlayers() == 0) {
                        lobby.setCreator(null);
                        createWatcher(lobby);
                    } else {
                        var newCreator = lobbyToPlayers.get(lobby.getId()).get(0);
                        lobby.setCreator(newCreator);
                        Message<CreatorRightsMsg> creatorMsg =
                                new Message<>(MessageType.LOBBY_CREATOR_RIGHTS, new CreatorRightsMsg(lobby.getId()));
                        newCreator.sendMessage(MessageParser.toJsonString(creatorMsg));
                    }
                }
            }
        }

        msg.content = true;
        if (sendMessage) {
            player.sendMessage(MessageParser.toJsonString(msg));
            notifyLobby(lobby);
        }
    }

    private static synchronized void assignId(Lobby lobby) {
        lobby.setId(idCounter);
        idCounter++;
    }

    private List<Lobby> filterLobbies(LobbyListRequest request, List<Lobby> tmp) {
        if (request.getMapId() != null) {
            tmp = lobbys.stream()
                    .filter(x -> x.getMap().ordinal() == request.getMapId())
                    .collect(Collectors.toList());
        }
        if (request.getGameMode() != null) {
            tmp = tmp.stream()
                    .filter(x -> x.getType() == request.getGameMode())
                    .collect(Collectors.toList());
        }
        if (!request.isIncludeFull()) {
            tmp = tmp.stream()
                    .filter(x -> x.getPlayers() < x.getMaxPlayers())
                    .collect(Collectors.toList());
        }
        return tmp;
    }

    private Lobby getLobbyForUser(User user) throws Exception {
        return lobbys.stream().filter(lobby -> lobbyToPlayers.get(lobby.getId())
                .contains(user)).findFirst()
                .orElseThrow(() -> new Exception("User " + user.getServerId() + " with username " + user.getUsername() + " isn't in any lobby"));
    }

    private void onStartGameRequest(User player, boolean status) {
        Message<Boolean> gameMsg = new Message<>(MessageType.START_GAME_RESPONSE, status);
        var stringMsg = MessageParser.toJsonString(gameMsg);

        if (player != null) {
            player.sendMessage(stringMsg);
        }
    }

    // TODO rm for release
    private void generateDummyLobbys(int n) {
        Random rnd = new Random();
        for (int i = 0; i < n; i++) {
            Lobby lobby = new Lobby();
            lobby.setDwarfs(rnd.nextInt(15) + 5);
            int mapId = rnd.nextInt(8);
            lobby.setMap(GameMap.fromInt(mapId));
            lobby.setOsmService(new OsmService(mapId));
            lobby.setSpeed(rnd.nextDouble() * 5);
            lobby.setMaxSpeed(rnd.nextDouble() * 5);
            lobby.setName("Test lobby " + i);
            lobby.setType(rnd.nextInt(2) == 1 ? GameType.TEAM_GAME : GameType.SOLO_GAME);
            lobby.setId(i + 1357);
            lobbys.add(lobby);
            lobbyToPlayers.computeIfAbsent(lobby.getId(), k -> new ArrayList<>());
            int players = rnd.nextInt(10);
            lobby.setMaxPlayers(rnd.nextInt(10) + players);

            for (int j = 0; j < players; j++) {
                User user = new User("User" + i + " " + j);
                user.setPlatform(GamePlatform.WEB);
                int team = lobby.getType() == GameType.SOLO_GAME ? 0
                        : rnd.nextInt(2) + 1;
                addPlayerToLobby(user, lobby.getId(), team, 100.0, 100.0);
            }
            lobby.setReadyPlayers(rnd.nextInt(lobby.getPlayers() + 1));
        }
    }
}
