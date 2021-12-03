package game;

import java.util.Map;

public class GameController {
    private final Map<Integer, User> playerToUser;
    private AbstractGame game;

    public GameController(AbstractGame game, Map<Integer, User> playerToUser) {
        this.game = game;
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
        //TODO  send to all players location of other players
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
