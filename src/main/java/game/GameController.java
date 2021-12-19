package game;

import game.json.*;
import messages.Message;
import messages.MessageParser;
import messages.MessageType;
import server.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameController {
    private final Map<Integer, User> playerToUser;
    private AbstractGame game;
    private static final Logger logger = Logger.getInstance();

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

    private void sendPlayersPointsUpdate() {
        var update = createdPlayersPointsUpdate();
        for (AbstractPlayer player: game.getPlayers()) {
            playerToUser.get(player.getId()).sendMessage(update);
        }
    }

    protected String createdPlayersPointsUpdate() {
        Map<String, List<PlayerPoints>> playersPointsUpdate =
                game.getTeams()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> "team" + entry.getKey(), entry -> mapUsernamesToPoints(entry.getValue())));
        var msg = new Message<>(MessageType.PLAYERS_POINTS_UPDATE, playersPointsUpdate);
        return MessageParser.toJsonString(msg);
    }

    private List<PlayerPoints> mapUsernamesToPoints(List<AbstractPlayer> players) {
        return players
                .stream()
                .map(player -> new PlayerPoints(
                        playerToUser.get(player.getId()).getUsername(),
                        player.points))
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
        MoveValidation validation = player.makeMove(move, game);
        switch(validation) {
            case WEB_VALID_MOVE:
            case WEB_INVALID_MOVE: {
                break;
            }
            case MOBILE_VALID_MOVE: {
                sendMoveResult(player, MoveValidation.MOBILE_VALID_MOVE, null, null, null);
                break;
            }
            case SPEED_BAN:
            case SPEED_BAN_CONTINUE: {
                sendMoveResult(player, MoveValidation.SPEED_BAN, player.getNode().getX(), player.getNode().getY(), player.getBanTimeLeft());
                break;
            }
            case POSITION_BAN:
            case POSITION_BAN_CONTINUE: {
                sendMoveResult(player, MoveValidation.POSITION_BAN, player.getNode().getX(), player.getNode().getY(), null);
                break;
            }
            default: {
                logger.warning("Unexpected move validation!");
                break;
            }
        }
        sendPositionDataUpdate();
    }

    private void sendMoveResult(AbstractPlayer player, MoveValidation moveValidation,  Double x, Double y, Double punishmentTime) {
        var response = new MobileMoveResponse(moveValidation, x, y, punishmentTime);
        var msg = new Message<>(MessageType.MOBILE_MOVE_RESPONSE, response);
        playerToUser.get(player.getId()).sendMessage(MessageParser.toJsonString(msg));
    }

    public void performDwarfPickUp(Integer playerId, Integer dwarfId) {
        var player = game.getPlayer(playerId);
        var resultMsg = pickUpDwarf(player, dwarfId);

        playerToUser.get(player.getId()).sendMessage(resultMsg);
        sendPlayersPointsUpdate();
        sendDwarfsLocation();
        if (game.getDwarfs().isEmpty()) {
            endGame();
        }
    }

    protected String pickUpDwarf(AbstractPlayer player, Integer dwarfId) {
        var dwarf = game.getDwarfById(dwarfId);
        var response = new PickDwarfResponse();

        if (dwarf == null)  {
            response.setStatus(0);
        } else {
            var success = player.pickUpDwarf(dwarf);
            response.setStatus(success);

            if (success == 1) {
                response.setPoints(dwarf.getPoints());
                game.removeDwarf(dwarf);
            }
        }

        return MessageParser.toJsonString(new Message<>(MessageType.PICK_DWARF_RESPONSE, response));
    }

    public void removePlayer(Integer playerId) {
        game.removePlayer(playerId);
        playerToUser.remove(playerId);
        if (game.getPlayers().isEmpty()) {
            endGame();
        }
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
                logger.warning(e.getMessage());
            }
        }
    }
}
