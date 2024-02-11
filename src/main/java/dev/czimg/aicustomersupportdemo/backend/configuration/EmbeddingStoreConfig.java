package dev.czimg.aicustomersupportdemo.backend.configuration;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Configuration
public class EmbeddingStoreConfig {

    @Value("${collection.name}")
    private String collectionName;
    @Value("${collection.host}")
    private String collectionHost;
    @Value("${collection.port}")
    private int collectionPort;
    @Value("${collection.use-tls}")
    private boolean collectionUseTls;
    @Value("${collection.payload-text-key}")
    private String collectionPayloadTextKey;
    @Value("${collection.api-key}")
    private String collectionApiKey;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {

        QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder(collectionHost, collectionPort, collectionUseTls).build());
        client.deleteCollectionAsync(collectionName);
        try {
            client.createCollectionAsync(collectionName, Collections.VectorParams.newBuilder().setDistance(Collections.Distance.Cosine).setSize(384).build()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return new QdrantEmbeddingStore(
                collectionName,
                collectionHost,
                collectionPort,
                collectionUseTls,
                collectionPayloadTextKey,
                collectionApiKey);
    }
}
