package com.monniserver.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class HistoryDTO {
    private UUID id;
    private String question;
    private String answer;
    private ZonedDateTime createdAt;

    public HistoryDTO(UUID id, String question, String answer, ZonedDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }
}