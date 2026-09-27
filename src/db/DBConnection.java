/*
 * Decompiled with CFR 0.152.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/fastfood_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123";
    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add mysql-connector-j jar to classpath.", classNotFoundException);
        }
        catch (SQLException sQLException) {
            throw new RuntimeException("Failed to connect to database: " + sQLException.getMessage(), sQLException);
        }
        return connection;
    }

    public static void main(String[] stringArray) {
        try {
            Connection connection = DBConnection.getConnection();
            System.out.println("Connected successfully: " + !connection.isClosed());
        }
        catch (Exception exception) {
            System.out.println("Connection failed: " + exception.getMessage());
        }
    }
}

