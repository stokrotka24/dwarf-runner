package game;

import osm.Node;
import server.ClientHandler;

public class WebPlayer extends AbstractPlayer {
    public WebPlayer(ClientHandler handler, int id, Node node, float positionX, float positionY) {
        super(handler, id, node, positionX, positionY);
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
