package com.monniserver.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PromptUtil {
    public static final String INTENT_PROMPT = loadPrompt("INTENT_PROMPT.txt");

    private static String loadPrompt(String fileName) {
        Path path = Paths.get("src/main/resources/prompts/" + fileName);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read prompt file: " + fileName, e);
        }
    }
}
