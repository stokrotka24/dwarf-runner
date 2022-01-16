package dbconn;

import game.AbstractGame;
import server.Logger;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class GameStatsManager {
    private static final String addGameQuery = "{call Add_game(?, ?, ?, ?, ?, ?)}";
    private static final String setPlayerResultQuery = "{call Add_players_in_game(?, ?, ?)}";
    private static final Logger logger = Logger.getInstance();

    public static int saveGameInfo(AbstractGame game) {
        int id = -1;
        try {
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(addGameQuery);

            cStatement.setInt(1, game.getPlayers().size());
            cStatement.setString(2, game.getGameMap().toString());
            cStatement.setFloat(3, (float)game.getWebSpeed());
            cStatement.setFloat(4, (float)game.getMobileMaxSpeed());
            cStatement.setString(5, game.getType().toString());
            cStatement.registerOutParameter(6, Types.INTEGER);

            cStatement.execute();
            id = cStatement.getInt(6);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            logger.warning(ex.getMessage());
        }
        return id;
    }

    public static void savePlayerResultInfo(int gameId, String email, int place) {
        try {
            CallableStatement cStatement = DBConnection.getConnection().prepareCall(setPlayerResultQuery);

            cStatement.setInt(1, gameId);
            cStatement.setString(2, email);
            cStatement.setInt(3, place);

            cStatement.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            logger.warning(ex.getMessage());
        }
    }
}
