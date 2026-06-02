package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;

public class UpdateUserPasswords {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                UPDATE bfsimf_clean.app_user SET password_hash = CASE
                    WHEN user_id = 'ADMIN001' THEN 'admin@123'
                    WHEN user_id = 'PM001'    THEN 'pm@12345'
                    WHEN user_id = 'BA001'    THEN 'ba@12345'
                    WHEN user_id = 'OPS001'   THEN 'ops@12345'
                    WHEN user_id = 'CMP001'   THEN 'cmp@12345'
                    WHEN user_id = 'INV001'   THEN 'inv@12345'
                    ELSE password_hash
                END;
            """;

            int rows = stmt.executeUpdate(sql);

            System.out.println("✅ Passwords updated successfully");
            System.out.println("✅ Rows affected: " + rows);

        } catch (Exception e) {
            System.out.println("❌ Error updating passwords:");
            e.printStackTrace();
        }
    }
}