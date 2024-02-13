package dev.czimg.aicustomersupportdemo.backend.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CustomerSupportAgentService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;
    @Autowired
    private RetrievalAugmentor retrievalAugmentor;

    @PostConstruct
    private void initialize() throws IOException {

        Path documentPath = Paths.get("src/main/resources/documents/item-list.pdf");
        DocumentParser documentParser = new ApachePdfBoxDocumentParser();
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(500,0);

        Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        embeddingStoreIngestor.ingest(document);
    }

    public String generateAgentResponse(String userMessage) {

        CustomerSupportAgent customerSupportAgent = AiServices.builder(CustomerSupportAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor)
                .build();

        return customerSupportAgent.chat(userMessage);
    }
}
