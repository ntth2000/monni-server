package com.monniserver.dto;

public class SpendingRequest {
    private String intent;
    private long amount;
    private String category;
    private String description;
    private String date;
    private String target_description;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public String getTarget_description() {
        return target_description;
    }

    public void setTarget_description(String target_description) {
        this.target_description = target_description;
    }


}
