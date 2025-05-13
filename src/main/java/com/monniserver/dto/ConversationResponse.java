package com.monniserver.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConversationResponse {
    private UUID id;
    private String question;
    private String answer;
    private ZonedDateTime createdAt;

    public ConversationResponse() {}

    public ConversationResponse(UUID id, String question, String answer, ZonedDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}