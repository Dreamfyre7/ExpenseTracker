package com.xalts.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MonthlyReportResponse {
    private String month;
    private double total;
    private Map<String, Double> categoryBreakdown;
	public MonthlyReportResponse(String month, double total, Map<String, Double> breakdown) {
		this.month = month;
		this.total = total;
		this.categoryBreakdown = breakdown;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Map<String, Double> getCategoryBreakdown() {
		return categoryBreakdown;
	}
	public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) {
		this.categoryBreakdown = categoryBreakdown;
	}
    
}

