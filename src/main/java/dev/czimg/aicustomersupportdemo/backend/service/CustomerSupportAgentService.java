package dev.czimg.aicustomersupportdemo.backend.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerSupportAgentService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    private void initialize() throws IOException {

        Path documentPath = Paths.get("src/main/resources/documents/item-list.pdf");
        DocumentParser documentParser = new ApachePdfBoxDocumentParser();
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100,0);

        Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        embeddingStoreIngestor.ingest(document);
    }

    public String generateAgentResponse(String userMessage) {

        String question = userMessage;
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        final int MAX_RESULTS = 2;
        final double MIN_SCORE = 0.5;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(questionEmbedding, MAX_RESULTS, MIN_SCORE);

        PromptTemplate promptTemplate = PromptTemplate.from(
                "You are a customer support agent called Skynet for a corporation called Weyland-Yutani.\n" +
                        "Your task is to pass the information about the products that are for sale listed in the item list of the corporation.\n" +
                        "The customer's question:\n"
                        + "{{question}}\n" +
                        "Base your answer on the following information:\n"
                        + "{{information}}\n"
        );

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        Map<String, Object> variables = Map.ofEntries(
                Map.entry("question", question),
                Map.entry("information", information)
        );

        Prompt prompt = promptTemplate.apply(variables);

        CustomerSupportAgent customerSupportAgent = AiServices.builder(CustomerSupportAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();

        return customerSupportAgent.chat(prompt.text());
    }
}
