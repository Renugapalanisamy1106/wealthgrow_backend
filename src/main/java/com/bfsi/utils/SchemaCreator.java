package com.bfsi.utils;

import java.sql.Connection;
import java.sql.Statement;


public class SchemaCreator {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute("CREATE SCHEMA IF NOT EXISTS bfsimf_clean;");
            stmt.execute("SET search_path TO bfsimf_clean;");

            System.out.println("✅ Schema bfsimf_clean ready.");

            /* =====================================================
               ROLES
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_role (
                    role_id VARCHAR(30) PRIMARY KEY,
                    role_name VARCHAR(50) NOT NULL
                );
            """);

            /* =====================================================
               USERS & PROFILES
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app_user (
                    user_id VARCHAR(30) PRIMARY KEY,
                    user_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role_id VARCHAR(30),
                    FOREIGN KEY (role_id) REFERENCES user_role(role_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_profile (
                    user_id VARCHAR(30) PRIMARY KEY,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50),
                    email VARCHAR(100),
                    mobile VARCHAR(20),
                    dob DATE,
                    pan VARCHAR(20),
                    address TEXT,
                    FOREIGN KEY (user_id) REFERENCES app_user(user_id)
                );
            """);

            /* =====================================================
               NOTIFICATIONS
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notification (
                    notification_id VARCHAR(50) PRIMARY KEY,
                    user_id VARCHAR(30),
                    message TEXT,
                    type VARCHAR(50),
                    status VARCHAR(20),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES app_user(user_id)
                );
            """);

            /* =====================================================
               MUTUAL FUNDS & NAV
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mutualfund_product (
                    fund_id VARCHAR(30) PRIMARY KEY,
                    fund_name VARCHAR(150),
                    category_name VARCHAR(50),
                    nav_level NUMERIC(12,4),
                    risk VARCHAR(50),
                    status VARCHAR(20),
                    promotion_status VARCHAR(20),
                    created_at DATE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS nav_calculation (
                    nav_id VARCHAR(50) PRIMARY KEY,
                    fund_id VARCHAR(30),
                    nav_value NUMERIC(12,4),
                    calculated_on DATE,
                    FOREIGN KEY (fund_id) REFERENCES mutualfund_product(fund_id)
                );
            """);

            /* =====================================================
               INVESTOR PORTFOLIO & TRANSACTIONS
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS investor_portfolio (
                    portfolio_id VARCHAR(50) PRIMARY KEY,
                    investor_id VARCHAR(30),
                    fund_id VARCHAR(30),
                    unit_balance INT,
                    current_value NUMERIC(14,2),
                    purchase_date DATE,
                    FOREIGN KEY (investor_id) REFERENCES app_user(user_id),
                    FOREIGN KEY (fund_id) REFERENCES mutualfund_product(fund_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    txn_id VARCHAR(50) PRIMARY KEY,
                    investor_id VARCHAR(30),
                    fund_id VARCHAR(30),
                    txn_type VARCHAR(50),
                    amount NUMERIC(14,2),
                    payment_mode VARCHAR(50),
                    status VARCHAR(30),
                    txn_date DATE,
                    FOREIGN KEY (investor_id) REFERENCES app_user(user_id),
                    FOREIGN KEY (fund_id) REFERENCES mutualfund_product(fund_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS unit_allocation (
                    allocation_id VARCHAR(50) PRIMARY KEY,
                    txn_id VARCHAR(50),
                    fund_id VARCHAR(30),
                    nav_applied NUMERIC(12,4),
                    units_allocated INT,
                    status VARCHAR(30),
                    FOREIGN KEY (txn_id) REFERENCES transactions(txn_id)
                );
            """);

            /* =====================================================
               SCENARIOS & EVALUATION
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scenario_analysis (
                    scenario_id VARCHAR(30) PRIMARY KEY,
                    scenario_name VARCHAR(100),
                    scenario_date DATE,
                    status VARCHAR(30),
                    action VARCHAR(50),
                    description TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scenario_impact_result (
                    impact_id VARCHAR(50) PRIMARY KEY,
                    scenario_id VARCHAR(30),
                    fund_id VARCHAR(30),
                    risk_impact DOUBLE PRECISION,
                    stability_score DOUBLE PRECISION,
                    recommendation VARCHAR(50),
                    analysis_data TEXT,
                    approved BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (scenario_id) REFERENCES scenario_analysis(scenario_id),
                    FOREIGN KEY (fund_id) REFERENCES mutualfund_product(fund_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS evaluation (
                    eval_id VARCHAR(50) PRIMARY KEY,
                    scenario_id VARCHAR(30),
                    submitted_by VARCHAR(30),
                    status VARCHAR(30),
                    FOREIGN KEY (scenario_id) REFERENCES scenario_analysis(scenario_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS data_evaluation (
                    request_id VARCHAR(50) PRIMARY KEY,
                    request_to_role VARCHAR(50),
                    scenario_id VARCHAR(30),
                    status VARCHAR(30),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            /* =====================================================
               COMPLAINTS
               ===================================================== */

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS complaints (
                    complaint_id VARCHAR(50) PRIMARY KEY,
                    investor_id VARCHAR(30),
                    category VARCHAR(50),
                    status VARCHAR(30),
                    raised_date DATE,
                    priority VARCHAR(20),
                    FOREIGN KEY (investor_id) REFERENCES app_user(user_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS complaint_details (
                    detail_id VARCHAR(50) PRIMARY KEY,
                    complaint_id VARCHAR(50),
                    description TEXT,
                    resolution TEXT,
                    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS complaint_view (
                    complaint_id VARCHAR(50) PRIMARY KEY,
                    investor_name VARCHAR(100),
                    category VARCHAR(50),
                    status VARCHAR(30),
                    created_at DATE
                );
            """);

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS scenario_nav_series (
        series_id VARCHAR(50) PRIMARY KEY,
        impact_id VARCHAR(50),
        nav_value DOUBLE PRECISION,
        nav_date VARCHAR(30),
        sequence_no INT,
        FOREIGN KEY (impact_id) REFERENCES scenario_impact_result(impact_id)
    );
""");

            System.out.println("ALL tables created successfully in bfsimf_clean.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}