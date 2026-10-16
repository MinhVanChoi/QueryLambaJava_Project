package com.fastfood.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
public class DashboardStatsDTO {
    private long totalBranches;
    private long totalEmployees;
    private long totalActiveAccounts;
    private long totalCustomers;
    private long totalCategories;
    private long totalFoodItems;
    private long totalOrders;
    private long totalCompletedOrders;
    private BigDecimal totalCompletedRevenue;
    private double averageOrderValue;
    private long lowStockCount;
    private Map<String, Long> ordersByStatus;
    private Map<String, BigDecimal> revenueByPaymentMethod;
    private Map<String, BigDecimal> revenueByBranch;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long totalBranches, long totalEmployees, long totalActiveAccounts, long totalCustomers,
                             long totalCategories, long totalFoodItems, long totalOrders, long totalCompletedOrders,
                             BigDecimal totalCompletedRevenue, double averageOrderValue, long lowStockCount,
                             Map<String, Long> ordersByStatus, Map<String, BigDecimal> revenueByPaymentMethod,
                             Map<String, BigDecimal> revenueByBranch) {
        this.totalBranches = totalBranches;
        this.totalEmployees = totalEmployees;
        this.totalActiveAccounts = totalActiveAccounts;
        this.totalCustomers = totalCustomers;
        this.totalCategories = totalCategories;
        this.totalFoodItems = totalFoodItems;
        this.totalOrders = totalOrders;
        this.totalCompletedOrders = totalCompletedOrders;
        this.totalCompletedRevenue = totalCompletedRevenue;
        this.averageOrderValue = averageOrderValue;
        this.lowStockCount = lowStockCount;
        this.ordersByStatus = ordersByStatus;
        this.revenueByPaymentMethod = revenueByPaymentMethod;
        this.revenueByBranch = revenueByBranch;
    }
}
