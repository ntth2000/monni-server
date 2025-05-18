package com.monniserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monniserver.dto.OpenAIResponse;
import com.monniserver.entity.Conversation;
import com.monniserver.exception.OpenAIException;
import com.monniserver.repository.ConversationRepository;
import com.monniserver.utils.PromptUtil;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OpenAIService {
    private final OpenAIClient openAIClient;
    private final String prompt;
    private final ConversationRepository conversationRepository;

    public OpenAIService(ConversationRepository conversationRepository) {
        this.openAIClient = OpenAIOkHttpClient.fromEnv();
        this.prompt = PromptUtil.INTENT_PROMPT;
        this.conversationRepository = conversationRepository;
    }

    public String getToday() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public List<OpenAIResponse> handleUserRequest(String userMsg, UUID userId) {
        try {
            ChatCompletion chatCompletion = detectIntent(userMsg, userId);
            ChatCompletionMessage completionMsg = chatCompletion.choices().get(0).message();
            Optional<String> content = completionMsg.content();

            if (content.isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                List<OpenAIResponse> responseList = mapper.readValue(content.get(), new TypeReference<List<OpenAIResponse>>() {
                });

                return responseList;
            } else {
                throw new OpenAIException("Received empty or invalid response from OpenAI.");
            }
        } catch (Exception e) {
            throw new OpenAIException("Failed to connect to OpenAI API: " + e.getMessage());
        }
    }

    public ChatCompletion detectIntent(String userMsg, UUID userId) {
        StringBuilder systemPrompt = new StringBuilder();
        List<Conversation> previousMessages = conversationRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId);

        systemPrompt.append("Hi, I'm MonniServer. Today is: ").append(getToday()).append(". ");
        systemPrompt.append(this.prompt);
        systemPrompt.append("This is the current user message: ").append(userMsg).append(".");
        systemPrompt.append("This is the 3 latest messages : ");

        previousMessages.forEach(m -> systemPrompt.append(m.getQuestion()).append("; "));

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(systemPrompt.toString())
                .model(ChatModel.GPT_4_1)
                .build();

        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);

        System.out.println(chatCompletion);


        return chatCompletion;
    }
}
