package com.monniserver.dto;

import com.monniserver.entity.Spending;

import java.util.List;

public class UpdateSpendingResult {
    private List<Spending> successfulUpdates;
    private List<SpendingItem> notFoundSpendings;

    public UpdateSpendingResult() {
    }

    public UpdateSpendingResult(List<Spending> successfulUpdates, List<SpendingItem> notFoundSpendings) {
        this.successfulUpdates = successfulUpdates;
        this.notFoundSpendings = notFoundSpendings;
    }

    public List<Spending> getSuccessfulUpdates() {
        return successfulUpdates;
    }

    public List<SpendingItem> getNotFoundSpendings() {
        return notFoundSpendings;
    }
}