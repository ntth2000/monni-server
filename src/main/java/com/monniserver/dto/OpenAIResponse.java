package com.monniserver.dto;

import java.util.List;

public class OpenAIResponse {
    private String intent;

    private List<SpendingItem> detailSpendings;          // for add_spending
    private List<SpendingItem> needUpdatedSpendings;     // for update_spending
    private List<SpendingItem> updatedSpendings;         // for update_spending
    private List<SpendingItem> deleteSpendings;          // for delete_spending

    private String startDate;
    private String endDate;
    private String compareToStartDate;
    private String compareToEndDate;

    private Double originalAmount;     // optional fallback for update/delete
    private Double targetAmount;

    private String category;
    private String description;
    private String date;

    // Getters and Setters

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<SpendingItem> getDetailSpendings() {
        return detailSpendings;
    }

    public void setDetailSpendings(List<SpendingItem> detailSpendings) {
        this.detailSpendings = detailSpendings;
    }

    public List<SpendingItem> getNeedUpdatedSpendings() {
        return needUpdatedSpendings;
    }

    public void setNeedUpdatedSpendings(List<SpendingItem> needUpdatedSpendings) {
        this.needUpdatedSpendings = needUpdatedSpendings;
    }

    public List<SpendingItem> getUpdatedSpendings() {
        return updatedSpendings;
    }

    public void setUpdatedSpendings(List<SpendingItem> updatedSpendings) {
        this.updatedSpendings = updatedSpendings;
    }

    public List<SpendingItem> getDeleteSpendings() {
        return deleteSpendings;
    }

    public void setDeleteSpendings(List<SpendingItem> deleteSpendings) {
        this.deleteSpendings = deleteSpendings;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCompareToStartDate() {
        return compareToStartDate;
    }

    public void setCompareToStartDate(String compareToStartDate) {
        this.compareToStartDate = compareToStartDate;
    }

    public String getCompareToEndDate() {
        return compareToEndDate;
    }

    public void setCompareToEndDate(String compareToEndDate) {
        this.compareToEndDate = compareToEndDate;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}