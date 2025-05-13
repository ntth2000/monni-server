package com.monniserver.service;

import com.monniserver.dto.ConversationResponse;
import com.monniserver.dto.OpenAIResponse;
import com.monniserver.entity.Conversation;
import com.monniserver.entity.User;
import com.monniserver.repository.ConversationRepository;
import com.monniserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final OpenAIService openAIService;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository, OpenAIService openAIService, UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.openAIService = openAIService;
        this.userRepository = userRepository;
    }

    public List<ConversationResponse> findByUserId(UUID userId) {
        System.out.println("Finding conversations for user: " + userId);
        return conversationRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(h -> new ConversationResponse(h.getId(), h.getQuestion(), h.getAnswer(), h.getCreatedAt()))
                .toList();
    }

    public ConversationResponse addNewQuestion(String question, UUID userId) {
        Optional<OpenAIResponse> response = openAIService.handleUserRequest(question);
        ConversationResponse conversationResponse = new ConversationResponse();
        conversationResponse.setCreatedAt(ZonedDateTime.now());
        conversationResponse.setQuestion(question);

        if (response.isPresent()){
            OpenAIResponse userRequest = response.get();
            String intent = userRequest.getIntent();

            if ("greeting".equals(intent) || "unknown".equals(intent)) {
                conversationResponse.setAnswer(userRequest.getMessage());

            } else {
                conversationResponse.setAnswer("");
            }
        } else {
            conversationResponse.setAnswer("Xin lỗi, không hiểu yêu cầu của bạn.");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Conversation newConversation = new Conversation(user, conversationResponse.getQuestion(), conversationResponse.getAnswer(), conversationResponse.getCreatedAt());
        conversationRepository.save(newConversation);
        conversationResponse.setId(newConversation.getId());
        return conversationResponse;
    }
}
