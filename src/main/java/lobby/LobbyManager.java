package lobby;

import game.GameMap;
import game.GameType;
import game.User;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyManager {
    private final List<Lobby> lobbys;
    private final Map<Integer, List<User>> lobbyToPlayers;
    private static int idCounter = 0;

    public LobbyManager() {
        lobbys = new ArrayList<>();
        lobbyToPlayers = new HashMap<>();
        // TODO rm for release
        generateDummyLobbys(20);
    }

    // TODO rm for release
    private void generateDummyLobbys(int n) {
        Random rnd = new Random();
        for (int i = 0; i < n; i++) {
            Lobby lobby = new Lobby();
            lobby.setDwarfs(rnd.nextInt(15) + 5);
            lobby.setMap(GameMap.fromInt(rnd.nextInt(8)));
            lobby.setSpeed(rnd.nextFloat() * 5);
            lobby.setMaxSpeed(rnd.nextFloat() * 5);
            lobby.setName("Test lobby " + i);
            lobby.setType(rnd.nextInt(2) == 1 ? GameType.TEAM_GAME : GameType.SOLO_GAME);
            lobby.setPlayers(rnd.nextInt(10));
            lobby.setReadyPlayers(rnd.nextInt(lobby.getPlayers() + 1));
            lobby.setMaxPlayers(rnd.nextInt(10) + lobby.getPlayers());
            lobby.setId(i + 1357);
            lobbys.add(lobby);
            lobbyToPlayers.computeIfAbsent(lobby.getId(), k -> new ArrayList<>());
        }
    }

    /**
     * Creates new Lobby, adds its creator to it
     * and notify subscribers
     * @param msg msg with required data
     * @param creator Player who creates lobby
     */
    public void createLobby(Message<Lobby> msg, User creator) {
        Lobby lobby = msg.content;
        if (!validateLobby(lobby)) {
            onJoinLobbyRequest(creator, false);
            return;
        }

        assignId(lobby);
        lobby.setPlayers(0);
        lobby.setReadyPlayers(0);
        lobbys.add(lobby);
        lobbyToPlayers.computeIfAbsent(lobby.getId(), k -> new ArrayList<>());

        addPlayerToLobby(creator, lobby.getId(), 0);
    }

    private boolean validateLobby(Lobby lobby) {
        if (lobby.getMapId() < 0 || lobby.getMapId() >= GameMap.nofMaps() ||
                !(lobby.getEnd() == 0 || lobby.getEnd() == 1)) {
            return false;
        }

        if (lobby.getMaxPlayers() < 1 || lobby.getDwarfs() < 1
                || lobby.getMaxSpeed() <= 0 || lobby.getSpeed() <= 0) {
            return false;
        }

        return true;
    }

    private boolean addPlayerToLobby(User player, int lobbyId, int teamId) {
        Lobby lobby = getLobbyInfo(lobbyId);

        if (lobby == null || lobby.getPlayers() >= lobby.getMaxPlayers()) {
            onJoinLobbyRequest(player, false);
            return false;
        }

        lobby.getTeams().computeIfAbsent(teamId, k -> new ArrayList<>());
        if (!lobby.getTeams().get(teamId).contains(player)) {
            lobby.getTeams().get(teamId).add(player);
        }
        addToLobby(player, lobbyId, lobby);

        return true;
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
        addPlayerToLobby(player, request.getLobbyId(), request.getTeam());
    }

    private void addToLobby(User player, int lobbyId, Lobby lobby) {
        lobby.setPlayers(lobby.getPlayers() + 1);
        lobbyToPlayers.get(lobbyId).add(player);
        onJoinLobbyRequest(player, true);
        notifyLobby(lobby);
    }

    private void notifyLobby(Lobby lobby) {
        Message<Lobby> lobbyMsg = new Message<>(MessageType.LOBBY_STATUS_UPDATE, lobby);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        for (User p: lobbyToPlayers.get(lobby.getId())) {
            p.sendMessage(stringMsg);
        }
    }

    private void onJoinLobbyRequest(User player, boolean status) {
        Message<Boolean> lobbyMsg = new Message<>(MessageType.JOIN_LOBBY_RESPONSE, status);
        var stringMsg = MessageParser.toJsonString(lobbyMsg);

        if (player != null) {
            player.sendMessage(stringMsg);
        }
    }

    /**
     * moves player to chosen team
     * @param player player to move
     * @param teamId team id - 0 or 1
     * @return result
     */
    public void changeTeam(User player, int teamId) {
        Message<Boolean> msg = new Message<>(MessageType.CHANGE_TEAM_RESPONSE);
        msg.content = false;

        for (Lobby l : lobbys) {
            if (lobbyToPlayers.get(l.getId()).contains(player)) {
                l.removePlayerFromTeam(player);
                l.getTeams().computeIfAbsent(teamId, k -> new ArrayList<>());
                if (!l.getTeams().get(teamId).contains(player)) {
                    l.getTeams().get(teamId).add(player);
                }
                msg.content = true;
            }
        }
        player.sendMessage(MessageParser.toJsonString(msg));
    }

    /**
     * removes player from lobby
     * @param player player to remove
     */
    public void removePlayerFromLobby(User player) {
        Lobby lobby = getLobbyForUser(player);
        removePlayerFromLobby(player, lobby);
    }

    private void removePlayerFromLobby(User player, Lobby lobby) {
        Message<Boolean> msg = new Message<>(MessageType.QUIT_LOBBY_RESPONSE, false);
        if (lobby == null) {
            player.sendMessage(MessageParser.toJsonString(msg));
            return;
        }

        if (lobbyToPlayers.get(lobby.getId()).remove(player)) {
            lobby.setPlayers(lobby.getPlayers() - 1);
            lobby.removePlayerFromTeam(player);
            lobby.removePlayerFromReadyPlayers(player.getServerId());
            notifyLobby(lobby);
        }
        msg.content = true;
        player.sendMessage(MessageParser.toJsonString(msg));
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
    public void removeLobby(int lobbyId) {
        Lobby lobby = getLobbyInfo(lobbyId);
        var players = lobbyToPlayers.get(lobbyId);

        for (User p: players) {
            removePlayerFromLobby(p, lobby);
        }

        lobbys.remove(lobby);
    }

    private static synchronized void assignId(Lobby lobby) {
        lobby.setId(idCounter);
        idCounter++;
    }

    public void sendLobbyList(LobbyListRequest request, User player) {
        if (player == null) {
            return;
        }
        List<Lobby> lobbyList = new ArrayList<>();
        List<Lobby> tmp = lobbys;

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

        int i = request.getRangeBegin();
        while (i < tmp.size() && i <= request.getRangeEnd()) {
            if (request.isIncludeFull() || tmp.get(i).getPlayers() < tmp.get(i).getMaxPlayers()) {
                lobbyList.add(tmp.get(i));
            }
            i++;
        }
        LobbyListDelivery delivery = new LobbyListDelivery(lobbyList, tmp.size());
        Message<LobbyListDelivery> msg = new Message<>(MessageType.LOBBY_LIST_DELIVERY, delivery);
        player.sendMessage(MessageParser.toJsonString(msg));
    }

    private Lobby getLobbyForUser(User user) {
        return lobbys.stream().filter(lobby -> lobbyToPlayers.get(lobby.getId()).contains(user)).findFirst().orElse(null);
    }

    public void setPlayerIsReady(User user) {
        Lobby lobby = getLobbyForUser(user);
        lobby.addPlayerToReadyPlayers(user.getServerId());
    }

    public void setPlayerIsUnready(User user) {
        Lobby lobby = getLobbyForUser(user);
        lobby.removePlayerFromReadyPlayers(user.getServerId());
    }

    public Optional<Lobby> getLobbyIfReady(User user) {
        var lobby = getLobbyForUser(user);
        boolean playersAreReady = lobby.getPlayers() == lobby.getReadyPlayers() + 1;
        onStartGameRequest(user, playersAreReady);

        if (playersAreReady) {
            return Optional.of(lobby);
        }

        return Optional.empty();
    }

    private void onStartGameRequest(User player, boolean status) {
        Message<Boolean> gameMsg = new Message<>(MessageType.START_GAME_RESPONSE, status);
        var stringMsg = MessageParser.toJsonString(gameMsg);

        if (player != null) {
            player.sendMessage(stringMsg);
        }
    }
}
