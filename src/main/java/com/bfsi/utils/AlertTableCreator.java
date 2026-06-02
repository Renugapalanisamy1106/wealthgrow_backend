package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;

public class AlertTableCreator {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS alerts (
                    alert_id VARCHAR(50) PRIMARY KEY,
                    txn_id VARCHAR(50),
                    alert_type VARCHAR(50),
                    issue_category VARCHAR(100),
                    remarks VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """;

            stmt.execute(sql);

            System.out.println("✅ ALERT table created successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
