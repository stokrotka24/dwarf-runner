package game;

public enum WebMove {
    UP,
    LEFT,
    DOWN,
    RIGHT;

    private static final WebMove[] webMoveTypeValues = WebMove.values();

    public static WebMove fromInt(int i) {
        return webMoveTypeValues[i];
    }
}
