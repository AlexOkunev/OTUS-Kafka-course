package ru.otus.courses.kafka;

import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Configuration {

  public static final String TOPIC_1 = "topic1";
  public static final String TOPIC_2 = "topic2";

  public static final Map<String, Object> PRODUCER_CONFIG = Map.of(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
      ProducerConfig.ACKS_CONFIG, "all",
      ProducerConfig.TRANSACTIONAL_ID_CONFIG, "homework-transactions",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

  public static final Map<String, Object> CONSUMER_CONFIG = Map.of(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091",
      ConsumerConfig.GROUP_ID_CONFIG, "some-java-consumer",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

  public static final Map<String, Object> ADMIN_CONFIG = Map.of(
      AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");
}
