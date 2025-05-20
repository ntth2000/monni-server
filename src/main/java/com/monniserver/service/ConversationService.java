package com.monniserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monniserver.dto.*;
import com.monniserver.entity.Conversation;
import com.monniserver.entity.Spending;
import com.monniserver.entity.User;
import com.monniserver.exception.OpenAIException;
import com.monniserver.exception.UserNotFoundException;
import com.monniserver.repository.ConversationRepository;
import com.monniserver.repository.SpendingRepository;
import com.monniserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final OpenAIService openAIService;
    private final UserRepository userRepository;
    private final SpendingRepository spendingRepository;
    private final AnswerService answerService;
    private final SpendingService spendingService;

    public ConversationService(ConversationRepository conversationRepository, OpenAIService openAIService, UserRepository userRepository, SpendingRepository spendingRepository, AnswerService answerService, SpendingService spendingService) {
        this.openAIService = openAIService;
        this.answerService = answerService;
        this.spendingService = spendingService;
        this.userRepository = userRepository;
        this.spendingRepository = spendingRepository;
        this.conversationRepository = conversationRepository;
    }

    public List<ConversationResponse> findByUserId(UUID userId) {
        return conversationRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(h -> {
                    ConversationResponse conversationResponse = new ConversationResponse(h.getId(), h.getQuestion(), h.getAnswer(), h.getCreatedAt());
                    return conversationResponse;
                })
                .toList();
    }

    @Transactional
    public ConversationResponse addNewQuestion(String question, UUID userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        List<OpenAIResponse> responses;
        try {
            responses = openAIService.handleUserRequest(question, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenAIException("Failed to process request via OpenAI.");
        }

        Conversation newConversation = new Conversation(user, question);
        StringBuilder finalAnswer = new StringBuilder();


        for (OpenAIResponse userRequest : responses) {
            String intent = userRequest.getIntent();
            String answer = "";

            try {
                switch (intent) {
                    case "app_intro":
                        answer = "<p>Tớ là Monni - trợ lý tài chính cá nhân, giúp bạn ghi lại và theo dõi các khoản chi tiêu hằng ngày \uD83D\uDCB0</p>\n" +
                                "\n" +
                                "<p>Với tớ, bạn có thể:</p>\n" +
                                "<ul>\n" +
                                "  <li>\uD83D\uDCDD Ghi lại các khoản chi tiêu</li>\n" +
                                "  <li>✏\uFE0F Cập nhật hoặc xóa các giao dịch đã ghi trước đó</li>\n" +
                                "  <li>\uD83D\uDCCA Xem tổng chi tiêu theo khoảng thời gian (ngày, tuần, tháng, hoặc tùy chọn)</li>\n" +
                                "  <li>\uD83D\uDD0D Xem chi tiết các khoản chi theo danh mục hoặc mô tả</li>\n" +
                                "</ul>\n" +
                                "</br>" +
                                "<p>Ví dụ, bạn chỉ cần nói: <em>“Hôm nay mua cà phê 25.000, gửi xe 5.000”</em><br>\n" +
                                "Tớ sẽ tự động ghi lại giúp bạn \uD83D\uDE0A</p>\n";
                        break;
                    case "add_spending":
                        if (userRequest.getMessage() != null) {
                            answer = userRequest.getMessage();
                        } else {
                            for (SpendingItem item : userRequest.getDetailSpendings()) {
                                Spending spending = new Spending(
                                        item.getAmount(),
                                        item.getCategory(),
                                        item.getDescription(),
                                        item.getDate(),
                                        user
                                );
                                spendingRepository.save(spending);
                            }
                            answer = answerService.buildAddSpendingAnswer(userRequest.getDetailSpendings());
                        }
                        break;
                    case "get_summary":
                    case "get_detail":
                        LocalDate startDate = _formatDate(userRequest.getStartDate());
                        LocalDate endDate = _formatDate(userRequest.getEndDate());
                        String category = userRequest.getCategory();
                        String description = userRequest.getDescription();

                        List<Spending> filtered = spendingRepository.findFilteredSpending(userId, startDate, endDate, category, description);

                        answer = answerService.generateSpendingSummary(filtered, startDate, endDate, category, description);

                        if (userRequest.getCompareToStartDate() != null && userRequest.getCompareToEndDate() != null) {
                            LocalDate compareStart = _formatDate(userRequest.getCompareToStartDate());
                            LocalDate compareEnd = _formatDate(userRequest.getCompareToEndDate());
                            List<Spending> compareFiltered = spendingRepository.findFilteredSpending(userId, compareStart, compareEnd, category, description);
                            answer += "\n" + answerService.generateSpendingSummary(compareFiltered, compareStart, compareEnd, category, description);
                        }
                        if (intent.equals("get_detail")) {
                            answer += "\n" + answerService.generateDetailSpendings(filtered);
                        }
                        break;

                    case "update_spending":
                        List<SpendingItem> oldItems = userRequest.getNeedUpdatedSpendings();
                        List<SpendingItem> newItems = userRequest.getUpdatedSpendings();
                        if (oldItems != null && newItems != null && oldItems.size() == newItems.size()) {
                            UpdateSpendingResult updateResult = spendingService.updateSpendings(userId, oldItems, newItems);
                            answer = answerService.buildUpdateSpendingAnswer(updateResult.getSuccessfulUpdates(), updateResult.getNotFoundSpendings());
                        } else {
                            answer = "Xin lỗi, tớ bạn hãy cung cấp thêm thông tin để tớ thực hiện chỉnh sửa chi tiêu nha!";
                        }
                        break;

                    case "delete_spending":
                        List<SpendingItem> deleteItems = userRequest.getDeleteSpendings();
                        if (deleteItems == null || deleteItems.isEmpty()) {
                            answer = "Xin lỗi, tớ bạn hãy cung cấp thêm thông tin để tớ thực hiện xoá chi tiêu nha!";
                        } else {
                            DeleteSpendingResult deleteResult = spendingService.deleteSpendings(userId, deleteItems);
                            answer = answerService.buildDeleteSpendingAnswer(deleteResult.getSuccessfulDeletions(), deleteResult.getNotFoundSpendings());
                        }
                        break;

                    case "greeting":
                        answer = "Xin chào, tớ có thể giúp gì trong việc quản lí chi tiêu nhỉ? Hãy nói cho tớ biết nha! 😊";
                        break;

                    case "unknown":
                        answer = userRequest.getMessage();
                        break;

                    default:
                        answer = "Xin lỗi, tớ không hiểu yêu cầu của bạn.";
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new OpenAIException("Failed to process request via OpenAI: " + e.getMessage());
            }

            if (!answer.isEmpty()) {
                finalAnswer.append(answer);
            }
        }

        newConversation.setAnswer(finalAnswer.toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String aiResponsesJson = mapper.writeValueAsString(responses);

        newConversation.setAiResponsesJson(aiResponsesJson);

        Conversation savedConversation = conversationRepository.saveAndFlush(newConversation);

        return new ConversationResponse(
                savedConversation.getId(),
                savedConversation.getQuestion(),
                savedConversation.getAnswer(),
                savedConversation.getCreatedAt()
        );
    }

    private LocalDate _formatDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }
}
