package ru.otus.courses.kafka;

import static java.lang.Thread.sleep;
import static java.util.stream.IntStream.rangeClosed;
import static ru.otus.courses.kafka.AdminUtils.recreateTopics;
import static ru.otus.courses.kafka.Configuration.ADMIN_CONFIG;
import static ru.otus.courses.kafka.Configuration.CONSUMER_CONFIG;
import static ru.otus.courses.kafka.Configuration.PRODUCER_CONFIG;
import static ru.otus.courses.kafka.Configuration.TOPIC_1;
import static ru.otus.courses.kafka.Configuration.TOPIC_2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    recreateTopics(ADMIN_CONFIG, TOPIC_1, TOPIC_2);

    var consumer1 = new LoggingConsumer("RC", List.of(TOPIC_1, TOPIC_2), CONSUMER_CONFIG, true);
    var consumer2 = new LoggingConsumer("RUnc", List.of(TOPIC_1, TOPIC_2), CONSUMER_CONFIG, false);

    final ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.submit(consumer1::process, 5);
    executorService.submit(consumer2::process);

    log.info("Threads with consumers are created");

    sleep(500);

    try (var producer = new KafkaProducer<String, String>(PRODUCER_CONFIG)) {
      producer.initTransactions();

      log.info("Start transaction 1");
      producer.beginTransaction();

      log.info("Write messages to topic {} in transaction 1", TOPIC_1);
      rangeClosed(1, 5).forEach(i -> producer.send(new ProducerRecord<>(TOPIC_1, "data-1-%d".formatted(i))));
      sleep(500);

      log.info("Write messages to topic {} in transaction 1", TOPIC_2);
      rangeClosed(1, 5).forEach(i -> producer.send(new ProducerRecord<>(TOPIC_2, "data-2-%d".formatted(i))));
      sleep(500);

      producer.commitTransaction();

      log.info("Transaction 1 commited");

      sleep(1000);

      log.info("Start transaction 2");

      producer.beginTransaction();

      log.info("Write messages to topic {} in transaction 2", TOPIC_1);
      rangeClosed(6, 7).forEach(i -> producer.send(new ProducerRecord<>(TOPIC_1, "data-1-%d".formatted(i))));
      sleep(500);

      log.info("Write messages to topic {} in transaction 2", TOPIC_2);
      rangeClosed(6, 7).forEach(i -> producer.send(new ProducerRecord<>(TOPIC_2, "data-2-%d".formatted(i))));
      sleep(500);

      producer.abortTransaction();
      log.info("Transaction 2 aborted");
    }

    log.info("Wait...");
    sleep(1000);

    log.info("Shutdown executor service");
    executorService.shutdownNow();

    sleep(500);
    log.info("Exit");
  }
}