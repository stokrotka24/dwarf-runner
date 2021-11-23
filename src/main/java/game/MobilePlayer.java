package game;

import java.sql.Timestamp;
import osm.Node;

public class MobilePlayer extends AbstractPlayer {
    private Timestamp banTimestamp = null;
  
    public MobilePlayer(int id, Node node) {
        super(id, node);
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
}