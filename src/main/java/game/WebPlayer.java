package game;

import osm.Node;

public class WebPlayer extends AbstractPlayer {
    private Node node = null;

    public WebPlayer(int id, Node node) {
        super(id, node);
    }

    @Override
    public GamePlatform getPlatform() {
        return GamePlatform.WEB;
    }

    @Override
    public boolean pickUpDwarf(Dwarf dwarf) {
        if (isNearToDwarf(dwarf)) {
            this.points += dwarf.getPoints();
            return true;
        }
        return false;
    }
}