package org.aryak.batch.writers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.archival.model.OutputRecord;
import org.aryak.batch.utils.RedisUtil;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericWriter implements ItemWriter<OutputRecord> {

    private static final String FILENAME = "MOCK_DATA.csv"; // You can pass this dynamically later if needed
    private final RedisUtil redisUtil;

    @Value("${commit.size}")
    int commitSize;

    @Value("${failure.line.number:362}")
    int failureRecordNumber;

    private int i;

    @Value("${simulate.failure:false}")
    private boolean simulateFailure;

    @PostConstruct
    public void init() {
        this.i = redisUtil.getLastProcessedCount(FILENAME);
        log.info("Restart detected. Starting from record: {}", i + 1);
    }

    @Override
    public void write(@NonNull Chunk<? extends OutputRecord> chunk) throws Exception {

        log.info("Inside here! i is : {}", i);
        List<? extends OutputRecord> items = chunk.getItems();

        for (OutputRecord o : items) {

            // publish to kafka topic
            log.info("Record no : {} published to kafka", i);
            i++;

            // [300-400] -> reading will start 360 onwards
            // simulate mid-chunk failure
            if (simulateFailure && i == failureRecordNumber) {
                throw new RuntimeException("kafka is down!!");
            }


            if (i % commitSize == 0) {
                log.info("Updating redis count for : {}'th item.", i);
                // update redis last processed count
                redisUtil.updateOffset(FILENAME, i);
            }

        }


    }
}
