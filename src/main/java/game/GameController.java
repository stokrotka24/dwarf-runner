package game;

import osm.OsmService;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
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

    public void runGame() {
        var timeToEnd =  game.getTimeToEnd();
        if (timeToEnd > 0) {
            long timeMillis = timeToEnd * 60 * 1000;
            new TimerTask(timeMillis);
        }
        sendDwarfsInitialLocation();
        sendWebPlayersInitialLocation();
        //TODO  when we get mobile location we can send to all players location of other players
    }

    private void sendWebPlayersInitialLocation() {
        for (AbstractPlayer player: game.getPlayers()) {
            if (player instanceof WebPlayer) {
                try {
                    var node = osmService.getRandomNode();
                    player.setNode(node);
                    var user = playerToUser.get(player.getId());
                    //TODO user.sendMessage(); with proper message
                } catch (InvalidTargetObjectTypeException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void endGame() {
    }

    class TimerTask extends Thread {
        private final long time;

        public TimerTask(long time) {
            this.time = time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(time);
                GameController.this.endGame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
