package server;

public enum LogLevel {
    NONE(0),
    ERROR(1),
    WARNING(2),
    INFO(3),
    ALL(4);

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public boolean isGreaterOrEqual(LogLevel level) {
        return this.level >= level.level;
    }

    public boolean isLessOrEqual(LogLevel level) {
        return this.level <= level.level;
    }
}
