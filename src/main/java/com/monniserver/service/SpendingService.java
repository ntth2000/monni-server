package com.monniserver.service;

import com.monniserver.entity.Spending;
import com.monniserver.entity.User;
import com.monniserver.repository.SpendingRepository;
import com.monniserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.TimeZone;
import java.util.UUID;

@Service
public class SpendingService {
    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;


    public SpendingService(SpendingRepository spendingRepository, UserRepository userRepository) {
        this.spendingRepository = spendingRepository;
        this.userRepository = userRepository;
    }

    public void saveSpending(long amount, String description, String category, UUID userId, LocalDate date) {
        Spending newSpending = new Spending();

        newSpending.setAmount(amount);
        newSpending.setCategory(category);
        User user = userRepository.findById(userId).orElseThrow();
        newSpending.setUser(user);
        newSpending.setDescription(description);
        newSpending.setDate(date);

        spendingRepository.save(newSpending);
    }
}
