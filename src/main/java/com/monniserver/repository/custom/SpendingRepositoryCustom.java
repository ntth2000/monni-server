package com.monniserver.repository.custom;

import com.monniserver.entity.Spending;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpendingRepositoryCustom {
    List<Spending> findByUserIdAndOptionalFields(UUID userId, LocalDate date, String category, String description, Double amount);
}
