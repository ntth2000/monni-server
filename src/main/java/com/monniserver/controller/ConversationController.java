package com.monniserver.controller;

import com.monniserver.config.JwtUtil;
import com.monniserver.dto.ConversationRequest;
import com.monniserver.dto.ConversationResponse;
import com.monniserver.service.ConversationService;
import com.monniserver.service.OpenAIService;
import com.openai.models.chat.completions.ChatCompletion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")

public class ConversationController {
    private final ConversationService conversationService;
    private final JwtUtil jwtUtil;
    private final OpenAIService openAIService;

    public ConversationController(ConversationService conversationService, JwtUtil jwtUtil, OpenAIService openAIService) {
        this.conversationService = conversationService;
        this.jwtUtil = jwtUtil;
        this.openAIService = openAIService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversation(@RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(conversationService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<String> addConversation(@RequestBody ConversationRequest request,
                                                          @RequestHeader("Authorization") String authHeader){
        UUID userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        System.out.println("User id: " + userId);
        return ResponseEntity.ok(openAIService.handleUserRequest(request.getQuestion()));
    }
}
