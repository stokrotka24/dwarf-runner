package game;

import lobby.Lobby;
import osm.OsmService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    public Map<Integer, GameController> userToGameController;

    public GameManager() {
        this.userToGameController = new HashMap<>();
    }

    public void runGame(Lobby lobby, List<User> users) {
        var gameController = createGameController(lobby, users);
        gameController.runGame();
    }

    private GameController createGameController(Lobby lobby, List<User> users) {
        var osmService = new OsmService(lobby.getMapId());
        var game = buildGame(lobby, users, osmService);

        Map<Integer, User> playerToUser = new HashMap<>();
        for (User user : users) {
            playerToUser.put(user.getServerId(), user);
        }

        var gameController = new GameController(game, osmService, playerToUser);
        for (User user : users) {
            userToGameController.put(user.getServerId(), gameController);
        }
        return gameController;
    }

    private AbstractGame buildGame(Lobby lobby, List<User> users, OsmService osmService) {
        return GameBuilder.aGame()
                .withId(lobby.getId())
                .withGameMap(lobby.getMap())
                .withPlayers(users)
                .withDwarfs(osmService.getUniqueRandomNodes(lobby.getDwarfs()))
                .withMobileMaxSpeed(lobby.getMaxSpeed())
                .withWebSpeed(lobby.getSpeed())
                .withTeams(lobby.getTeams())
                .withGameType(lobby.getType())
                .withEndCondition(lobby.getEnd())
                .build();
    }
}
