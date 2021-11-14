package game;

import osm.OsmService;

public class GameController {
    private AbstractGame game;
    private OsmService osmService;

    public GameController(AbstractGame game) {
        this.game = game;
        sendDwarfsInitialLocation();
    }

    private void sendDwarfsInitialLocation() {
        for (AbstractPlayer player: game.getPlayers()) {
            //TODO: send dwarfs location
        }
    }
}
