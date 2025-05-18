package com.monniserver.dto;

public class ConversationRequest {
    private String question;

    public ConversationRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
