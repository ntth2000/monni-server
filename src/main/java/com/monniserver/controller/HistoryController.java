package com.monniserver.controller;

import com.monniserver.dto.HistoryDTO;
import com.monniserver.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor

public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{userId}")
    public List<HistoryDTO> getHistory(@PathVariable UUID userId) {
        return historyService.findByUserId(userId);
    }

    @PostMapping("/{userId}")
    public void addHistory(@PathVariable UUID userId, @RequestBody HistoryDTO historyDTO) {

    }
}
