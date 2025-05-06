package com.monniserver.repository;

import com.monniserver.entity.History;
import com.monniserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {
    List<History> findByUserId(UUID userId);
}
