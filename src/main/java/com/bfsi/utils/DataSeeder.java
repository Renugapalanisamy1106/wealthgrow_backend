package com.bfsi.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;

public class DataSeeder {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {

            // conn.createStatement().execute("SET search_path TO bfsimf_clean;");

            System.out.println("Seeding data into bfsimf_clean...");

            seedRoles(conn);
            seedUsers(conn);
            seedProfiles(conn);
            seedMutualFunds(conn);
            seedInvestorPortfolioAndTransactions(conn);
            seedScenarios(conn);
            seedScenarioImpactResults(conn);
            seedEvaluations(conn);
            seedComplaints(conn);
            seedNotifications(conn);

            System.out.println("✅ Data seeding completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================================================
       ROLES
       ========================================================= */
    private static void seedRoles(Connection conn) throws Exception {

        String sql = """
            INSERT IGNORE INTO user_role (role_id, role_name) VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            insert(ps, "ADMIN", "Admin");
            insert(ps, "INVESTOR", "Investor");
            insert(ps, "BUSINESS_ANALYST", "Business Analyst");
            insert(ps, "PORTFOLIO_MANAGER", "Portfolio Manager");
            insert(ps, "OPERATIONS", "Operations Team");
            insert(ps, "COMPLAINTS_MANAGER", "Complaints Manager");
        }
    }

    /* =========================================================
       USERS
       ========================================================= */
    private static void seedUsers(Connection conn) throws Exception {

        String sql = """
            INSERT IGNORE INTO app_user
            (user_id, user_name, email, password_hash, role_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            insert(ps, "ADMIN001", "Admin User", "admin@mail.com", "admin123", "ADMIN");
            insert(ps, "PM001", "Portfolio Manager", "pm@mail.com", "pm123", "PORTFOLIO_MANAGER");
            insert(ps, "BA001", "Business Analyst", "ba@mail.com", "ba123", "BUSINESS_ANALYST");
            insert(ps, "OPS001", "Operations User", "ops@mail.com", "ops123", "OPERATIONS");
            insert(ps, "CMP001", "Complaints Manager", "cmp@mail.com", "cmp123", "COMPLAINTS_MANAGER");
            insert(ps, "INV001", "Investor One", "investor@mail.com", "inv123", "INVESTOR");
        }
    }

    /* =========================================================
       USER PROFILES (ALL ROLES)
       ========================================================= */
    private static void seedProfiles(Connection conn) throws Exception {

        String sql = """
            INSERT IGNORE INTO user_profile
            (user_id, first_name, last_name, email, mobile, dob, pan, current_address, permanent_address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            insert(ps, "ADMIN001", "System", "Admin", "admin@mail.com", "9000000001",
                    Date.valueOf("1980-01-01"), "ADMINP001A", "Head Office", "Head Office");

            insert(ps, "PM001", "Rahul", "Sharma", "pm@mail.com", "9000000002",
                    Date.valueOf("1985-06-15"), "PMPAN001A", "Mumbai", "Mumbai");

            insert(ps, "BA001", "Anita", "Verma", "ba@mail.com", "9000000003",
                    Date.valueOf("1990-02-20"), "BAPAN001A", "Bengaluru", "Bengaluru");

            insert(ps, "OPS001", "Karan", "Mehta", "ops@mail.com", "9000000004",
                    Date.valueOf("1988-09-10"), "OPSPAN001A", "Hyderabad", "Hyderabad");

            insert(ps, "CMP001", "Neha", "Singh", "cmp@mail.com", "9000000005",
                    Date.valueOf("1987-11-25"), "CMPPAN001A", "Pune", "Pune");

            insert(ps, "INV001", "Amit", "Patel", "investor@mail.com", "9000000006",
                    Date.valueOf("1992-03-18"), "INVPAN001A", "Ahmedabad", "Ahmedabad");
        }
    }

    /* =========================================================
       MUTUAL FUNDS (10 REAL TATA FUNDS)
       ========================================================= */
    private static void seedMutualFunds(Connection conn) throws Exception {

        String sql = """
            INSERT IGNORE INTO mutualfund_product
            (fund_id, fund_name, category_name, nav_level,
             risk, status, promotion_status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            insert(ps, "MF001", "Tata Digital India Fund", "Equity", 45.21, "High", "ACTIVE", "PROMOTED", today());
            insert(ps, "MF002", "Tata Large Cap Fund", "Equity", 320.55, "High", "ACTIVE", "NORMAL", today());
            insert(ps, "MF003", "Tata Flexi Cap Fund", "Equity", 98.40, "High", "ACTIVE", "NORMAL", today());
            insert(ps, "MF004", "Tata Equity P/E Fund", "Equity", 215.75, "Medium", "INACTIVE", "NORMAL", today());

            insert(ps, "MF005", "Tata Short Term Bond Fund", "Debt", 52.30, "Low", "ACTIVE", "NORMAL", today());
            insert(ps, "MF006", "Tata Corporate Bond Fund", "Debt", 110.60, "Low", "ACTIVE", "NORMAL", today());
            insert(ps, "MF007", "Tata Money Market Fund", "Debt", 101.10, "Low", "ACTIVE", "NORMAL", today());

            insert(ps, "MF008", "Tata Balanced Advantage Fund", "Hybrid", 75.80, "Medium", "ACTIVE", "NORMAL", today());
            insert(ps, "MF009", "Tata Hybrid Equity Fund", "Hybrid", 165.25, "Medium", "ACTIVE", "NORMAL", today());
            insert(ps, "MF010", "Tata Multi Asset Opportunities Fund", "Hybrid", 130.90, "Medium", "ACTIVE", "NORMAL", today());
        }
    }

    /* =========================================================
       INVESTOR PORTFOLIO & TRANSACTIONS
       ========================================================= */
    private static void seedInvestorPortfolioAndTransactions(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO investor_portfolio
            (portfolio_id, investor_id, fund_id, unit_balance, current_value, purchase_date)
            VALUES
            ('PORT001', 'INV001', 'MF001', 50, 2260.50, CURRENT_DATE())
        """).executeUpdate();

        conn.prepareStatement("""
            INSERT IGNORE INTO transactions
            (txn_id, investor_id, fund_id, txn_type, amount, payment_mode, status, txn_date)
            VALUES
            ('TXN001', 'INV001', 'MF001', 'INVEST', 5000, 'CARD', 'SUCCESS', CURRENT_DATE()),
            ('TXN002', 'INV001', 'MF001', 'WITHDRAW', 1000, 'CARD', 'SUCCESS', CURRENT_DATE())
        """).executeUpdate();
    }

    /* =========================================================
       SCENARIOS
       ========================================================= */
    private static void seedScenarios(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO scenario_analysis
            (scenario_id, scenario_name, scenario_date, status, action, description)
            VALUES
            ('SC001', 'Inflation Scenario', CURRENT_DATE(), 'ACTIVE', 'HOLD', 'Impact of inflation'),
            ('SC002', 'Recession Scenario', CURRENT_DATE(), 'IN_PROGRESS', 'WAIT', 'Impact of recession')
        """).executeUpdate();
    }

    /* =========================================================
       SCENARIO IMPACT RESULTS
       ========================================================= */
    private static void seedScenarioImpactResults(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO scenario_impact_result
            (impact_id, scenario_id, fund_id, risk_impact,
             stability_score, recommendation, analysis_data, approved)
            VALUES
            ('IMP001', 'SC001', 'MF001', 0.85, 0.60, 'HOLD',
             'Moderate risk under inflation', FALSE),
            ('IMP002', 'SC001', 'MF001', 0.85, 0.60, 'HOLD',
             'Approved analysis', TRUE)
        """).executeUpdate();
    }

    /* =========================================================
       EVALUATION & REQUESTS
       ========================================================= */
    private static void seedEvaluations(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO data_evaluation
            (request_id, request_to_role, scenario_id, status)
            VALUES
            ('REQ001', 'PORTFOLIO_MANAGER', 'SC001', 'PENDING'),
            ('REQ002', 'ADMIN', 'SC001', 'APPROVED')
        """).executeUpdate();
    }

    /* =========================================================
       COMPLAINTS
       ========================================================= */
    private static void seedComplaints(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO complaints
            (complaint_id, investor_id, category, status, raised_date, priority)
            VALUES
            ('CMP001', 'INV001', 'Transaction Issue', 'OPEN', CURRENT_DATE(), 'HIGH')
        """).executeUpdate();

        conn.prepareStatement("""
            INSERT IGNORE INTO complaint_details
            (detail_id, complaint_id, description, resolution)
            VALUES
            ('CMPD001', 'CMP001', 'Transaction failed', NULL)
        """).executeUpdate();
    }

    /* =========================================================
       NOTIFICATIONS
       ========================================================= */
    private static void seedNotifications(Connection conn) throws Exception {

        conn.prepareStatement("""
            INSERT IGNORE INTO notification
            (notification_id, user_id, message, type, status)
            VALUES
            ('NOTIF001', 'INV001', 'Investment successful', 'TRANSACTION_SUCCESS', 'UNREAD'),
            ('NOTIF002', 'PM001', 'Evaluation pending approval', 'EVALUATION_PENDING', 'UNREAD')
        """).executeUpdate();
    }

    /* =========================================================
       HELPER METHODS
       ========================================================= */
    private static void insert(PreparedStatement ps, Object... values) throws Exception {
        for (int i = 0; i < values.length; i++) {
            ps.setObject(i + 1, values[i]);
        }
        ps.executeUpdate();
    }

    private static Date today() {
        return new Date(System.currentTimeMillis());
    }
}