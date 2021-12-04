package game;

import game.json.DwarfsLocationListDelivery;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.util.Map;

public class GameController {
    private final Map<Integer, User> playerToUser;
    private AbstractGame game;

    public GameController(AbstractGame game, Map<Integer, User> playerToUser) {
        this.game = game;
        this.playerToUser = playerToUser;
    }

    private void sendDwarfsLocation() {
        for (AbstractPlayer player: game.getPlayers()) {
            playerToUser.get(player.getId()).sendMessage(createdDwarfsLocationDelivery());
        }
    }

    protected String createdDwarfsLocationDelivery() {
        var dwarfsListDelivery = new DwarfsLocationListDelivery(game.getDwarfs());
        var msg = new Message<>(MessageType.DWARF_LIST_DELIVERY, dwarfsListDelivery);
        return MessageParser.toJsonString(msg);
    }

    public void runGame() {
        var timeToEnd =  game.getTimeToEnd();
        if (timeToEnd > 0) {
            long timeMillis = timeToEnd * 60 * 1000;
            new TimerTask(timeMillis);
        }
        sendDwarfsLocation();
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
