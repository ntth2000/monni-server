package com.monniserver.service;

import com.monniserver.dto.SpendingItem;
import com.monniserver.entity.Spending;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnswerService {
    public String generateSpendingSummary(
            List<Spending> spendings,
            LocalDate startDate,
            LocalDate endDate,
            String category,
            String description
    ) {
        double totalAmount = spendings.stream().mapToDouble(Spending::getAmount).sum();

        int count = spendings.size();

        String startDateFormatted = startDate.format(DateTimeFormatter.ofPattern("dd/MM"));
        String endDateFormatted = endDate.format(DateTimeFormatter.ofPattern("dd/MM"));

        StringBuilder message = new StringBuilder();

        if (startDateFormatted.equals(endDateFormatted)) {
            message.append("Tổng chi tiêu của bạn trong ngày <strong>").append(startDateFormatted).append("</strong> là ");
        } else {
            message.append("Tổng chi tiêu của bạn từ <strong>")
                    .append(startDateFormatted).append("</strong> đến ").append(endDateFormatted)
                    .append(" là <strong>");
        }

        message.append(String.format("%,.0f", totalAmount)).append(" VND <strong/>");

        if (category != null) {
            message.append(" cho mục ").append(category);
        }
        if (description != null) {
            message.append(" (").append(description).append(")");
        }

        message.append(", gồm <strong>").append(count).append(" giao dịch</strong>.");

        return message.toString();
    }

    public String generateDetailSpendings(List<Spending> spendings) {
        if (spendings == null || spendings.isEmpty()) {
            return "Không có khoản chi tiêu nào được tìm thấy.";
        }

        // Group by date and sort by date ascending
        Map<String, List<Spending>> grouped = spendings.stream()
                .sorted(Comparator.comparing(Spending::getDate))
                .collect(Collectors.groupingBy(
                        s -> s.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        LinkedHashMap::new, // để giữ thứ tự
                        Collectors.toList()
                ));

        StringBuilder sb = new StringBuilder();
        sb.append("Dưới đây là các khoản chi tiêu chi tiết:\n<ul>");

        for (Map.Entry<String, List<Spending>> entry : grouped.entrySet()) {
            String date = entry.getKey();
            List<Spending> items = entry.getValue();

            sb.append("<li><strong>").append(date).append("</strong>: ");

            List<String> itemDescriptions = items.stream()
                    .map(item -> String.format(
                            "<strong>%,.0f VND</strong> cho <strong>%s</strong> (%s)",
                            item.getAmount(),
                            item.getDescription(),
                            item.getCategory()
                    ))
                    .collect(Collectors.toList());

            sb.append(String.join(", ", itemDescriptions)).append(".</li>");
        }

        sb.append("</ul>");
        return sb.toString();
    }

    public String buildAddSpendingAnswer(List<SpendingItem> items) {
        if (items == null || items.isEmpty()) return "";

        Map<LocalDate, List<SpendingItem>> groupedByDate = items.stream()
                .sorted(Comparator.comparing(SpendingItem::getDate))
                .collect(Collectors.groupingBy(SpendingItem::getDate, LinkedHashMap::new, Collectors.toList()));

        StringBuilder sb = new StringBuilder();
        sb.append("Đã lưu các khoản chi tiêu sau:\n<ul>");

        for (Map.Entry<LocalDate, List<SpendingItem>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<SpendingItem> spendings = entry.getValue();

            sb.append("<li><strong>Ngày ").append(date).append(":</strong> ");

            for (int i = 0; i < spendings.size(); i++) {
                SpendingItem item = spendings.get(i);
                sb.append(String.format("<strong>%,.0f VND</strong> cho <strong>%s</strong>", item.getAmount(), item.getDescription()));
                if (i < spendings.size() - 1) sb.append(", ");
            }

            sb.append(".</li>");
        }

        sb.append("</ul>");
        return sb.toString();
    }

    public String buildUpdateSpendingAnswer(List<SpendingItem[]> successfulUpdates, List<SpendingItem> notFoundSpendings) {
        StringBuilder sb = new StringBuilder();

        if (!successfulUpdates.isEmpty()) {
            sb.append("Đã cập nhật thành công ").append(successfulUpdates.size()).append(" khoản chi tiêu:\n<ul>");
            for (SpendingItem[] pair : successfulUpdates) {
                SpendingItem oldItem = pair[0];
                SpendingItem newItem = pair[1];

                sb.append("<li>")
                        .append("Từ <strong>")
                        .append(_formatAmount(oldItem.getAmount())).append(" VND</strong> cho ")
                        .append("<strong>").append(oldItem.getDescription()).append("</strong>")
                        .append(" (").append(oldItem.getCategory()).append(") vào <strong>")
                        .append(oldItem.getDate()).append("</strong>")
                        .append(" → thành <strong>")
                        .append(_formatAmount(newItem.getAmount())).append(" VND</strong> cho ")
                        .append("<strong>").append(newItem.getDescription()).append("</strong>")
                        .append(" (").append(newItem.getCategory()).append(") vào <strong>")
                        .append(newItem.getDate()).append("</strong>")
                        .append("</li>");
            }
            sb.append("</ul>");
        }

        if (!notFoundSpendings.isEmpty()) {
            sb.append("Không tìm thấy ").append(notFoundSpendings.size()).append(" khoản chi để cập nhật:\n<ul>");
            for (SpendingItem item : notFoundSpendings) {
                sb.append("<li>")
                        .append("<strong>").append(_formatAmount(item.getAmount())).append(" VND</strong> cho ")
                        .append("<strong>").append(item.getDescription()).append("</strong>")
                        .append(" (").append(item.getCategory()).append(") vào <strong>")
                        .append(item.getDate()).append("</strong>")
                        .append("</li>");
            }
            sb.append("</ul>");
        }

        return sb.toString();
    }

    public String buildDeleteSpendingAnswer(List<SpendingItem> successfulDeletes, List<SpendingItem> notFoundSpendings) {
        StringBuilder sb = new StringBuilder();

        if (!successfulDeletes.isEmpty()) {
            sb.append("Đã xoá thành công ").append(successfulDeletes.size()).append(" khoản chi tiêu:\n<ul>");
            for (SpendingItem item : successfulDeletes) {
                sb.append("<li>")
                        .append("<strong>").append(_formatAmount(item.getAmount())).append(" VND</strong> cho ")
                        .append("<strong>").append(item.getDescription()).append("</strong>")
                        .append(" (").append(item.getCategory()).append(") vào <strong>")
                        .append(item.getDate()).append("</strong>")
                        .append("</li>");
            }
            sb.append("</ul>");
        }

        if (!notFoundSpendings.isEmpty()) {
            sb.append("Không tìm thấy ").append(notFoundSpendings.size()).append(" khoản chi để xoá:\n<ul>");
            for (SpendingItem item : notFoundSpendings) {
                sb.append("<li>")
                        .append("<strong>").append(_formatAmount(item.getAmount())).append(" VND</strong> cho ")
                        .append("<strong>").append(item.getDescription()).append("</strong>")
                        .append(" (").append(item.getCategory()).append(") vào <strong>")
                        .append(item.getDate()).append("</strong>")
                        .append("</li>");
            }
            sb.append("</ul>");
        }

        return sb.toString();
    }


    private String _formatAmount(Double amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }

}
