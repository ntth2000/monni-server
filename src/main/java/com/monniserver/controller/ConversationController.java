package com.monniserver.controller;

import com.monniserver.config.JwtUtil;
import com.monniserver.dto.ConversationRequest;
import com.monniserver.dto.ConversationResponse;
import com.monniserver.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")

public class ConversationController {
    private final ConversationService conversationService;
    private final JwtUtil jwtUtil;

    public ConversationController(ConversationService conversationService, JwtUtil jwtUtil) {
        this.conversationService = conversationService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversation(@RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        return ResponseEntity.ok(conversationService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> addConversation(@RequestBody ConversationRequest request,
                                                                @RequestHeader("Authorization") String authHeader) throws Exception {
        UUID userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));

        return ResponseEntity.ok(conversationService.addNewQuestion(request.getQuestion(), userId));
    }
}
