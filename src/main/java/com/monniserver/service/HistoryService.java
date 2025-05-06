package com.monniserver.service;

import com.monniserver.dto.HistoryDTO;
import com.monniserver.entity.History;
import com.monniserver.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public List<HistoryDTO> findByUserId(UUID userId) {
        List<History> histories = historyRepository.findByUserId(userId);
        return histories.stream()
            .map(history -> new HistoryDTO(
                    history.getId(),
                    history.getQuestion(),
                    history.getAnswer(),
                    history.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    public void addHistory(History history) {
        historyRepository.save(history);
    }
}
