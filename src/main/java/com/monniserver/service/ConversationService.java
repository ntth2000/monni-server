package com.monniserver.service;

import com.monniserver.dto.ConversationResponse;
import com.monniserver.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public List<ConversationResponse> findByUserId(UUID userId) {
        System.out.println("Finding conversations for user: " + userId);
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(h -> new ConversationResponse(h.getId(), h.getQuestion(), h.getAnswer(), h.getCreatedAt()))
                .toList();
    }
}
