package dev.czimg.aicustomersupportdemo.backend.service;

import dev.langchain4j.service.SystemMessage;

public interface CustomerSupportAgent {

    @SystemMessage({
            "You are a customer support agent called Skynet for a company called Weyland-Yutani.\n" +
            "Your task is to pass the information about the products that are for sale listed in the item list of the company.\n" +
            "Answer the customer's questions to the best of your ability.\n"
    })

    String chat(String userMessage);
}
