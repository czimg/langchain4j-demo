package dev.czimg.aicustomersupportdemo.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import dev.czimg.aicustomersupportdemo.backend.controller.CustomerSupportAgentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Route("")
@Slf4j
public class ChatUI extends FlexLayout {

    private TextField messageInput;
    private TextArea chatArea;
    private VerticalLayout cartLayout = new VerticalLayout();
    @Autowired
    private CustomerSupportAgentController customerSupportAgentController;

    public ChatUI() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setFlexDirection(FlexDirection.COLUMN);

        add(new H3("Customer support Demo"));

        chatArea = new TextArea();
        chatArea.setWidth("80%");
        chatArea.setHeight("600px");
        chatArea.setReadOnly(true);
        add(chatArea);

        messageInput = new TextField("Type your message (Enter to send)");
        messageInput.setWidth("80%");
        messageInput.addKeyDownListener(Key.ENTER, keyDownEvent -> {
            sendMessageToServer();
        });

        add(messageInput);
    }

    private void sendMessageToServer() {
        String userMessage = messageInput.getValue();
        addNewTextToChatArea("User", userMessage);
        messageInput.clear();

        UI ui = UI.getCurrent();
        if (ui != null) {
            log.info("ACCESSING UI");
            ui.access(() -> sendHttpRequest(userMessage));
        }
    }

    private void sendHttpRequest(String userMessage) {
        String backendUrl = "http://localhost:8080/api/chat/message";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(backendUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(userMessage))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Response received: {}", response.body());

            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.access(() -> addNewTextToChatArea("Agent", response.body()));
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Error sending HTTP request", e);
        }
    }

    private void addNewTextToChatArea(String sender, String message) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.access(() -> {
                chatArea.setValue(chatArea.getValue() + sender + ": " + "\n");
                chatArea.setValue(chatArea.getValue() + message + "\n\n");
            });
        }
    }
}
