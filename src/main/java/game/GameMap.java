package game;

import messages.MessageType;

public enum GameMap {
    OLD_TOWN,
    CENTENNIAL_HALL,
    MAIN_STATION,
    WESTERN_PARK,
    PWR_MAIN_CAMPUS,
    PWR_ARCHITECTURE_CAMPUS,
    CATHEDRAL_ISLAND,
    SZCZYTNICKI_PARK;

    private static final GameMap[] mapTypeValues = GameMap.values();

    public static GameMap fromInt(int i) {
        return mapTypeValues[i];
    }
}
