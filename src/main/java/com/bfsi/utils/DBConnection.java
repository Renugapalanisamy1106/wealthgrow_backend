package com.bfsi.utils;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.SQLException;

public class DBConnection {

    private static final String URL ="jdbc:mysql://localhost:3306/bfsimf_clean?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER ="root";
    private static final String PASSWORD ="password";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn;
    }
}