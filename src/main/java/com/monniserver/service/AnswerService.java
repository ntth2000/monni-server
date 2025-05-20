package com.monniserver.service;

import com.monniserver.dto.SpendingItem;
import com.monniserver.entity.Spending;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

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
            message.append("<p>Tổng chi tiêu của bạn trong ngày <strong>").append(startDateFormatted).append("</strong> là <strong>");
        } else {
            message.append("Tổng chi tiêu của bạn từ <strong>")
                    .append(startDateFormatted).append("</strong> đến <strong>").append(endDateFormatted)
                    .append("</strong> là <strong>");
        }

        message.append(this._formatAmount(totalAmount)).append("</strong>");

        if (category != null) {
            message.append(" cho mục ").append(category);
        }
        if (description != null) {
            message.append(" (").append(description).append(")");
        }

        message.append(", gồm <strong>").append(count).append(" giao dịch</strong>.</p>");

        return message.toString();
    }

    public String generateDetailSpendings(List<Spending> spendings) {
        if (spendings == null || spendings.isEmpty()) {
            return "";
        }

        List<Spending> sortedSpendings = spendings.stream()
                .sorted(Comparator.comparing(Spending::getDate))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("<p>Dưới đây là các khoản chi tiêu chi tiết:</p>");
        sb.append("<table>");
        sb.append("<thead><tr>")
                .append("<th>Giao dịch</th>")
                .append("<th>Danh mục</th>")
                .append("<th>Ngày</th>")
                .append("<th>Số tiền</th>")
                .append("</tr></thead>");
        sb.append("<tbody>");

        for (Spending item : sortedSpendings) {
            sb.append("<tr>")
                    .append("<td>").append(item.getDescription()).append("</td>")
                    .append("<td>").append(item.getCategory()).append("</td>")
                    .append("<td>").append(this._formatDate(item.getDate())).append("</td>")
                    .append("<td><strong>").append(this._formatAmount(item.getAmount())).append("</strong></td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");
        return sb.toString();
    }


    public String buildAddSpendingAnswer(List<SpendingItem> items) {
        if (items == null || items.isEmpty()) return "";

        List<SpendingItem> sortedItems = items.stream()
                .sorted(Comparator.comparing(SpendingItem::getDate))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("Tớ đã lưu các khoản chi tiêu sau rồi nha:\n");
        sb.append("<div><table>");
        sb.append("<thead><tr>")
                .append("<th>Giao dịch</th>")
                .append("<th>Danh mục</th>")
                .append("<th>Ngày</th>")
                .append("<th>Số tiền</th>")
                .append("</tr></thead>");
        sb.append("<tbody>");

        for (SpendingItem item : sortedItems) {
            sb.append("<tr>")
                    .append("<td>").append(item.getDescription()).append("</td>")
                    .append("<td>").append(item.getCategory()).append("</td>")
                    .append("<td>").append(this._formatDate(item.getDate())).append("</td>")
                    .append("<td>").append(this._formatAmount(item.getAmount())).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table></div>");
        return sb.toString();
    }

    public String buildUpdateSpendingAnswer(List<Spending> successfulUpdates, List<SpendingItem> notFoundSpendings) {
        StringBuilder sb = new StringBuilder();

        if (!successfulUpdates.isEmpty()) {
            if (successfulUpdates.size() == 1) {
                Spending spending = successfulUpdates.getFirst();
                sb.append("Đã sửa lại chi tiêu ")
                        .append(spending.getDescription())
                        .append(" thành ").append(this._formatAmount(spending.getAmount())).append(" ngày")
                        .append(this._formatDate(spending.getDate()))
                        .append("cho mục ").append(spending.getCategory()).append(" rồi nha!");
            } else {
                sb.append("<p>Tớ đã cập nhật các chi tiêu sau rồi nha:</p>");
                sb.append("<div><table>");
                sb.append("<thead><tr>")
                        .append("<th>Giao dịch</th>")
                        .append("<th>Danh mục</th>")
                        .append("<th>Ngày</th>")
                        .append("<th>Số tiền</th>")
                        .append("</tr></thead>");
                sb.append("<tbody>");

                for (Spending item : successfulUpdates) {
                    sb.append("<tr>")
                            .append("<td>").append(item.getDescription()).append("</td>")
                            .append("<td>").append(item.getCategory()).append("</td>")
                            .append("<td>").append(item.getDate()).append("</td>")
                            .append("<td>").append(this._formatAmount(item.getAmount())).append("</td>")
                            .append("</tr>");
                }

                sb.append("</tbody></table></div>");
            }
        }

        if (!notFoundSpendings.isEmpty()) {
            sb.append("<p>Bạn hãy cho tớ thêm thông tin về các khoản còn lại đế tớ cập nhật nhé. Tớ chưa thể cập nhật do thiếu thông tin 😊</p>");
        }

        return sb.toString();
    }

    public String buildDeleteSpendingAnswer(List<Spending> successfulDeletions, List<SpendingItem> notFoundSpendings) {
        StringBuilder sb = new StringBuilder();

        if (!successfulDeletions.isEmpty()) {
            if (successfulDeletions.size() == 1) {
                Spending spending = successfulDeletions.getFirst();
                sb.append("Tớ đã xoá <strong>mua ")
                        .append(spending.getDescription())
                        .append(this._formatAmount(spending.getAmount())).append("</strong> ngày <strong>")
                        .append(this._formatDate(spending.getDate()))
                        .append("</strong> cho mục <strong>").append(spending.getCategory()).append("</strong> rồi nha!");
            } else {
                sb.append("<p>Tớ đã xoá các chi tiêu sau rồi nha:</p>");
                sb.append("<div><table>");
                sb.append("<thead><tr>")
                        .append("<th>Giao dịch</th>")
                        .append("<th>Danh mục</th>")
                        .append("<th>Ngày</th>")
                        .append("<th>Số tiền</th>")
                        .append("</tr></thead>");
                sb.append("<tbody>");

                for (Spending item : successfulDeletions) {
                    sb.append("<tr>")
                            .append("<td>").append(item.getDescription()).append("</td>")
                            .append("<td>").append(item.getCategory()).append("</td>")
                            .append("<td>").append(this._formatDate(item.getDate())).append("</td>")
                            .append("<td>").append(this._formatAmount(item.getAmount())).append("</td>")
                            .append("</tr>");
                }

                sb.append("</tbody></table></div>");
            }
        }

        if (!notFoundSpendings.isEmpty()) {
            sb.append("<p>Bạn hãy cho tớ thêm thông tin về các khoản còn lại đế tớ xoá nhé. Tớ chưa thể xoá do thiếu hoặc sai thông tin 😊</p>");
        }

        return sb.toString();
    }


    private String _formatAmount(Double amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount).replace(",", ".") + "₫";
    }

    private String _formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
