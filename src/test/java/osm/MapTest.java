package osm;

import game.GameMap;
import org.junit.Test;

public class MapTest {

    @Test
    public void test() {
        for (int i = 0; i < GameMap.values().length; i++) {
            OsmService service = new OsmService(i);
        }
    }
}
