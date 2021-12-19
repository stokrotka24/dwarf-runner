package dbconn;

import server.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public final class DBConnection {

    private static String url = "jdbc:sqlserver://SQL5080.site4now.net:1433;databaseName=db_a7d05b_dwarfrunnerdb;"
            + "user=db_a7d05b_dwarfrunnerdb_admin;password=Dwarf1Runner";
    private static String username = "db_a7d05b_dwarfrunnerdb_admin";
    private static String password = "Dwarf1Runner";
    private static final Logger logger = Logger.getInstance();
    private static Connection connection;

    /**
     * called to receive connection object to database 
     * @return Connection object, connected to our database
     */
    public static Connection getConnection() {

        if (connection == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(url);
                logger.info("First connection to database is successful " + url);
            }
            catch (Exception e) { 
                logger.warning("Cannot create database connection");
            }
        }
        logger.info("Returning database connection");
        return connection;
    }
}