package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Logger {
    private static Logger instance;
    private static final Object mutex = new Object();
    private Logger () {}
    private LogLevel level = LogLevel.ERROR;
    private LoggerOption option = LoggerOption.LOG_TO_FILE;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withZone(ZoneId.systemDefault());
    private String filename = "log_" + Instant.now().getEpochSecond() + ".txt";
    private final String dirName = "logs/";
    private FileWriter writer;
    
    public static Logger getInstance() {
        Logger result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Logger();
            }
        }
        return result;
    }

    public void error(String msg) {
        if (level.isGreaterOrEqual(LogLevel.ERROR)) {
            log("Error:", msg);
        }
    }

    public void warning(String msg) {
        if (level.isGreaterOrEqual(LogLevel.WARNING)) {
            log("Warning:", msg);
        }
    }

    public void info(String msg) {
        if (level.isGreaterOrEqual(LogLevel.INFO)) {
            log("Info:", msg);
        }
    }

    private void log(String label, String msg) {
        if (option == LoggerOption.LOG_TO_CONSOLE) {
            System.out.println("[" + formatter.format(Instant.now()) + "] " + label + " " + msg);
        } else if (option == LoggerOption.LOG_TO_FILE) {
            if (writer == null) {
                prepareFile();
            }
            try {
                writer.write("[" + formatter.format(Instant.now()) + "] " + label + " " + msg + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareFile() {
        try {
            File dir = new File(dirName);
            dir.mkdir();
            writer = new FileWriter(dirName + filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoggingLevel(LogLevel level) {
        this.level = level;
    }

    public void setOption(LoggerOption option) {
        this.option = option;
    }

    public void setLogFile(String filename) {
        try {
            writer.close();
            this.filename = filename;
            prepareFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}