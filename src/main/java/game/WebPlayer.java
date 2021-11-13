package game;

import server.ClientHandler;

public class WebPlayer extends AbstractPlayer {
    public WebPlayer(int id, ClientHandler handler) {
        super(id,handler);
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
