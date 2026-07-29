-- MySQL Database Seed Data for Mutual Funds System
-- Schema name: bfsimf_clean


-- Disable foreign key checks for clean seeding
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate existing data
TRUNCATE TABLE alerts;
TRUNCATE TABLE unit_allocation;
TRUNCATE TABLE transactions;
TRUNCATE TABLE investor_portfolio;
TRUNCATE TABLE nav_calculation;
TRUNCATE TABLE fund_risk_analysis;
TRUNCATE TABLE scenario_nav_series;
TRUNCATE TABLE scenario_impact_result;
TRUNCATE TABLE evaluation;
TRUNCATE TABLE data_evaluation;
TRUNCATE TABLE scenario_analysis;
TRUNCATE TABLE complaint_details;
TRUNCATE TABLE complaints;
TRUNCATE TABLE complaint_view;
TRUNCATE TABLE notification;
TRUNCATE TABLE user_profile;
TRUNCATE TABLE app_user;
TRUNCATE TABLE user_role;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Seed user_role
INSERT INTO user_role (role_id, role_name) VALUES
('ADMIN', 'Admin'),
('INVESTOR', 'Investor'),
('BUSINESS_ANALYST', 'Business Analyst'),
('PORTFOLIO_MANAGER', 'Portfolio Manager'),
('OPERATIONS', 'Operations Team'),
('COMPLAINTS_MANAGER', 'Complaints Manager');

-- 2. Seed app_user (Using passwords from UpdateUserPasswords.java for local login)
INSERT INTO app_user (user_id, user_name, email, password_hash, role_id) VALUES
('ADMIN001', 'Admin User', 'admin@mail.com', 'admin@123', 'ADMIN'),
('PM001', 'Portfolio Manager', 'pm@mail.com', 'pm@12345', 'PORTFOLIO_MANAGER'),
('BA001', 'Business Analyst', 'ba@mail.com', 'ba@12345', 'BUSINESS_ANALYST'),
('OPS001', 'Operations User', 'ops@mail.com', 'ops@12345', 'OPERATIONS'),
('CMP001', 'Complaints Manager', 'cmp@mail.com', 'cmp@12345', 'COMPLAINTS_MANAGER'),
('INV001', 'Investor One', 'investor@mail.com', 'inv@12345', 'INVESTOR');

-- 3. Seed user_profile
INSERT INTO user_profile (user_id, first_name, last_name, email, mobile, dob, pan, current_address, permanent_address) VALUES
('ADMIN001', 'System', 'Admin', 'admin@mail.com', '9000000001', '1980-01-01', 'ADMINP001A', 'Head Office', 'Head Office'),
('PM001', 'Rahul', 'Sharma', 'pm@mail.com', '9000000002', '1985-06-15', 'PMPAN001A', 'Mumbai', 'Mumbai'),
('BA001', 'Anita', 'Verma', 'ba@mail.com', '9000000003', '1990-02-20', 'BAPAN001A', 'Bengaluru', 'Bengaluru'),
('OPS001', 'Karan', 'Mehta', 'ops@mail.com', '9000000004', '1988-09-10', 'OPSPAN001A', 'Hyderabad', 'Hyderabad'),
('CMP001', 'Neha', 'Singh', 'cmp@mail.com', '9000000005', '1987-11-25', 'CMPPAN001A', 'Pune', 'Pune'),
('INV001', 'Amit', 'Patel', 'investor@mail.com', '9000000006', '1992-03-18', 'INVPAN001A', 'Ahmedabad', 'Ahmedabad');

-- 4. Seed mutualfund_product (10 Real Tata Funds)
INSERT INTO mutualfund_product (fund_id, fund_name, category_name, nav_level, risk, status, promotion_status, created_at) VALUES
('MF001', 'Tata Digital India Fund', 'Equity', 45.2100, 'High', 'ACTIVE', 'PROMOTED', CURRENT_DATE()),
('MF002', 'Tata Large Cap Fund', 'Equity', 320.5500, 'High', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF003', 'Tata Flexi Cap Fund', 'Equity', 98.4000, 'High', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF004', 'Tata Equity P/E Fund', 'Equity', 215.7500, 'Medium', 'INACTIVE', 'NORMAL', CURRENT_DATE()),
('MF005', 'Tata Short Term Bond Fund', 'Debt', 52.3000, 'Low', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF006', 'Tata Corporate Bond Fund', 'Debt', 110.6000, 'Low', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF007', 'Tata Money Market Fund', 'Debt', 101.1000, 'Low', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF008', 'Tata Balanced Advantage Fund', 'Hybrid', 75.8000, 'Medium', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF009', 'Tata Hybrid Equity Fund', 'Hybrid', 165.2500, 'Medium', 'ACTIVE', 'NORMAL', CURRENT_DATE()),
('MF010', 'Tata Multi Asset Opportunities Fund', 'Hybrid', 130.9000, 'Medium', 'ACTIVE', 'NORMAL', CURRENT_DATE());

-- 5. Seed investor_portfolio
INSERT INTO investor_portfolio (portfolio_id, investor_id, fund_id, unit_balance, current_value, purchase_date) VALUES
('PORT001', 'INV001', 'MF001', 50, 2260.50, CURRENT_DATE());

-- 6. Seed transactions
INSERT INTO transactions (txn_id, investor_id, fund_id, txn_type, amount, payment_mode, status, txn_date) VALUES
('TXN001', 'INV001', 'MF001', 'INVEST', 5000.00, 'CARD', 'SUCCESS', CURRENT_DATE()),
('TXN002', 'INV001', 'MF001', 'WITHDRAW', 1000.00, 'CARD', 'SUCCESS', CURRENT_DATE());

-- 7. Seed scenario_analysis
INSERT INTO scenario_analysis (scenario_id, scenario_name, scenario_date, status, action, description) VALUES
('SC001', 'Inflation Scenario', CURRENT_DATE(), 'ACTIVE', 'HOLD', 'Impact of inflation'),
('SC002', 'Recession Scenario', CURRENT_DATE(), 'IN_PROGRESS', 'WAIT', 'Impact of recession');

-- 8. Seed scenario_impact_result
INSERT INTO scenario_impact_result (impact_id, scenario_id, fund_id, risk_impact, stability_score, recommendation, analysis_data, approved, sharpe_ratio, max_drawdown, bounce_velocity) VALUES
('IMP001', 'SC001', 'MF001', 0.85, 0.60, 'HOLD', 'Moderate risk under inflation', FALSE, 1.25, 0.15, 0.45),
('IMP002', 'SC001', 'MF001', 0.85, 0.60, 'HOLD', 'Approved analysis', TRUE, 1.30, 0.12, 0.50);

-- 9. Seed data_evaluation (request_id mapped to evaluationId in DataEvaluation entity)
INSERT INTO data_evaluation (request_id, request_to_role, scenario_id, status, created_at, submitted_by, admin_remarks) VALUES
('REQ001', 'PORTFOLIO_MANAGER', 'SC001', 'PENDING', CURRENT_TIMESTAMP(), 'BA001', NULL),
('REQ002', 'ADMIN', 'SC001', 'APPROVED', CURRENT_TIMESTAMP(), 'BA001', 'Looks good');

-- 10. Seed complaints
INSERT INTO complaints (complaint_id, investor_id, category, status, raised_date, priority, preferred_language) VALUES
('CMP001', 'INV001', 'Transaction Issue', 'OPEN', CURRENT_DATE(), 'HIGH', 'English');

-- 11. Seed complaint_details
INSERT INTO complaint_details (detail_id, complaint_id, description, resolution) VALUES
('CMPD001', 'CMP001', 'Transaction failed', NULL);

-- 12. Seed complaint_view (matching SchemaCreator schema)
INSERT INTO complaint_view (complaint_id, investor_name, category, status, created_at) VALUES
('CMP001', 'Investor One', 'Transaction Issue', 'OPEN', CURRENT_DATE());

-- 13. Seed notification
INSERT INTO notification (notification_id, user_id, message, type, status, created_at) VALUES
('NOTIF001', 'INV001', 'Investment successful', 'TRANSACTION_SUCCESS', 'UNREAD', CURRENT_TIMESTAMP()),
('NOTIF002', 'PM001', 'Evaluation pending approval', 'EVALUATION_PENDING', 'UNREAD', CURRENT_TIMESTAMP());
