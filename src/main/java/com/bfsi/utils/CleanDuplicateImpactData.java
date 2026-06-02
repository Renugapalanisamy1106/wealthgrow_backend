package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;

public class CleanDuplicateImpactData {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ Connected to DB");

            conn.setAutoCommit(false);  // ✅ transaction start

            // ✅ STEP 1: DELETE NAV SERIES (children first)
            String deleteNavSeries = """
                DELETE FROM bfsimf_clean.scenario_nav_series
                WHERE impact_id IN (
                    SELECT impact_id FROM bfsimf_clean.scenario_impact_result
                    WHERE impact_id NOT IN (
                        SELECT MIN(impact_id)
                        FROM bfsimf_clean.scenario_impact_result
                        GROUP BY scenario_id, fund_id
                    )
                );
            """;

            int navDeleted = stmt.executeUpdate(deleteNavSeries);
            System.out.println("✅ NAV series deleted: " + navDeleted);

            // ✅ STEP 2: DELETE DUPLICATE IMPACT ROWS
            String deleteImpact = """
                DELETE FROM bfsimf_clean.scenario_impact_result
                WHERE impact_id NOT IN (
                    SELECT MIN(impact_id)
                    FROM bfsimf_clean.scenario_impact_result
                    GROUP BY scenario_id, fund_id
                );
            """;

            int impactDeleted = stmt.executeUpdate(deleteImpact);
            System.out.println("✅ Impact rows deleted: " + impactDeleted);

            conn.commit();  // ✅ commit transaction

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}