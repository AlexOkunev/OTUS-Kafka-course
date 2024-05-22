# Kafka Streams

Attach to kafka container's terminal
```bash
docker container exec -it kafka1-otuskafka /bin/bash
```

Run console producer and send messages with key and value separated with ':'
```bash
kafka-console-producer --broker-list localhost:9191 --topic events --property "parse.key=true" --property "key.separator=:"
>a:a
>b:b
>g:f
```


