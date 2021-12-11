package lobby;

import game.GameMap;
import game.GameType;

public class LobbyValidator {
    /**
     * Checks, if newly created lobby has correct
     * member fields values and can be used in app without any problem
     * @param lobby lobby to evaluate
     * @return correctness of lobby
     */
    public static boolean validateLobby(Lobby lobby) {
        // correct map?
        if (lobby.getMapId() < 0 || lobby.getMapId() >= GameMap.nofMaps()) {
            return false;
        }
        // correct speed?
        if (lobby.getMaxSpeed() <= 0 || lobby.getSpeed() <= 0) {
            return false;
        }

        // other rules - number of players, dwarves etc.
        return lobby.getMaxPlayers() >= 1 && lobby.getDwarfs() >= 1
                && lobby.getEnd() >= 0;
    }

    /**
     * checks if user can join requested lobby and team
     * @param teamId id of team to join
     * @param lobby lobby to join
     * @return is join possible
     */
    public static boolean isJoinPossible(int teamId, Lobby lobby) {
        if (lobby == null || lobby.getPlayers() >= lobby.getMaxPlayers()) {
            return false;
        }

        if (lobby.getType() == GameType.SOLO_GAME) {
            return teamId == 0;
        } else {
            return teamId == 1 || teamId == 2;
        }
    }
}
