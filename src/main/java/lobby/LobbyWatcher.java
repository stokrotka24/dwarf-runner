package lobby;

import java.util.TimerTask;

public class LobbyWatcher extends TimerTask {
    private final LobbyManager manager;
    private final Lobby lobby;

    public LobbyWatcher(LobbyManager manager, Lobby lobby) {
        this.manager = manager;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        synchronized (lobby) {
            if (lobby.getPlayers() <= 0) {
                manager.removeLobby(lobby.getId());
            }
        }
    }
}
