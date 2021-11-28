package dbconn;

import java.sql.Connection; 
import java.sql.DriverManager;

public class DBConnection {

    private static String url = "jdbc:sqlserver://SQL5080.site4now.net:1433;databaseName=db_a7d05b_dwarfrunnerdb;"
            + "user=db_a7d05b_dwarfrunnerdb_admin;password=Dwarf1Runner";
    private static String username = "db_a7d05b_dwarfrunnerdb_admin";
    private static String password = "Dwarf1Runner";

    /**
     * called to receive connection object to database 
     * @return Connection object, connected to our database
     */
    public static Connection getConnection() {

        Connection connection = null;
        try { 
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
            System.out.println("Connection is successful to the database " + url);
        }
        catch (Exception e) { 
            System.out.println("Cannot create database connection");
            e.printStackTrace();
        } 
        return connection;
    }
}