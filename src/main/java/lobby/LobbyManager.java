package lobby;

import game.AbstractGame;
import game.User;
import game.GameBuilder;
import game.GameMap;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LobbyManager {
    private final List<Lobby> lobbys;
    private final Map<Integer, List<User>> lobbyToPlayers;
    private static int idCounter = 0;

    public LobbyManager() {
        lobbys = new ArrayList<>();
        lobbyToPlayers = new HashMap<>();
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
        lobbys.add(lobby);

        addPlayerToLobby(creator, lobby.getId());
    }

    private boolean validateLobby(Lobby lobby) {
        if (lobby.getMapId() < 0 || lobby.getMapId() >= GameMap.nofMaps()) {
            return false;
        }

        if (lobby.getMaxPlayers() < 1 || lobby.getDwarfs() < 1
                || lobby.getMaxSpeed() <= 0 || lobby.getSpeed() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * Adds player to lobby
     * Intended for SOLO_GAME
     * @param player player to add to lobby
     * @param lobbyId id of lobby
     * @return result
     */
    public boolean addPlayerToLobby(User player, int lobbyId) {
        return addPlayerToLobby(player, lobbyId, 0);
    }

    /**
     * Adds player to lobby
     * Intended for TEAM_GAME
     * @param player player to add to lobby
     * @param lobbyId id of lobby
     * @param teamId id of chosen team - 0 or 1
     * @return result
     */
    public boolean addPlayerToLobby(User player, int lobbyId, int teamId) {
        Lobby lobby = getLobbyInfo(lobbyId);

        if (lobby == null || lobby.getPlayers() >= lobby.getMaxPlayers()) {
            System.out.println("Rejected there");

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

    private void addToLobby(User player, int lobbyId, Lobby lobby) {
        lobby.setPlayers(lobby.getPlayers() + 1);
        lobbyToPlayers.computeIfAbsent(lobbyId, k -> new ArrayList<>());
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
    public boolean changeTeam(User player, int teamId) {
        for (Lobby l : lobbys) {
            if (lobbyToPlayers.get(l.getId()).contains(player)) {
                var otherTeam = l.getTeams().get(teamId == 1 ? 0 : 1);
                otherTeam.remove(player);
                if (!l.getTeams().get(teamId).contains(player)) {
                    l.getTeams().get(teamId).add(player);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * removes player from lobby
     * @param player player to remove
     * @param lobbyId id of lobby
     */
    public void removePlayerFromLobby(User player, int lobbyId) {
        Lobby lobby = getLobbyInfo(lobbyId);
        if (lobby == null) {
            return;
        }

        if (lobbyToPlayers.get(lobbyId).remove(player)) {
            lobby.setPlayers(lobby.getPlayers() - 1);
            lobby.getTeams().get(0).remove(player);
            lobby.getTeams().get(1).remove(player);
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
     * creates game from specified lobby
     * @param lobbyId id of lobby
     * @return newly created game object
     */
    public AbstractGame createGame(int lobbyId) {
        Lobby lobby = getLobbyInfo(lobbyId);

        if (lobby == null) {
            return null;
        }

        return buildGame(lobby);
    }

    // TODO might need changes after GameBuilder implementation
    private AbstractGame buildGame(Lobby lobby) {
        return GameBuilder.aGame()
                .withId(lobby.getId())
                .withGameMap(lobby.getMap())
                .withPlayers(lobbyToPlayers.get(lobby.getId()))
                .withDwarfs(lobby.getDwarfs())
                .withMobileMaxSpeed(lobby.getMaxSpeed())
                .withWebSpeed(lobby.getSpeed())
                .withTeams(lobby.getTeams())
                .withGameType(lobby.getType())
                .build();
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
            removePlayerFromLobby(p, lobbyId);
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
        List<Lobby> lobbyList = new ArrayList<Lobby>();
        List<Lobby> tmp = new ArrayList<>();

        if (request.getMapId() != -1) {
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
            if (request.isIncludeFull() || lobbys.get(i).getPlayers() < lobbys.get(i).getMaxPlayers()) {
                lobbyList.add(tmp.get(i));
            }
            i++;
        }
        LobbyListDelivery delivery = new LobbyListDelivery(lobbyList, tmp.size());
        Message<LobbyListDelivery> msg = new Message<>(MessageType.LOBBY_LIST_DELIVERY, delivery);
        player.sendMessage(MessageParser.toJsonString(msg));
    }
}
