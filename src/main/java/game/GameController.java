package game;

import osm.OsmService;

import java.util.Map;

public class GameController {
    private final Map<Integer, User> playerToUser;
    private AbstractGame game;
    private final OsmService osmService;

    public GameController(AbstractGame game, OsmService osmService, Map<Integer, User> playerToUser) {
        this.game = game;
        this.osmService = osmService;
        this.playerToUser = playerToUser;
    }

    private void sendDwarfsInitialLocation() {
        for (AbstractPlayer player: game.getPlayers()) {
            //TODO: send dwarfs location
        }
    }
}
