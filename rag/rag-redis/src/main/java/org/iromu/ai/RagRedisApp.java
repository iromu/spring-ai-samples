package org.iromu.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.params.ScanParams;

import java.util.Collections;
import java.util.HashSet;

@SpringBootApplication
@Slf4j
public class RagRedisApp {

    public static void main(String[] args) {
        SpringApplication.run(RagRedisApp.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEvent(RedisVectorStore vectorStore,
                                                                       @Value("classpath:medicaid-wa-faqs.pdf") Resource resource,
                                                                       @Value("${spring.ai.vectorstore.redis.prefix}") String prefix) {
        return event -> {

            //if (vectorStore instanceof RedisVectorStore r)
            cleanupRedis(vectorStore, prefix);

            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                            .build())
                    .build();

            var documentReader = new ParagraphPdfDocumentReader(resource, config);
            var textSplitter = new TokenTextSplitter();
            var documentList = textSplitter.apply(documentReader.get());
            log.info("Adding {} documents to the vector store", documentList.size());
            for (Document document : documentList) {
                log.info("Adding document {}", document.getId());
                vectorStore.accept(Collections.singletonList(document));
            }
            log.info("Added {} documents to the vector store", documentList.size());
        };
    }

    private static void cleanupRedis(RedisVectorStore vectorStore, String prefix) {
        var matchingKeys = new HashSet<String>();
        var params = new ScanParams().match(prefix + "*");
        var nextCursor = "0";
        var jedis = vectorStore.getJedis();

        do {
            var scanResult = jedis.scan(nextCursor, params);
            matchingKeys.addAll(scanResult.getResult());
            nextCursor = scanResult.getCursor();
        } while (!nextCursor.equals("0"));

        if (!matchingKeys.isEmpty()) {
            String[] array = matchingKeys.toArray((new String[0]));
            log.info("Deleting keys: {}", array);
            jedis.del(array);
        }
    }
}

