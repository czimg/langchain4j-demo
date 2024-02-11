package dev.czimg.aicustomersupportdemo.backend.configuration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ChatLanguageModelConfig {

    @Value("${model.name}")
    private String modelName;
    @Value("${model.url}")
    private String modelUrl;

    @Bean
    public ChatLanguageModel chatLanguageModel() {

        return OllamaChatModel.builder()
                .modelName(modelName)
                .baseUrl(modelUrl)
                .timeout(Duration.ofSeconds(60))
                .temperature(0.5)
                .build();
    }
}
