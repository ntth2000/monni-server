package com.monniserver.dto;

import java.util.List;

public class UpdateSpendingResult {
    private List<SpendingItem[]> successfulUpdates;
    private List<SpendingItem> notFoundSpendings;

    public UpdateSpendingResult() {
    }

    public UpdateSpendingResult(List<SpendingItem[]> successfulUpdates, List<SpendingItem> notFoundSpendings) {
        this.successfulUpdates = successfulUpdates;
        this.notFoundSpendings = notFoundSpendings;
    }

    public List<SpendingItem[]> getSuccessfulUpdates() {
        return successfulUpdates;
    }

    public void setSuccessfulUpdates(List<SpendingItem[]> successfulUpdates) {
        this.successfulUpdates = successfulUpdates;
    }

    public List<SpendingItem> getNotFoundSpendings() {
        return notFoundSpendings;
    }

    public void setNotFoundSpendings(List<SpendingItem> notFoundSpendings) {
        this.notFoundSpendings = notFoundSpendings;
    }
}