package dbconn;

import java.sql.Connection; 
import java.sql.DriverManager;

public class DBConnection {

    //TODO: when db is working, update url
    private static String url = "jdbc:mysql://localhost:3306/thegame";
    private static String username = "Backend_user";
    private static String password = "BackendUser!2021";

    /**
     * called to receive connection object to database 
     * @return Connection object, connected to our database
     */
    public static Connection getConnection() {

        Connection connection = null;
        try { 
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection is successful to the database " + url);
        }
        catch (Exception e) { 
            System.out.println("Cannot create database connection");
            e.printStackTrace();
        } 
        return connection;
    }
}