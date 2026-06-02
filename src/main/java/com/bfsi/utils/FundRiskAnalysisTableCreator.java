package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;

public class FundRiskAnalysisTableCreator {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS bfsimf_clean.fund_risk_analysis (
    analysis_id      VARCHAR(30)    NOT NULL PRIMARY KEY,
    fund_id          VARCHAR(20)    NOT NULL,
    fund_name        VARCHAR(100),

    sharpe_ratio     DOUBLE PRECISION NOT NULL DEFAULT 0,
    max_drawdown     DOUBLE PRECISION NOT NULL DEFAULT 0,
    stability_score  DOUBLE PRECISION NOT NULL DEFAULT 0,
    volatility       DOUBLE PRECISION NOT NULL DEFAULT 0,

    suggested_risk   VARCHAR(20)    NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    submitted_by     VARCHAR(50),
    created_at       DATE,
    remarks          TEXT,

    FOREIGN KEY (fund_id)
        REFERENCES bfsimf_clean.mutualfund_product(fund_id)
);
            """;

            stmt.execute(sql);

            System.out.println("✅ fund_risk_analysis table created successfully (or already exists)");

        } catch (Exception e) {
            System.out.println("❌ Error creating fund_risk_analysis table:");
            e.printStackTrace();
        }
    }
}