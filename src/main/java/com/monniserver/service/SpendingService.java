package com.monniserver.service;

import com.monniserver.dto.DeleteSpendingResult;
import com.monniserver.dto.SpendingItem;
import com.monniserver.dto.UpdateSpendingResult;
import com.monniserver.entity.Spending;
import com.monniserver.entity.User;
import com.monniserver.exception.SpendingSaveException;
import com.monniserver.repository.SpendingRepository;
import com.monniserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Transactional
    public UpdateSpendingResult updateMultipleSpendings(UUID userId, List<SpendingItem> oldItems, List<SpendingItem> newItems) {
        List<SpendingItem[]> successfulUpdates = new ArrayList<>();
        List<SpendingItem> notFoundSpendings = new ArrayList<>();

        for (int i = 0; i < oldItems.size(); i++) {
            SpendingItem oldItem = oldItems.get(i);
            SpendingItem newItem = newItems.get(i);

            if (oldItem.getDate() != null || oldItem.getCategory() != null || oldItem.getDescription() != null || oldItem.getAmount() != null) {
                List<Spending> matched = spendingRepository.findByUserIdAndOptionalFields(
                        userId,
                        oldItem.getDate(),
                        oldItem.getCategory(),
                        oldItem.getDescription(),
                        oldItem.getAmount()
                );

                Spending spending = matched.stream().findFirst().orElse(null);
                if (spending == null) {
                    notFoundSpendings.add(oldItem);
                } else {
                    try {
                        if (newItem.getDate() != null) {
                            spending.setDate(newItem.getDate());
                        }
                        if (newItem.getCategory() != null) {
                            spending.setCategory(newItem.getCategory());
                        }
                        if (newItem.getDescription() != null) {
                            spending.setDescription(newItem.getDescription());
                        }
                        if (newItem.getAmount() != null) {
                            spending.setAmount(newItem.getAmount());
                        }
                        spendingRepository.save(spending);
                        successfulUpdates.add(new SpendingItem[]{oldItem, newItem});
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new SpendingSaveException("Có lỗi xảy ra khi cập nhật chi tiêu, vui lòng thử lại.");
                    }
                }
            }
        }

        return new UpdateSpendingResult(successfulUpdates, notFoundSpendings);
    }

    @Transactional
    public DeleteSpendingResult deleteSpendings(UUID userId, List<SpendingItem> deleteItems) {
        List<SpendingItem> successfulDeletes = new ArrayList<>();
        List<SpendingItem> notFoundSpendings = new ArrayList<>();

        for (SpendingItem item : deleteItems) {
            try {
                List<Spending> matched = spendingRepository.findByUserIdAndOptionalFields(
                        userId,
                        item.getDate(),
                        item.getCategory(),
                        item.getDescription(),
                        item.getAmount()
                );

                Spending spending = matched.stream().findFirst().orElse(null);
                if (spending == null) {
                    notFoundSpendings.add(item);
                } else {
                    spendingRepository.delete(spending);
                    successfulDeletes.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SpendingSaveException("Có lỗi xảy ra khi xoá chi tiêu, vui lòng thử lại.");
            }
        }

        return new DeleteSpendingResult(successfulDeletes, notFoundSpendings);
    }
}
