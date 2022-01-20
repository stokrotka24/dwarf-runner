package game;

public enum WebMove {
    NORTH,
    WEST,
    SOUTH,
    EAST,
    NORTHEAST,
    NORTHWEST,
    SOUTHEAST,
    SOUTHWEST;

    private static final WebMove[] webMoveTypeValues = WebMove.values();

    public static WebMove fromInt(int i) {
        return webMoveTypeValues[i];
    }
}
