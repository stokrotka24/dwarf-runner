package game;

import org.junit.jupiter.api.Test;
import utility.GameUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    @Test
    void shouldClearGame() {
        var gameManager = new GameManager();

        var nofDwarfsLocations = 2;
        int nofPlayers = 2;

        var users = GameUtils.prepareUsersMock(nofPlayers);
        Map<Integer, List<User>> teams = new HashMap<>();
        teams.put(0, users);
        var lobby = GameUtils.prepareLobbyMock("SOLO", users, teams, 0, nofDwarfsLocations);
        var gameController = GameUtils.prepareGameControllerMock(lobby, users);

        for (User user : users) {
            gameManager.userToGameController.put(user.getServerId(), gameController);
        }

        gameManager.clearGame(gameController);

        assertEquals(0, gameManager.userToGameController.size());
    }
}