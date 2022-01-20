package osm;

import game.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveTest {

    @Test
    public void test() {
        List<AbstractPlayer> players = new ArrayList<>();
        OsmService service = new OsmService(0);
        AbstractPlayer player = new WebPlayer(1, new Node(service.getNodes().get(8)));
        players.add(player);
        AbstractGame game = new SoloGame(1,GameMap.OLD_TOWN, service, players,0.0000001,0,null,0);

        player.setCoords(new Coordinates(0.0,0.0));
        Coordinates coords1 = new Coordinates(17.029637492, 51.108117836);
        Coordinates coords2 = new Coordinates(17.02966586, 51.10811158);

        double maxDistFromNode1 = 0.00004;
        double maxDistFromNode2 = 0.000004;

        /*----------------------------------------------------------------------*/
        OsmService.setNodeRadius(maxDistFromNode1);
        
        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.NORTH), game);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        /*player.setCoords(coords1);
        player.makeMove(new Move(WebMove.LEFT), game);
        assertEquals(17.02963739434643, player.getCoords().getX());
        assertEquals(51.108117857535554, player.getCoords().getY());

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.DOWN), game);
        assertEquals(17.02963742663529, player.getCoords().getX());
        assertEquals(51.10811776032005, player.getCoords().getY());

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.RIGHT), game);
        assertEquals(17.029637589653568, player.getCoords().getX());
        assertEquals(51.10811781446444, player.getCoords().getY());

        *//*----------------------------------------------------------------------*//*
        OsmService.setNodeRadius(maxDistFromNode2);

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.UP), game);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.LEFT), game);
        assertEquals(17.02963739434643, player.getCoords().getX());
        assertEquals(51.108117857535554, player.getCoords().getY());

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.DOWN), game);
        assertEquals(17.029637492, player.getCoords().getX());
        assertEquals(51.108117836, player.getCoords().getY());

        player.setCoords(coords1);
        player.makeMove(new Move(WebMove.RIGHT), game);
        assertEquals(17.029637589653568, player.getCoords().getX());
        assertEquals(51.10811781446444, player.getCoords().getY());

        *//*----------------------------------------------------------------------*//*
        OsmService.setNodeRadius(maxDistFromNode1);

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.UP), game);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.LEFT), game);
        assertEquals(17.029665762346433, player.getCoords().getX());
        assertEquals(51.108111601535555, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.DOWN), game);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.RIGHT), game);
        assertEquals(17.02966595765357, player.getCoords().getX());
        assertEquals(51.108111558464444, player.getCoords().getY());

        *//*----------------------------------------------------------------------*//*
        OsmService.setNodeRadius(maxDistFromNode2);

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.UP), game);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.LEFT), game);
        assertEquals(17.029665762346433, player.getCoords().getX());
        assertEquals(51.108111601535555, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.DOWN), game);
        assertEquals(17.02966586, player.getCoords().getX());
        assertEquals(51.10811158, player.getCoords().getY());

        player.setCoords(coords2);
        player.makeMove(new Move(WebMove.RIGHT), game);
        assertEquals(17.02966595765357, player.getCoords().getX());
        assertEquals(51.108111558464444, player.getCoords().getY());*/
    }

    // TODO mobile move tests
    /*@Test
    public void test2() {
        List<AbstractPlayer> players = new ArrayList<>();
        OsmService service = new OsmService(0);
        AbstractPlayer player = new MobilePlayer(1, new Node(service.getNodes().get(8)));
        players.add(player);
        AbstractGame game = new SoloGame(1, GameMap.OLD_TOWN, service, players, 0.0000001, 0.0000002, null,
            0);

        player.setCoords(new Coordinates(0.0, 0.0));
        Coordinates coords1 = new Coordinates(17.029637492, 51.108117836);
        Coordinates coords2 = new Coordinates(17.02966586, 51.10811158);

        double maxDistFromNode1 = 0.00004;
        double maxDistFromNode2 = 0.000004;
    }*/
}
