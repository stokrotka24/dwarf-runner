package game;

import java.util.List;

public class SoloGame extends AbstractGame {
    public SoloGame(int id, GameMap gameMap, List<AbstractPlayer> players, double webSpeed, double mobileMaxSpeed, List<Dwarf> dwarfs) {
        super(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs);
    }
}
