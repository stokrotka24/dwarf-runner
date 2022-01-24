package game;

public enum WebMove {
    NORTHEAST,
    NORTH,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTH,
    SOUTHEAST,
    EAST;

    private static final WebMove[] webMoveTypeValues = WebMove.values();

    public static WebMove fromInt(int i) {
        return webMoveTypeValues[i];
    }
}
