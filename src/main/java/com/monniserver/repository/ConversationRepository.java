package com.monniserver.repository;

import com.monniserver.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT h FROM Conversation h WHERE h.user.id = :userId ORDER BY h.createdAt ASC")
    List<Conversation> findByUserIdOrderByCreatedAtAsc(UUID userId);

    List<Conversation> findTop3ByUserIdOrderByCreatedAtDesc(UUID userId);
}
