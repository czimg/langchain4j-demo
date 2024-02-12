package dev.czimg.aicustomersupportdemo.backend.configuration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetrievalAugmentorConfig {

    @Autowired
    private ContentRetriever contentRetriever;
    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Bean
    public RetrievalAugmentor retrievalAugmentor() {

        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatLanguageModel);

        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .build();
    }
}
