package com.monniserver.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String aiResponsesJson; // chứa list<OpenAIResponse>

    public Conversation() {
    }

    public Conversation(User user, String question) {
        this.user = user;
        this.question = question;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAiResponsesJson() {
        return aiResponsesJson;
    }

    public void setAiResponsesJson(String aiResponsesJson) {
        this.aiResponsesJson = aiResponsesJson;
    }
}

