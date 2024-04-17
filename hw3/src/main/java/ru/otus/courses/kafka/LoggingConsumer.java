package ru.otus.courses.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingConsumer {

  private static final Logger log = LoggerFactory.getLogger(LoggingConsumer.class);

  private final List<String> topics;
  protected final Map<String, Object> config;
  protected final String name;

  public LoggingConsumer(String name, List<String> topics, Map<String, Object> config, boolean readCommitted) {
    this.topics = topics;
    this.name = name;
    this.config = new HashMap<>(config);

    this.config.put(ConsumerConfig.GROUP_ID_CONFIG, "%s-%s".formatted(name, UUID.randomUUID()));
    this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    if (readCommitted) {
      this.config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    }
  }

  public void process() {
    log.info("[{}] Start processing", name);

    try (var consumer = new KafkaConsumer<String, String>(config)) {
      consumer.subscribe(topics);

      log.info("[{}] Subscribed topics: {}", name, topics);

      while (!Thread.interrupted()) {
        var read = consumer.poll(Duration.ofSeconds(1));
        for (var record : read) {
          processOne(record);
        }
      }
    } catch (Exception ignored) {
    }

    log.info("[{}] Complete processing", name);
  }

  protected void processOne(ConsumerRecord<String, String> record) {
    log.info("[{}] Receive from {}. {}:{} at {}", name, record.topic(), record.key(), record.value(), record.offset());
  }
}
