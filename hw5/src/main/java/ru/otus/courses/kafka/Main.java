package ru.otus.courses.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static ru.otus.courses.kafka.AdminUtils.recreateTopics;
import static ru.otus.courses.kafka.Configuration.*;
import static ru.otus.courses.kafka.TimeUtils.toLocalTime;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        recreateTopics(ADMIN_CONFIG, TOPIC_EVENTS);

        StreamsBuilder builder = new StreamsBuilder();

        KTable<Windowed<String>, Long> counts = builder
                .stream(TOPIC_EVENTS, Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count();

        counts.toStream()
                .foreach((k, v) -> log.info("Window: [{} - {}]. Key:{}, Count: {}", toLocalTime(k.window().start()), toLocalTime(k.window().end()), k.key(), v));

        Topology topology = builder.build();

        log.warn("{}", topology.describe());

        try (var kafkaStreams = new KafkaStreams(topology, new StreamsConfig(STREAMS_CONFIG))) {
            log.info("App Started");

            kafkaStreams.start();

            while (!Thread.interrupted()) {
                Thread.sleep(1000);
            }

            log.info("Shutting down now");
        }
    }
}