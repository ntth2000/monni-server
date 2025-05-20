package com.monniserver.dto;

import com.monniserver.entity.Spending;

import java.util.List;

public class DeleteSpendingResult {
    private List<Spending> successfulDeletions;
    private List<SpendingItem> notFoundSpendings;

    public DeleteSpendingResult() {
    }

    public DeleteSpendingResult(List<Spending> successfulDeletions, List<SpendingItem> notFoundSpendings) {
        this.successfulDeletions = successfulDeletions;
        this.notFoundSpendings = notFoundSpendings;
    }

    public List<Spending> getSuccessfulDeletions() {
        return successfulDeletions;
    }

    public void setSuccessfulDeletions(List<Spending> successfulDeletions) {
        this.successfulDeletions = successfulDeletions;
    }

    public List<SpendingItem> getNotFoundSpendings() {
        return notFoundSpendings;
    }

    public void setNotFoundSpendings(List<SpendingItem> notFoundSpendings) {
        this.notFoundSpendings = notFoundSpendings;
    }
}