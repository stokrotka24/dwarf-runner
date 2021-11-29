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
            lobby.setId(i + 1357);
            lobbys.add(lobby);
            lobbyToPlayers.computeIfAbsent(lobby.getId(), k -> new ArrayList<>());
            int players = rnd.nextInt(10);
            for (int j = 0; j < players; j++) {
                User user = new User("User" + i + " " + j);
                int team = lobby.getType() == GameType.SOLO_GAME ? 0
                        : rnd.nextInt(2) + 1;
                addPlayerToLobby(user, lobby.getId(), team);
            }
            lobby.setReadyPlayers(rnd.nextInt(lobby.getPlayers() + 1));
            lobby.setMaxPlayers(rnd.nextInt(10) + lobby.getPlayers());
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

        setupNewLobby(lobby);

        addPlayerToLobby(creator, lobby.getId(),
                lobby.getType() == GameType.SOLO_GAME ? 0 : 1);
    }

    private void setupNewLobby(Lobby lobby) {
        assignId(lobby);
        lobby.setPlayers(0);
        lobby.setReadyPlayers(0);
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

    private void addPlayerToLobby(User player, int lobbyId, int teamId) {
        Lobby lobby = getLobbyInfo(lobbyId);

        if(!checkIfJoinPossible(player, teamId, lobby)) {
            onJoinLobbyRequest(player, false);
            return;
        }

        if (lobby.getPlayers() == 0) {
            lobby.setCreator(player);
        }

        lobby.getTeams().computeIfAbsent(teamId, k -> new ArrayList<>());
        if (!lobby.getTeams().get(teamId).contains(player)) {
            lobby.getTeams().get(teamId).add(player);
        }
        addToLobby(player, lobbyId, lobby);
    }

    private boolean checkIfJoinPossible(User player, int teamId, Lobby lobby) {
        if (lobby == null || lobby.getPlayers() >= lobby.getMaxPlayers()) {
            return false;
        }

        if (lobby.getType() == GameType.SOLO_GAME && teamId != 0) {
            return false;
        } else {
            return teamId == 1 || teamId == 2;
        }
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
        Lobby lobby = getLobbyForUser(player);

        if (lobby.getType() == GameType.SOLO_GAME ||
                (teamId != 1 && teamId != 2)) {
            onJoinLobbyRequest(player, false);
            player.sendMessage(MessageParser.toJsonString(msg));
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
            if (lobby.getPlayers() == 0) {
                lobby.setCreator(null);
            } else {
                lobby.setCreator(lobbyToPlayers.get(lobby.getId()).get(0));
            }
        }
        msg.content = true;
        player.sendMessage(MessageParser.toJsonString(msg));
        notifyLobby(lobby);
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
        List<Lobby> tmp = filterLobbies(request, lobbyList);

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
        return tmp;
    }

    private Lobby getLobbyForUser(User user) {
        return lobbys.stream().filter(lobby -> lobbyToPlayers.get(lobby.getId()).contains(user)).findFirst().orElse(null);
    }

    public void setPlayerIsReady(User user) {
        Lobby lobby = getLobbyForUser(user);
        lobby.addPlayerToReadyPlayers(user.getServerId());
        notifyLobby(lobby);
    }

    public void setPlayerIsUnready(User user) {
        Lobby lobby = getLobbyForUser(user);
        lobby.removePlayerFromReadyPlayers(user.getServerId());
        notifyLobby(lobby);
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
