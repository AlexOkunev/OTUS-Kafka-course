# Postgres, Debezium с использованием Docker

1. Запустить контейнеры Zookeeper, Postgres, Kafka, Kafka Connect, kafka-ui
```bash
docker compose up -d
docker compose ps -a
```
![docker](01-run-docker-containers.png) 

2. Проверить логи Kafka Connect
```bash
docker logs -f connect
```
![connect logs](02-connect-logs.png)

3. Проверить статус
```bash
curl http://localhost:8083 | jq
```
![status](03-status.png)

4. Запросить список плагинов коннекторов 
```bash
curl http://localhost:8083/connector-plugins | jq
```
![plugins](04-plugins.png)

5. Проверить список топиков \
![topics](05-topics.png)

6. Создать таблицу в postgres и заполнить ее данными
```bash
    docker exec -ti postgres psql -U postgres
```
```sql
CREATE TABLE test (id INT PRIMARY KEY, message TEXT, active BOOLEAN);
INSERT INTO test (id, message, active) VALUES (1, 'fist record', TRUE);
INSERT INTO test (id, message, active) VALUES (2, 'second record', TRUE);
INSERT INTO test (id, message, active) VALUES (3, 'one more row', TRUE);
INSERT INTO test (id, message, active) VALUES (4, 'disabled item', FALSE);
```
![table](06-create-table.png) \
![data](07-insert-data.png)

7. Создать коннектор
```bash
curl -X POST --data-binary "@test_data.json" -H "Content-Type: application/json" http://localhost:8083/connectors | jq
```
![connector](08-connector.png)

8. Проверить статус коннектора
```bash
curl http://localhost:8083/connectors/test-data-connector/status | jq
```
![connector-status](09-connector-status.png)

10. Проверить топики \
![topics](10-topics.png)

12. Прочитать данные в топике \
![data-1](11-data.png) \
![data-2](12-data.png) 

13. Открыть postgres, добавить 1 новую запись, модифицировать 1 старую запись и удалить 1 запись
```bash
    docker exec -ti postgres psql -U postgres
```
```sql
    INSERT INTO test (id, message, active) VALUES (5, 'one more disabled item', FALSE);
    UPDATE test SET active = TRUE WHERE id = 4;
    DELETE FROM test WHERE id = 3;
```
![modification](13-modification.png) \
![delete](14-delete.png) \
![new-data](15-new-data.png)
14. Посмотреть содержимое топика \
![topic-1](16-topic-1.png) \
![topic-2](17-topic-2.png) \
![topic-3](18-topic-3.png) \
![topic-4](19-topic-4.png) \
![topic-5](20-topic-5.png) 

15. Удалить коннектор
```bash
curl -X DELETE http://localhost:8083/connectors/test-data-connector
```
![remove-connector](21-remove-connector.png)

16. Остановить контейнеры и выполнить очистку
```bash
docker compose stop
```
```bash
docker container prune -f
```
```bash
docker volume prune -f
```