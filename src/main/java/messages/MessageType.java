package messages;

/**
 * Enumeration of  msg codes
 * should be uniform between server and clients
 */
public enum MessageType {
    CLIENT_HELLO,
    SERVER_HELLO,
    LOG_IN_REQUEST,
    LOG_IN_RESPONSE,
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    LOG_OUT_REQUEST,
    CHANGE_SETTINGS_REQUEST,
    CHANGE_SETTINGS_RESPONSE,
    CREATE_LOBBY_REQUEST,
    CREATE_LOBBY_RESPONSE,
    LOBBY_LIST_REQUEST,
    LOBBY_LIST_DELIVERY,
    DWARF_LIST_DELIVERY,
    LOBBY_STATUS_UPDATE,
    JOIN_LOBBY_REQUEST,
    JOIN_LOBBY_RESPONSE,
    QUIT_LOBBY_REQUEST,
    QUIT_LOBBY_RESPONSE,
    CHANGE_TEAM_REQUEST,
    CHANGE_TEAM_RESPONSE,
    PLAYER_IS_READY,
    PLAYER_IS_UNREADY,
    START_GAME_REQUEST,
    START_GAME_RESPONSE,
    POSITION_DATA,
    GAME_STATE_UPDATE,
    PICK_DWARF_REQUEST,
    PICK_DWARF_RESPONSE,
    RANKING_DATA,
    ACKNOWLEDGE,
    ERROR;

    private static final MessageType[] msgTypeValues = MessageType.values();

    public static MessageType fromInt(int i) {
        return msgTypeValues[i];
    }
}
