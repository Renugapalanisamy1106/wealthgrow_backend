package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class AddSubmittedByColumn {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // ✅ Check if column exists
            String checkSql = """
                SELECT 1 
                FROM information_schema.columns
                WHERE table_schema = 'bfsimf_clean'
                AND table_name = 'data_evaluation'
                AND column_name = 'submitted_by'
            """;

            ResultSet rs = stmt.executeQuery(checkSql);

            if (!rs.next()) {

                // ✅ Add column
                String alterSql = """
                    ALTER TABLE bfsimf_clean.data_evaluation
                    ADD COLUMN submitted_by VARCHAR(50)
                """;

                stmt.execute(alterSql);

                System.out.println("✅ Column 'submitted_by' added successfully");

            } else {
                System.out.println("✅ Column already exists — skipped");
            }

        } catch (Exception e) {
            System.out.println("❌ Error while adding column:");
            e.printStackTrace();
        }
    }
}