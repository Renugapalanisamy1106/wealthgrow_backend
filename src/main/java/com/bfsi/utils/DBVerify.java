package com.bfsi.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * DBVerify Utility
 *
 * ✅ Lists all tables in bfsimf_clean schema
 * ✅ Prints all rows & columns from each table
 * ✅ READ-ONLY (safe to run anytime)
 */
public class DBVerify {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {

            // Ensure we are reading from the clean schema only
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET search_path TO bfsimf_clean;");
            }

            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("🔍 Verifying schema: bfsimf_clean\n");

            // Fetch all tables in bfsimf_clean
            ResultSet tables = metaData.getTables(
                    null,
                    "bfsimf_clean",
                    "%",
                    new String[]{"TABLE"}
            );

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");
                System.out.println("=================================================");
                System.out.println("📦 TABLE: " + tableName);
                System.out.println("=================================================");

                printTableData(conn, tableName);
                System.out.println();
            }

            System.out.println("✅ DB verification completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTableData(Connection conn, String tableName) {

        String query = "SELECT * FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData rsMeta = rs.getMetaData();
            int columnCount = rsMeta.getColumnCount();

            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMeta.getColumnName(i) + "\t");
            }
            System.out.println();

            boolean hasData = false;

            // Print rows
            while (rs.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

            if (!hasData) {
                System.out.println("⚠️  (No data)");
            }

        } catch (Exception e) {
            System.out.println("❌ Error reading table: " + tableName);
            e.printStackTrace();
        }
    }
}