package com.monniserver.service;

import com.google.gson.Gson;
import com.monniserver.config.GsonProvider;
import com.monniserver.dto.OpenAIResponse;
import com.monniserver.dto.SpendingRequest;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class OpenAIService {
    private OpenAIClient openAIClient;

    public OpenAIService () {
        this.openAIClient = OpenAIOkHttpClient.fromEnv();
    }

    public ChatCompletion testFn() {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Say this is a test")
                .model(ChatModel.GPT_4_1)
                .build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        System.out.println(chatCompletion);
        return chatCompletion;
    }

    public String getToday() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public Optional<OpenAIResponse> handleUserRequest(String userMsg) {
        ChatCompletion chatCompletion = detectIntent(userMsg);

        ChatCompletionMessage completionMsg = chatCompletion.choices().get(0).message();
        Optional<String> content = completionMsg.content();


        if (content.isPresent()) {
            Gson gson = GsonProvider.getGson();
            OpenAIResponse request = gson.fromJson(content.get(), OpenAIResponse.class);
            return Optional.of(request);
        } else {
            System.out.println("No content found");
            return null;
        }
    }

    public ChatCompletion detectIntent(String userMsg) {
        String systemPrompt = """
            You are an intelligent personal finance assistant.
        
            Based on the user's message, identify their intent and extract relevant information.
        
            Supported intents:
            - add_spending
            - update_spending
            - delete_spending
            - get_summary
            - greeting
            - unknown
        
            Return JSON with these keys:
            - intent: one of the intents above
            - amount: number (VND), null if not found
            - category: string (e.g., ăn uống, điện, nước...), null if not found
            - description: short string (e.g., trà sữa, tiền điện), null if not found
            - date: yyyy-MM-dd, default to today if not provided
            - target_description: for update/delete, what spending entry to find and act on
            - message: 
                - if intent is 'greeting', return a friendly greeting (e.g., "Xin chào! Tôi có thể giúp gì cho bạn hôm nay?")
                - if intent is 'unknown', return an explanation (e.g., "Xin lỗi, hiện tại tôi chưa hỗ trợ chức năng này. Tôi chỉ có thể giúp bạn quản lý chi tiêu.")
                - otherwise, set to null
        
            Example responses:
        
            User: "Hôm nay t tiêu 70k mua bánh mì"
            → {
                "intent": "add_spending",
                "amount": 70000,
                "category": "ăn uống",
                "description": "bánh mì",
                "date": "2025-05-10",
                "target_description": null,
                "message": null
            }
        
            User: "Xoá khoản trà sữa hôm qua"
            → {
                "intent": "delete_spending",
                "amount": null,
                "category": null,
                "description": null,
                "date": "2025-05-09",
                "target_description": "trà sữa",
                "message": null
            }
        
            User: "Xin chào"
            → {
                "intent": "greeting",
                "amount": null,
                "category": null,
                "description": null,
                "date": null,
                "target_description": null,
                "message": "Xin chào! Tôi có thể giúp gì cho bạn hôm nay?"
            }
        
            User: "Nhắc tôi uống nước mỗi 2 tiếng"
            → {
                "intent": "unknown",
                "amount": null,
                "category": null,
                "description": null,
                "date": null,
                "target_description": null,
                "message": "Xin lỗi, hiện tại tôi chưa hỗ trợ chức năng này. Tôi chỉ có thể giúp bạn quản lý chi tiêu."
            }
        
            Only return valid JSON. Set null for any field not available.
        
            This is the user message:
        """;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Today is: " + getToday() + ". " + systemPrompt + userMsg)
                .model(ChatModel.GPT_4_1)
                .build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        System.out.println(chatCompletion);
        return chatCompletion;
    }
}
