package com.bfsi.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class SchemaVerifier {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("✅ Connected to DB\n");

            DatabaseMetaData metaData = conn.getMetaData();

            // ✅ Get all tables in your schema
            ResultSet tables = metaData.getTables(null, "bfsimf_clean", "%", new String[]{"TABLE"});

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");

                System.out.println("\n📌 TABLE: " + tableName);

                // ✅ Get columns for each table
                ResultSet columns = metaData.getColumns(null, "bfsimf_clean", tableName, "%");

                while (columns.next()) {

                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int size = columns.getInt("COLUMN_SIZE");

                    System.out.println("   ➜ " + columnName + " : " + dataType + "(" + size + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}