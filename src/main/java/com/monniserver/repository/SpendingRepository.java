package com.monniserver.repository;

import com.monniserver.entity.Spending;
import com.monniserver.repository.custom.SpendingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpendingRepository extends JpaRepository<Spending, UUID>, SpendingRepositoryCustom {
    void deleteById(UUID id);

    @Query("""
                SELECT s FROM Spending s
                WHERE s.user.id = :userId
                  AND s.date BETWEEN :startDate AND :endDate
                  AND (:category IS NULL OR s.category = :category)
                  AND (:description IS NULL OR s.description LIKE %:description%)
            """)
    List<Spending> findFilteredSpending(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") String category,
            @Param("description") String description
    );

    List<Spending> findByUserIdAndOptionalFields(
            @Param("userId") UUID userId,
            @Param("date") LocalDate date,
            @Param("category") String category,
            @Param("description") String description,
            @Param("amount") Double amount
    );

}
