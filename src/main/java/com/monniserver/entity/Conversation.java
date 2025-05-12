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

    @Column(name = "answer", nullable = false)
    private String answer;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    public Conversation(User user, String question, String answer, ZonedDateTime createdAt) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public Conversation() {}

    public UUID getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
}
