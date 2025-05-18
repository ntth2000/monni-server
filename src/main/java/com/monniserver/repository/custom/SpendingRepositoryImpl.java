package com.monniserver.repository.custom;

import com.monniserver.entity.Spending;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpendingRepositoryImpl implements SpendingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Spending> findByUserIdAndOptionalFields(UUID userId, LocalDate date, String category, String description, Double amount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Spending> cq = cb.createQuery(Spending.class);
        Root<Spending> root = cq.from(Spending.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("user").get("id"), userId));

        if (date != null) {
            predicates.add(cb.equal(root.get("date"), date));
        }
        if (category != null) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        if (description != null) {
            predicates.add(cb.equal(root.get("description"), description));
        }
        if (amount != null) {
            predicates.add(cb.equal(root.get("amount"), amount));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }
}
