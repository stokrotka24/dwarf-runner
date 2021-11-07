package lobby;

import game.GameMap;
import game.GameType;
import game.AbstractPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    public int id;
    public String name;
    public GameType type;
    public int players;
    public int maxPlayers;
    public GameMap map;
    // TODO end - possibly enum?
    public int end;
    public float speed;
    public float maxSpeed;
    public int dwarfs;
    public transient Map<Integer, List<AbstractPlayer>> teams = new HashMap<>();
}
