package com.monniserver.repository;

import com.monniserver.entity.Spending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpendingRepository extends JpaRepository<Spending, UUID> {
    void deleteById(UUID id);

    List<Spending> findByUserId(UUID userId);
}
