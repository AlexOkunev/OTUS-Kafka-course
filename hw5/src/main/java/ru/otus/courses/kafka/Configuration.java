package ru.otus.courses.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Map;

import static java.util.UUID.randomUUID;

public class Configuration {
    public static final String TOPIC_EVENTS = "events";

    public static final Map<String, Object> STREAMS_CONFIG = Map.of(
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091",
            StreamsConfig.APPLICATION_ID_CONFIG, "hw-events-%s".formatted(randomUUID().hashCode()),
            StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500);

    public static final Map<String, Object> ADMIN_CONFIG = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");
}
