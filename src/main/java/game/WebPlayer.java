package game;

import osm.Node;

public class WebPlayer extends AbstractPlayer {
    private Node node = null;

    public WebPlayer(int id) {
        super(id);
    }

    @Override
    public void setNode(Node node) {
        this.node = node;
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