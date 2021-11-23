package game;

import java.util.List;
import osm.Coordinates;

public abstract class AbstractGame {

    private int id;
    private GameMap gameMap;
    private List<AbstractPlayer> players;
    private double webSpeed;
    private double mobileMaxSpeed;
    private List<Dwarf> dwarfs;

    public AbstractGame(int id, GameMap gameMap, List<AbstractPlayer> players, float webSpeed, float mobileMaxSpeed, List<Dwarf> dwarfs) {
        this.id = id;
        this.gameMap = gameMap;
        this.players = players;
        this.webSpeed = webSpeed;
        this.mobileMaxSpeed = mobileMaxSpeed;
        this.dwarfs = dwarfs;
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public void webMove(WebPlayer player, WebMove move) {

        Coordinates from = player.getCoords();
        Coordinates to = null;

        Double x = from.getLon();
        Double y = from.getLat();

        for (int i = 0; i < 4; i++) {
            if (move == WebMove.fromInt(i)) {
                double mini = 1000;
                for (int j = 0; j < player.getNode().getNeighbors().size(); j++) {
                    Coordinates neighbor = player.getNode().getNeighbors().get(j);
                    double angle = Math
                        .toDegrees(Math.atan2(neighbor.getLat() - y, neighbor.getLon() - x));
                    angle -= (i * 90);
                    if (angle >= 45 && angle <= 135) {
                        if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                            mini = angle;
                            to = player.getNode().getNeighbors().get(j);
                        }
                    }
                }
                double angle = Math
                    .toDegrees(
                        Math.atan2(player.getNode().getLat() - y, player.getNode().getLon() - x))
                    - (i * 90);
                if (angle >= 45 && angle <= 135) {
                    if (Math.abs(angle - 90) < Math.abs(mini - 90)) {
                        to = player.getNode().getCoords();
                    }
                }
                break;
            }
        }

        // X - lon
        // Y - lat
        if (to != null) {
            double deltaX = from.getLon() - to.getLon();
            double deltaY = from.getLat() - to.getLat();
            double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            double t = Math.min(1, webSpeed / dist);
            double newX = from.getLon() + t * (to.getLon() - from.getLon());
            double newY = from.getLat() + t * (to.getLat() - from.getLat());
            player.setLon(newX);
            player.setLat(newY);
        }
    }
}
