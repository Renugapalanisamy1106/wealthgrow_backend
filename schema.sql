-- MySQL Database Schema for Mutual Funds System
-- Schema name: bfsimf_clean (matched to JPA @Table mappings)

CREATE DATABASE IF NOT EXISTS bfsimf_clean;
USE bfsimf_clean;

-- Drop tables in reverse order of foreign key dependency
DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS unit_allocation;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS investor_portfolio;
DROP TABLE IF EXISTS nav_calculation;
DROP TABLE IF EXISTS fund_risk_analysis;
DROP TABLE IF EXISTS scenario_nav_series;
DROP TABLE IF EXISTS scenario_impact_result;
DROP TABLE IF EXISTS evaluation;
DROP TABLE IF EXISTS data_evaluation;
DROP TABLE IF EXISTS scenario_analysis;
DROP TABLE IF EXISTS complaint_details;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS complaint_view;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS user_role;

-- 1. user_role Table
CREATE TABLE user_role (
    role_id VARCHAR(30) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. app_user Table
CREATE TABLE app_user (
    user_id VARCHAR(30) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id VARCHAR(30),
    UNIQUE KEY uq_app_user_email (email),
    CONSTRAINT fk_app_user_role FOREIGN KEY (role_id) REFERENCES user_role (role_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. user_profile Table
CREATE TABLE user_profile (
    user_id VARCHAR(30) PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    mobile VARCHAR(20),
    dob DATE,
    pan VARCHAR(20),
    current_address TEXT,
    permanent_address VARCHAR(500),
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES app_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. notification Table
CREATE TABLE notification (
    notification_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(30),
    message TEXT,
    type VARCHAR(50),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES app_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. mutualfund_product Table
CREATE TABLE mutualfund_product (
    fund_id VARCHAR(30) PRIMARY KEY,
    fund_name VARCHAR(150),
    category_name VARCHAR(50),
    nav_level DECIMAL(12,4),
    risk VARCHAR(50),
    status VARCHAR(20),
    promotion_status VARCHAR(20),
    created_at DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. nav_calculation Table
CREATE TABLE nav_calculation (
    nav_id VARCHAR(50) PRIMARY KEY,
    fund_id VARCHAR(30),
    nav_value DECIMAL(12,4),
    calculated_on DATE,
    CONSTRAINT fk_nav_calc_fund FOREIGN KEY (fund_id) REFERENCES mutualfund_product (fund_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. investor_portfolio Table
CREATE TABLE investor_portfolio (
    portfolio_id VARCHAR(50) PRIMARY KEY,
    investor_id VARCHAR(30),
    fund_id VARCHAR(30),
    unit_balance INT,
    current_value DECIMAL(14,2),
    purchase_date DATE,
    CONSTRAINT fk_portfolio_investor FOREIGN KEY (investor_id) REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_portfolio_fund FOREIGN KEY (fund_id) REFERENCES mutualfund_product (fund_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. transactions Table
CREATE TABLE transactions (
    txn_id VARCHAR(50) PRIMARY KEY,
    investor_id VARCHAR(30),
    fund_id VARCHAR(30),
    txn_type VARCHAR(50),
    amount DECIMAL(14,2),
    payment_mode VARCHAR(50),
    status VARCHAR(30),
    txn_date DATE,
    CONSTRAINT fk_txn_investor FOREIGN KEY (investor_id) REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_txn_fund FOREIGN KEY (fund_id) REFERENCES mutualfund_product (fund_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. unit_allocation Table (Matched to JPA Entity mappings)
CREATE TABLE unit_allocation (
    allocation_id VARCHAR(50) PRIMARY KEY,
    transaction_id VARCHAR(50),
    units DOUBLE PRECISION,
    nav DOUBLE PRECISION,
    allocation_date DATE,
    CONSTRAINT fk_allocation_txn FOREIGN KEY (transaction_id) REFERENCES transactions (txn_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. scenario_analysis Table
CREATE TABLE scenario_analysis (
    scenario_id VARCHAR(30) PRIMARY KEY,
    scenario_name VARCHAR(100),
    scenario_date DATE,
    status VARCHAR(30),
    action VARCHAR(50),
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. scenario_impact_result Table (Matched to JPA Entity metrics)
CREATE TABLE scenario_impact_result (
    impact_id VARCHAR(50) PRIMARY KEY,
    scenario_id VARCHAR(30),
    fund_id VARCHAR(30),
    risk_impact DOUBLE PRECISION,
    stability_score DOUBLE PRECISION,
    recommendation VARCHAR(50),
    analysis_data TEXT,
    approved BOOLEAN DEFAULT FALSE,
    sharpe_ratio DOUBLE PRECISION,
    max_drawdown DOUBLE PRECISION,
    bounce_velocity DOUBLE PRECISION,
    CONSTRAINT fk_impact_scenario FOREIGN KEY (scenario_id) REFERENCES scenario_analysis (scenario_id) ON DELETE CASCADE,
    CONSTRAINT fk_impact_fund FOREIGN KEY (fund_id) REFERENCES mutualfund_product (fund_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. evaluation Table
CREATE TABLE evaluation (
    eval_id VARCHAR(50) PRIMARY KEY,
    scenario_id VARCHAR(30),
    submitted_by VARCHAR(30),
    status VARCHAR(30),
    CONSTRAINT fk_eval_scenario FOREIGN KEY (scenario_id) REFERENCES scenario_analysis (scenario_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. data_evaluation Table (Matched to JPA Entity new fields)
CREATE TABLE data_evaluation (
    request_id VARCHAR(50) PRIMARY KEY,
    request_to_role VARCHAR(50),
    scenario_id VARCHAR(30),
    status VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_by VARCHAR(50),
    admin_remarks VARCHAR(255),
    CONSTRAINT fk_data_eval_scenario FOREIGN KEY (scenario_id) REFERENCES scenario_analysis (scenario_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. complaints Table (Matched to DBMigrationScript update)
CREATE TABLE complaints (
    complaint_id VARCHAR(50) PRIMARY KEY,
    investor_id VARCHAR(30),
    category VARCHAR(50),
    status VARCHAR(30),
    raised_date DATE,
    priority VARCHAR(20),
    preferred_language VARCHAR(100),
    CONSTRAINT fk_complaint_investor FOREIGN KEY (investor_id) REFERENCES app_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. complaint_details Table
CREATE TABLE complaint_details (
    detail_id VARCHAR(50) PRIMARY KEY,
    complaint_id VARCHAR(50),
    description TEXT,
    resolution TEXT,
    CONSTRAINT fk_complaint_detail FOREIGN KEY (complaint_id) REFERENCES complaints (complaint_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. complaint_view Table
CREATE TABLE complaint_view (
    complaint_id VARCHAR(50) PRIMARY KEY,
    investor_name VARCHAR(100),
    category VARCHAR(50),
    status VARCHAR(30),
    created_at DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. scenario_nav_series Table
CREATE TABLE scenario_nav_series (
    series_id VARCHAR(50) PRIMARY KEY,
    impact_id VARCHAR(50),
    nav_value DOUBLE PRECISION,
    nav_date VARCHAR(30),
    sequence_no INT,
    CONSTRAINT fk_nav_series_impact FOREIGN KEY (impact_id) REFERENCES scenario_impact_result (impact_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. alerts Table (Matched to AlertTableCreator)
CREATE TABLE alerts (
    alert_id VARCHAR(50) PRIMARY KEY,
    txn_id VARCHAR(50),
    alert_type VARCHAR(50),
    issue_category VARCHAR(100),
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_txn FOREIGN KEY (txn_id) REFERENCES transactions (txn_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. fund_risk_analysis Table (Matched to FundRiskAnalysisTableCreator)
CREATE TABLE fund_risk_analysis (
    analysis_id VARCHAR(30) PRIMARY KEY,
    fund_id VARCHAR(30) NOT NULL,
    fund_name VARCHAR(100),
    sharpe_ratio DOUBLE PRECISION NOT NULL DEFAULT 0,
    max_drawdown DOUBLE PRECISION NOT NULL DEFAULT 0,
    stability_score DOUBLE PRECISION NOT NULL DEFAULT 0,
    volatility DOUBLE PRECISION NOT NULL DEFAULT 0,
    suggested_risk VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    submitted_by VARCHAR(50),
    created_at DATE,
    remarks TEXT,
    CONSTRAINT fk_risk_analysis_fund FOREIGN KEY (fund_id) REFERENCES mutualfund_product (fund_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PERFORMANCE INDEXES
CREATE INDEX idx_user_role_id ON app_user(role_id);
CREATE INDEX idx_profile_user_id ON user_profile(user_id);
CREATE INDEX idx_notification_user_id ON notification(user_id);
CREATE INDEX idx_nav_calc_fund_id ON nav_calculation(fund_id);
CREATE INDEX idx_portfolio_investor_fund ON investor_portfolio(investor_id, fund_id);
CREATE INDEX idx_transactions_investor_fund ON transactions(investor_id, fund_id);
CREATE INDEX idx_allocation_transaction_id ON unit_allocation(transaction_id);
CREATE INDEX idx_impact_scenario_fund ON scenario_impact_result(scenario_id, fund_id);
CREATE INDEX idx_eval_scenario_id ON evaluation(scenario_id);
CREATE INDEX idx_data_eval_scenario_id ON data_evaluation(scenario_id);
CREATE INDEX idx_complaints_investor_id ON complaints(investor_id);
CREATE INDEX idx_complaint_details_id ON complaint_details(complaint_id);
CREATE INDEX idx_nav_series_impact_id ON scenario_nav_series(impact_id);
CREATE INDEX idx_alerts_txn_id ON alerts(txn_id);
CREATE INDEX idx_risk_analysis_fund_id ON fund_risk_analysis(fund_id);
