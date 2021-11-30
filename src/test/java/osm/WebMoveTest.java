package osm;

import game.AbstractGame;
import game.GameMap;
import game.SoloGame;
import game.WebMove;
import game.WebPlayer;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WebMoveTest {

    @Test
    public void test() {
        AbstractGame game = new SoloGame(1,GameMap.OLD_TOWN,null,0.0000001,0,null);

        OsmService service = new OsmService(0);
        Node node = service.getNodes().get(8);

        WebPlayer player = new WebPlayer(1);
        player.setNode(node);
        player.setCoords(new Coordinates(0.0,0.0));

        Coordinates coords1 = new Coordinates(17.029637492, 51.108117836);
        Coordinates coords2 = new Coordinates(17.02966586, 51.10811158);

        double maxDistFromNode1 = 0.00004;
        double maxDistFromNode2 = 0.000004;

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode1);

        player.setCoords(coords1);
        game.webMove(player, WebMove.UP);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.LEFT);
        assertEquals(17.02963739434643, player.getCoords().getX());
        assertEquals(51.108117857535554, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.DOWN);
        assertEquals(17.02963742663529, player.getCoords().getX());
        assertEquals(51.10811776032005, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.RIGHT);
        assertEquals(17.029637589653568, player.getCoords().getX());
        assertEquals(51.10811781446444, player.getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode2);

        player.setCoords(coords1);
        game.webMove(player, WebMove.UP);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.LEFT);
        assertEquals(17.02963739434643, player.getCoords().getX());
        assertEquals(51.108117857535554, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.DOWN);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        player.setCoords(coords1);
        game.webMove(player, WebMove.RIGHT);
        assertEquals(17.029637589653568, player.getCoords().getX());
        assertEquals(51.10811781446444, player.getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode1);

        player.setCoords(coords2);
        game.webMove(player, WebMove.UP);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.LEFT);
        assertEquals(17.029665762346433, player.getCoords().getX());
        assertEquals(51.108111601535555, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.DOWN);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.RIGHT);
        assertEquals(17.02966595765357, player.getCoords().getX());
        assertEquals(51.108111558464444, player.getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode2);

        player.setCoords(coords2);
        game.webMove(player, WebMove.UP);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.LEFT);
        assertEquals(17.029665762346433, player.getCoords().getX());
        assertEquals(51.108111601535555, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.DOWN);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        game.webMove(player, WebMove.RIGHT);
        assertEquals(17.02966595765357, player.getCoords().getX());
        assertEquals(51.108111558464444, player.getCoords().getY());
    }
}
