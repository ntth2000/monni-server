package com.monniserver.service;

import com.google.gson.Gson;
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

    public String handleUserRequest(String userMsg) {
        ChatCompletion chatCompletion = detectIntent(userMsg);

        ChatCompletionMessage completionMsg = chatCompletion.choices().get(0).message();
        Optional<String> content = completionMsg.content();


        if (content.isPresent()) {
            Gson gson = new Gson();
            SpendingRequest request = gson.fromJson(content.get(), SpendingRequest.class);
            System.out.println("Intent: " + request.getIntent());
            System.out.println("Amount: " + request.getAmount ());
            System.out.println("Description: " + request.getDescription());
            System.out.println("Date: " + request.getDate());
            return content.get();
        } else {
            System.out.println("No content found");
            return "No content found.";
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
    
            Example responses:
    
            User: "Hôm nay t tiêu 70k mua bánh mì"
            → {
                "intent": "add_spending",
                "amount": 70000,
                "category": "ăn uống",
                "description": "bánh mì",
                "date": "2025-05-10",
                "target_description": null
            }
    
            User: "Xoá khoản trà sữa hôm qua"
            → {
                "intent": "delete_spending",
                "amount": null,
                "category": null,
                "description": null,
                "date": "2025-05-09",
                "target_description": "trà sữa"
            }
    
            User: "Cập nhật tiền taxi hôm trước thành 150k"
            → {
                "intent": "update_spending",
                "amount": 150000,
                "category": "di chuyển",
                "description": "taxi",
                "date": "2025-05-09",
                "target_description": "taxi"
            }
    
            User: "Tổng chi tiêu tháng này"
            → {
                "intent": "get_summary",
                "amount": null,
                "category": null,
                "description": null,
                "date": "2025-05-01",
                "target_description": null
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
