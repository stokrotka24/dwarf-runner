package game;

import osm.Node;
import server.ClientHandler;

import java.sql.Timestamp;

public class MobilePlayer extends AbstractPlayer {
    private Timestamp banTimestamp = null;

    public MobilePlayer(ClientHandler handler, int id, Node node, float positionX, float positionY) {
        super(handler, id, node, positionX, positionY);
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.MOBILE;
    }

    @Override
    public boolean pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf) && !isBanned()) {
            this.points += dwarf.getPoints();
            return true;
        }
        return false;
    }

    private boolean isBanned() {
        if (banTimestamp == null) {
            return false;
        }
        if (new Timestamp(System.currentTimeMillis()).getTime() - banTimestamp.getTime() >= 100L) {
            //TODO: change 100L to real ban time
            banTimestamp = null;
            return false;
        }
        return true;
    }
}
