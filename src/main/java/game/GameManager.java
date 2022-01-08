package game;

import lobby.Lobby;

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

    public void disconnectUser(User sender) {
        var controller = userToGameController.get(sender.getServerId());
        if (controller != null) {
            controller.removePlayer(sender.getServerId());
        }
        userToGameController.remove(sender.getServerId());
    }

    private GameController createGameController(Lobby lobby, List<User> users) {
        var game = buildGame(lobby, users);

        Map<Integer, User> playerToUser = new HashMap<>();
        for (User user : users) {
            playerToUser.put(user.getServerId(), user);
        }

        var gameController = new GameController(game, playerToUser, this);
        for (User user : users) {
            userToGameController.put(user.getServerId(), gameController);
        }
        return gameController;
    }

    public int getNumberOfGames() {
        return userToGameController.values().size();
    }

    private AbstractGame buildGame(Lobby lobby, List<User> users) {
        return GameBuilder.aGame()
                .withId(lobby.getId())
                .withGameMap(lobby.getMap())
                .withOsmService(lobby.getOsmService())
                .withPlayers(users, lobby.getPlayersToInitialNode())
                .withDwarfs(lobby.getDwarfs(), lobby.getOsmService())
                .withMobileMaxSpeed(lobby.getMaxSpeed())
                .withWebSpeed(lobby.getSpeed())
                .withTeams(lobby.getTeams(), lobby.getPlayersToInitialNode())
                .withGameType(lobby.getType())
                .withEndCondition(lobby.getEnd())
                .build();
    }

    public void clearGame(GameController gameController) {
        while (userToGameController.values().remove(gameController));
    }
}
