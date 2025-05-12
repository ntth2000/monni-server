package com.monniserver.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

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
