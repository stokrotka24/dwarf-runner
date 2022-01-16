package dbconn;

import game.*;
import org.junit.jupiter.api.Test;
import osm.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class GameStatsManagerTest {
    @Test
    void testSaveGameInfo() {
        var users = prepareUsersMock(4);
        var game = prepareGameMock(users);

        int i = GameStatsManager.saveGameInfo(game);
        assertTrue(i >= 0);
        var users2 = prepareUsersMock(6);
        var game2 = prepareGameMock(users2);

        int j = GameStatsManager.saveGameInfo(game2);
        assertTrue(j >= 0);

        assertNotEquals(i, j);
    }

    // TODO just to see if any errors, will be expanded later
    @Test
    void setPlayerResultTest() {
        GameStatsManager.savePlayerResultInfo(16, "user1@wp.pl", 3);
    }

    private List<User> prepareUsersMock(int nofPlayers) {
        var users = new ArrayList<User>();
        for (int i = 1; i <= nofPlayers; i++) {
            var user = new User(i);
            user.setPlatform(GamePlatform.WEB);
            user.setUsername("User"+i);
            users.add(user);
        }

        return users;
    }

    private AbstractGame prepareGameMock(List<User> users) {
        Map<Integer, Node> playersToInitialNode = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            playersToInitialNode.put(users.get(i).getServerId(), new Node((long) i, 0.5435 * i, 0.534534 * i));
        }
        return GameBuilder.aGame()
                .withId(1)
                .withGameMap(GameMap.MAIN_STATION)
                .withPlayers(users, playersToInitialNode)
                .withMobileMaxSpeed(6.0f)
                .withWebSpeed(5.0f)
                .withGameType(GameType.SOLO_GAME)
                .build();
    }
}
