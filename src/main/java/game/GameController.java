package game;

import game.json.DwarfsLocationListDelivery;
import game.json.PositionData;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameController {
    private final Map<Integer, User> playerToUser;
    private AbstractGame game;

    public GameController(AbstractGame game, Map<Integer, User> playerToUser) {
        this.game = game;
        this.playerToUser = playerToUser;
    }

    private void sendDwarfsLocation() {
        var delivery = createdDwarfsLocationDelivery();
        for (AbstractPlayer player: game.getPlayers()) {
            playerToUser.get(player.getId()).sendMessage(delivery);
        }
    }

    protected String createdDwarfsLocationDelivery() {
        var dwarfsListDelivery = new DwarfsLocationListDelivery(game.getDwarfs());
        var msg = new Message<>(MessageType.DWARF_LIST_DELIVERY, dwarfsListDelivery);
        return MessageParser.toJsonString(msg);
    }

    private void sendPositionDataUpdate() {
        var update = createdPositionDataUpdate();
        for (AbstractPlayer player: game.getPlayers()) {
            playerToUser.get(player.getId()).sendMessage(update);
        }
    }

    protected String createdPositionDataUpdate() {
        Map<String, List<PositionData>> positionData =
                game.getTeams()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> "team" + entry.getKey(), entry -> mapPlayersToPositionData(entry.getValue())));
        var msg = new Message<>(MessageType.POSITION_DATA_UPDATE, positionData);
        return MessageParser.toJsonString(msg);
    }

    private List<PositionData> mapPlayersToPositionData(List<AbstractPlayer> players) {
        return players
                .stream()
                .map(player -> new PositionData(
                        playerToUser.get(player.getId()).getUsername(),
                        player.getCoords().getX(),
                        player.getCoords().getY()))
                .collect(Collectors.toList());
    }

    public void runGame() {
        var timeToEnd =  game.getTimeToEnd();
        if (timeToEnd > 0) {
            long timeMillis = timeToEnd * 60 * 1000;
            new TimerTask(timeMillis);
        }
        sendDwarfsLocation();
        sendPositionDataUpdate();
    }

    public void endGame() {
    }

    public void performMove(Integer playerId, Move move) {
        var player = game.getPlayer(playerId);
        player.makeMove(move, game);
        sendPositionDataUpdate();
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
