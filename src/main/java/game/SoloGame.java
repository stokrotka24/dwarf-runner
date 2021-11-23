package game;

import java.util.List;

public class SoloGame extends AbstractGame {
    public SoloGame(int id, GameMap gameMap, List<AbstractPlayer> players, float webSpeed, float mobileMaxSpeed, List<Dwarf> dwarfs) {
        super(id, gameMap, players, webSpeed, mobileMaxSpeed, dwarfs);
    }
}