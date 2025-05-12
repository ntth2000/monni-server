package com.monniserver.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConversationResponse {
    private UUID id;
    private String question;
    private String answer;
    private ZonedDateTime createdAt;

    public ConversationResponse(UUID id, String question, String answer, ZonedDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}