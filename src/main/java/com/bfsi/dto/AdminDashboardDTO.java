package com.bfsi.dto;

/**
 * Dashboard metrics for Admin
 * ✅ UPDATED: added resolvedComplaints field
 */
public class AdminDashboardDTO {

    private int totalFunds;
    private int activeUsers;
    private int pendingComplaints;
    private int resolvedComplaints;   // ✅ NEW
    private int pendingRequests;

    public AdminDashboardDTO() {}

    public int getTotalFunds()           { return totalFunds; }
    public int getActiveUsers()          { return activeUsers; }
    public int getPendingComplaints()    { return pendingComplaints; }
    public int getResolvedComplaints()   { return resolvedComplaints; }   // ✅ NEW
    public int getPendingRequests()      { return pendingRequests; }

    public void setTotalFunds(int v)          { this.totalFunds = v; }
    public void setActiveUsers(int v)         { this.activeUsers = v; }
    public void setPendingComplaints(int v)   { this.pendingComplaints = v; }
    public void setResolvedComplaints(int v)  { this.resolvedComplaints = v; }  // ✅ NEW
    public void setPendingRequests(int v)     { this.pendingRequests = v; }
}
