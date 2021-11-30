package osm;

import game.AbstractGame;
import game.AbstractPlayer;
import game.GameMap;
import game.SoloGame;
import game.WebMove;
import game.WebPlayer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WebMoveTest {

    @Test
    public void test() {
        List<AbstractPlayer> players = new ArrayList<>();
        players.add(new WebPlayer(1));
        AbstractGame game = new SoloGame(1,GameMap.OLD_TOWN,players,0.0000001,0,null,0);

        OsmService service = new OsmService(0);

        game.getPlayers().get(0).setNode(new Node(service.getNodes().get(8)));
        long a = game.getPlayers().get(0).getNode().getId();
        game.getPlayers().get(0).setCoords(new Coordinates(0.0,0.0));

        Coordinates coords1 = new Coordinates(17.029637492, 51.108117836);
        Coordinates coords2 = new Coordinates(17.02966586, 51.10811158);

        double maxDistFromNode1 = 0.00004;
        double maxDistFromNode2 = 0.000004;

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode1);

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.UP);
        assertEquals(17.029637492, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108117836, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.LEFT);
        assertEquals(17.02963739434643, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108117857535554, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.DOWN);
        assertEquals(17.02963742663529, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811776032005, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.RIGHT);
        assertEquals(17.029637589653568, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811781446444, game.getPlayers().get(0).getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode2);

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.UP);
        assertEquals(17.029637492, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108117836, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.LEFT);
        assertEquals(17.02963739434643, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108117857535554, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.DOWN);
        assertEquals(17.029637492, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108117836, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords1);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.RIGHT);
        assertEquals(17.029637589653568, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811781446444, game.getPlayers().get(0).getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode1);

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.UP);
        assertEquals(17.02966586, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811158, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.LEFT);
        assertEquals(17.029665762346433, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108111601535555, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.DOWN);
        assertEquals(17.02966586, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811158, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.RIGHT);
        assertEquals(17.02966595765357, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108111558464444, game.getPlayers().get(0).getCoords().getY());

        /*----------------------------------------------------------------------*/
        game.setOnlyBackOrForward(maxDistFromNode2);

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.UP);
        assertEquals(17.02966586, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811158, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.LEFT);
        assertEquals(17.029665762346433, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108111601535555, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.DOWN);
        assertEquals(17.02966586, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.10811158, game.getPlayers().get(0).getCoords().getY());

        game.getPlayers().get(0).setCoords(coords2);
        game.webMove((WebPlayer) game.getPlayers().get(0), WebMove.RIGHT);
        assertEquals(17.02966595765357, game.getPlayers().get(0).getCoords().getX());
        assertEquals(51.108111558464444, game.getPlayers().get(0).getCoords().getY());
    }
}
