package com.bfsi.utils;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.SQLException;

public class DBConnection {

    private static final String URL =System.getenv("DB_URL") != null ? System.getenv("DB_URL")
            : "jdbc:postgresql://172.26.48.73:5432/pnemar26mutualfundsdb";
    private static final String USER =System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "pnemar26mutualfunds";
    private static final String PASSWORD =System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "mutual";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        conn.prepareStatement("SET search_path TO bfsimf_clean").execute();

        return conn;
    }
}