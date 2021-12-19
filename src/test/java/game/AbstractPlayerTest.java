package game;

import org.junit.jupiter.api.Test;
import osm.Coordinates;
import osm.Node;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AbstractPlayerTest {

    @Test
    void shouldBeNearToDwarf() {
        var dwarf = new Dwarf(new Node(1L, 1.0, 1.0), 0);
        AbstractPlayer player = new WebPlayer(1, new Node(2L, 1.00004, 1.00004));
        player.setCoords(new Coordinates(1.000006, 1.000003));
        assertTrue(player.isNearToDwarf(dwarf));
    }

    @Test
    void shouldNotBeNearToDwarf() {
        var dwarf = new Dwarf(new Node(1L, 1.0, 1.0), 0);
        AbstractPlayer player = new WebPlayer(1, new Node(2L, 1.00004, 1.00004));
        player.setCoords(new Coordinates(1.012, 1.000003));
        assertFalse(player.isNearToDwarf(dwarf));
    }
}