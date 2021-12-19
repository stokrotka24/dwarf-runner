package lobby;

import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class LobbyWatcher extends TimerTask {
    private final LobbyManager manager;
    private final Lobby lobby;
    private final ReentrantLock mutex;

    public LobbyWatcher(LobbyManager manager, Lobby lobby, ReentrantLock mutex) {
        this.manager = manager;
        this.lobby = lobby;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        mutex.lock();
        synchronized (lobby) {
            if (lobby.getPlayers() <= 0) {
                manager.removeLobby(lobby.getId(), true);
            }
            mutex.unlock();
        }
    }
}
