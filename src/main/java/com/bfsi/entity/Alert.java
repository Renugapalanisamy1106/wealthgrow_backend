package com.bfsi.entity;

public class Alert {

    private String alertId;
    private String txnId;
    private String alertType;
    private String issueCategory;
    private String remarks;

    public Alert() {
    }

    public Alert(String alertId, String txnId,
                 String alertType, String issueCategory, String remarks) {
        this.alertId = alertId;
        this.txnId = txnId;
        this.alertType = alertType;
        this.issueCategory = issueCategory;
        this.remarks = remarks;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getIssueCategory() {
        return issueCategory;
    }

    public void setIssueCategory(String issueCategory) {
        this.issueCategory = issueCategory;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}