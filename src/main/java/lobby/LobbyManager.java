package lobby;

import game.AbstractGame;
import game.AbstractPlayer;
import game.GameBuilder;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyManager {
    private final List<Lobby> lobbys;
    private final Map<Integer, List<AbstractPlayer>> lobbyToPlayers;
    private final List<AbstractPlayer> subscribedToLobbyChanges;
    private static int idCounter = 0;

    public LobbyManager() {
        lobbys = new ArrayList<>();
        lobbyToPlayers = new HashMap<>();
        subscribedToLobbyChanges = new ArrayList<>();
    }

    /**
     * Creates new Lobby, adds its creator to it
     * and notify subscribers
     * @param msg msg with required data
     * @param creator Player who creates lobby
     */
    public void createLobby(Message<Lobby> msg, AbstractPlayer creator) {
        Lobby lobby = msg.content;

        assignId(lobby);
        lobby.players = 0;
        addPlayerToLobby(creator, lobby.id);
        notifySubscribed();

        lobbys.add(lobby);
    }

    /**
     * registers player to subscribers, meaning that
     * LobbyManager will notify player automatically on any important change
     * @param player Player to register
     */
    public void addToSubscribed(AbstractPlayer player) {
        if (!subscribedToLobbyChanges.contains(player)) {
            subscribedToLobbyChanges.add(player);
        }
        notifyPlayer(player);
    }

    /**
     * removes player from subscribers if it is there, meaning that
     * LobbyManager will no longer notify player automatically
     * @param player Player to unsubscribe
     */
    public void removeFromSubscribed(AbstractPlayer player) {
        if (!subscribedToLobbyChanges.contains(player)) {
            subscribedToLobbyChanges.remove(player);
        }
    }

    /**
     * Adds player to lobby
     * Intended for SOLO_GAME
     * @param player player to add to lobby
     * @param lobbyId id of lobby
     * @return result
     */
    public boolean addPlayerToLobby(AbstractPlayer player, int lobbyId) {
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
    public boolean addPlayerToLobby(AbstractPlayer player, int lobbyId, int teamId) {
        Lobby lobby = getLobbyInfo(lobbyId);

        if (lobby == null || lobby.players >= lobby.maxPlayers) {
            return false;
        }

        if (lobby.teams.get(teamId == 1 ? 0 : 1).contains(player)) {
            lobby.teams.get(teamId).add(player);
        }
        addToLobby(player, lobbyId, lobby);

        return true;
    }

    private void addToLobby(AbstractPlayer player, int lobbyId, Lobby lobby) {
        lobby.players++;
        lobbyToPlayers.get(lobbyId).add(player);
        removeFromSubscribed(player);
        notifySubscribed();
    }

    /**
     * moves player to chosen team
     * @param player player to move
     * @param teamId team id - 0 or 1
     * @return result
     */
    public boolean changeTeam(AbstractPlayer player, int teamId) {
        for (Lobby l : lobbys) {
            if (lobbyToPlayers.get(l.id).contains(player)) {
                var otherTeam = l.teams.get(teamId == 1 ? 0 : 1);
                otherTeam.remove(player);
                if (!l.teams.get(teamId).contains(player)) {
                    l.teams.get(teamId).add(player);
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
    public void removePlayerFromLobby(AbstractPlayer player, int lobbyId) {
        Lobby lobby = getLobbyInfo(lobbyId);
        if (lobby == null) {
            return;
        }

        if (lobbyToPlayers.get(lobbyId).remove(player)) {
            lobby.players--;
            lobby.teams.get(0).remove(player);
            lobby.teams.get(1).remove(player);
            notifySubscribed();
        }
    }

    /**
     * @param lobbyId id of lobby
     * @return Lobby object
     */
    public Lobby getLobbyInfo(int lobbyId) {
        for (Lobby l : lobbys) {
            if (l.id == lobbyId) {
                return l;
            }
        }

        return null;
    }

    /**
     * @param lobbyId id of lobby
     * @return list of players in specified lobby
     */
    public List<AbstractPlayer> getPlayerList(int lobbyId) {
        return lobbyToPlayers.get(lobbyId);
    }

    /**
     * creates game from specified lobby
     * @param lobbyId id of lobby
     * @return newly created game object
     */
    public AbstractGame createGame(int lobbyId) {
        AbstractGame game = null;
        Lobby lobby = getLobbyInfo(lobbyId);

        if (lobby == null) {
            return null;
        }

        return buildGame(lobby);
    }

    // TODO might need changes after GameBuilder implementation
    private AbstractGame buildGame(Lobby lobby) {
        GameBuilder builder = new GameBuilder();
        builder.setId(lobby.id);
        builder.setMapType(lobby.map);
        builder.setDwarfs(lobby.dwarfs);
        builder.setMaxMobileSpeed(lobby.maxSpeed);
        builder.setWebSpeed(lobby.speed);
        builder.setTeams(lobby.teams);

        return builder.build();
    }

    /**
     * removes lobby and redirect players back to
     * lobby browsing view
     * @param lobbyId id of lobby to remove
     */
    public void removeLobby(int lobbyId) {
        Lobby lobby = getLobbyInfo(lobbyId);
        var players = lobbyToPlayers.get(lobbyId);

        for (AbstractPlayer p: players) {
            removePlayerFromLobby(p, lobbyId);
            addToSubscribed(p);
        }

        lobbys.remove(lobby);
    }

    private void notifySubscribed() {
        Message<List<Lobby>> lobbyStateMsg = new Message<>(MessageType.LOBBYS_DATA, lobbys);
        var stringMsg = MessageParser.toJsonString(lobbyStateMsg);

        for (AbstractPlayer p: subscribedToLobbyChanges) {
            p.sendMessage(stringMsg);
        }
    }

    private void notifyPlayer(AbstractPlayer player) {
        Message<List<Lobby>> lobbyStateMsg = new Message<>(MessageType.LOBBYS_DATA, lobbys);
        var stringMsg = MessageParser.toJsonString(lobbyStateMsg);

        player.sendMessage(stringMsg);
    }

    private static synchronized void assignId(Lobby lobby) {
        lobby.id = idCounter;
        idCounter++;
    }
}
