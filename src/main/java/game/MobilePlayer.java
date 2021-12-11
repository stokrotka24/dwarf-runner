package game;

import osm.Coordinates;
import osm.Node;

import java.sql.Timestamp;

public class MobilePlayer extends AbstractPlayer {
    private Timestamp banTimestamp = null;
  
    public MobilePlayer(int id, Node node) {
        super(id, node);
        this.coords = new Coordinates(0.0, 0.0);
    }

    public void setBanTimestamp(Timestamp banTimestamp) {
        this.banTimestamp = banTimestamp;
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

    @Override
    public void makeMove(Move move, AbstractGame game) {
        //TODO
    }
}