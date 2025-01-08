package org.system.bank.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*export interface DashboardStats {
    totalUsers: number;
    activeUsers: number;
    pendingRequests: number;
    totalTransactions: number;
    lastUpdated: Date;
}*/
@Setter
@Getter
public class DashboardStats {
    // Getters and setters
    private int totalUsers;
    private int activeUsers;
    private int pendingRequests;
    private int totalTransactions;
    private Date lastUpdated;

    // Builder
    public static DashboardStatsBuilder builder() {
        return new DashboardStatsBuilder();
    }
    public static class DashboardStatsBuilder {
        private int totalUsers;
        private int activeUsers;
        private int pendingRequests;
        private int totalTransactions;
        private Date lastUpdated;
        public DashboardStatsBuilder totalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }
        public DashboardStatsBuilder activeUsers(int activeUsers) {
            this.activeUsers = activeUsers;
            return this;
        }
        public DashboardStatsBuilder pendingRequests(int pendingRequests) {
            this.pendingRequests = pendingRequests;
            return this;
        }
        public DashboardStatsBuilder totalTransactions(int totalTransactions) {
            this.totalTransactions = totalTransactions;
            return this;
        }
        public DashboardStatsBuilder lastUpdated(Date lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        public DashboardStats build() {
            DashboardStats dashboardStats = new DashboardStats();
            dashboardStats.setTotalUsers(totalUsers);
            dashboardStats.setActiveUsers(activeUsers);
            dashboardStats.setPendingRequests(pendingRequests);
            dashboardStats.setTotalTransactions(totalTransactions);
            dashboardStats.setLastUpdated(lastUpdated);
            return dashboardStats;
        }
    }
}
