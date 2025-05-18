package com.monniserver.dto;

import java.util.List;

public class DeleteSpendingResult {
    private List<SpendingItem> successfulDeletions;
    private List<SpendingItem> notFoundSpendings;

    public DeleteSpendingResult() {
    }

    public DeleteSpendingResult(List<SpendingItem> successfulDeletions, List<SpendingItem> notFoundSpendings) {
        this.successfulDeletions = successfulDeletions;
        this.notFoundSpendings = notFoundSpendings;
    }

    public List<SpendingItem> getSuccessfulDeletions() {
        return successfulDeletions;
    }

    public void setSuccessfulDeletions(List<SpendingItem> successfulDeletions) {
        this.successfulDeletions = successfulDeletions;
    }

    public List<SpendingItem> getNotFoundSpendings() {
        return notFoundSpendings;
    }

    public void setNotFoundSpendings(List<SpendingItem> notFoundSpendings) {
        this.notFoundSpendings = notFoundSpendings;
    }
}