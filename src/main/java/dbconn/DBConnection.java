package dbconn;

import server.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBConnection {

    private static final String url = "jdbc:sqlserver://DwarfRunner.mssql.somee.com;databaseName=DwarfRunner;"
            + "user=Snakey616_SQLLogin_1;password=v2mnzk4mzh";
    private static final String username = "db_a7d05b_dwarfrunnerdb_admin";
    private static final String password = "Dwarf1Runner";
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
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery("SELECT 1");
        } 
        catch (SQLException e) {
            logger.warning(e.getMessage());
            connection = reopenConnection();
        }

        logger.info("Returning database connection");
        return connection;
    }
    
    public static Connection reopenConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
            logger.info("Connection reopen successful. Url: " + url);
        } 
        catch (Exception e) {
            logger.warning("Connection reopen failure");
        }
        return connection;
    }
}
