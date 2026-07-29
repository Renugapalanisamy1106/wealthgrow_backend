package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBMigrationScript {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            
            // ================================
            // 4. Add complaints.mobile_number  (Task 1)
            // ================================
            stmt.execute("""
                ALTER TABLE bfsimf_clean.complaints
                ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(100)
            """);
            System.out.println("✅ Column 'complaints.preferred_language' ensured");

            System.out.println("🎉 Migration script completed successfully!");

        } catch (Exception e) {
            System.out.println("❌ Migration script failed");
            e.printStackTrace();
        }
    }
}